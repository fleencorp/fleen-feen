package com.fleencorp.feen.service.impl.stream.join;

import com.fleencorp.feen.exception.base.FailedOperationException;
import com.fleencorp.feen.exception.calendar.CalendarNotFoundException;
import com.fleencorp.feen.exception.stream.StreamNotFoundException;
import com.fleencorp.feen.exception.stream.core.StreamAlreadyCanceledException;
import com.fleencorp.feen.exception.stream.core.StreamAlreadyHappenedException;
import com.fleencorp.feen.exception.stream.core.StreamNotCreatedByUserException;
import com.fleencorp.feen.mapper.stream.StreamMapper;
import com.fleencorp.feen.model.domain.calendar.Calendar;
import com.fleencorp.feen.model.domain.stream.FleenStream;
import com.fleencorp.feen.model.domain.stream.StreamAttendee;
import com.fleencorp.feen.model.dto.event.AddNewStreamAttendeeDto;
import com.fleencorp.feen.model.request.calendar.event.AddNewEventAttendeeRequest;
import com.fleencorp.feen.model.response.stream.common.AddNewStreamAttendeeResponse;
import com.fleencorp.feen.model.security.FleenUser;
import com.fleencorp.feen.repository.stream.StreamAttendeeRepository;
import com.fleencorp.feen.repository.stream.StreamRepository;
import com.fleencorp.feen.repository.user.MemberRepository;
import com.fleencorp.feen.service.chat.space.member.ChatSpaceMemberService;
import com.fleencorp.feen.service.common.MiscService;
import com.fleencorp.feen.service.stream.attendee.StreamAttendeeService;
import com.fleencorp.feen.service.stream.common.StreamService;
import com.fleencorp.feen.service.stream.join.EventJoinService;
import com.fleencorp.feen.service.stream.update.OtherEventUpdateService;
import com.fleencorp.localizer.service.Localizer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.fleencorp.feen.service.impl.stream.common.StreamServiceImpl.verifyStreamDetails;

/**
 * Implementation of the {@link EventJoinService} interface that handles the logic for managing attendee participation in events.
 *
 * <p>This service is responsible for processing requests from users to join events, handling attendee statuses,
 * and ensuring that attendees are correctly registered and notified about event details. It interacts with other services
 * such as the event and attendee services to ensure smooth management of the event lifecycle and attendee records.</p>
 *
 * @author Yusuf Àlàmù Musa
 * @version 1.0
 */
@Slf4j
@Service
public class EventJoinServiceImpl implements EventJoinService {

  private final ChatSpaceMemberService chatSpaceMemberService;
  private final MiscService miscService;
  private final StreamAttendeeService attendeeService;
  private final StreamService streamService;
  private final OtherEventUpdateService otherEventUpdateService;
  private final StreamRepository streamRepository;
  private final StreamAttendeeRepository streamAttendeeRepository;
  private final StreamMapper streamMapper;
  private final MemberRepository memberRepository;
  private final Localizer localizer;

  /**
   * Constructs a new instance of {@code EventJoinServiceImpl} with the specified dependencies.
   *
   * <p>This constructor initializes the service with all required components for handling event join operations,
   * including member and attendee services, stream management, notifications, event updates, and repositories for
   * accessing event and attendee data. It also integrates with localization and mapping services for internationalization
   * and data transformation.</p>
   *
   * @param chatSpaceMemberService     the service for managing chat space members
   * @param miscService                the service for miscellaneous operations
   * @param attendeeService            the service for managing event attendees
   * @param streamService              the service for stream-related operations
   * @param otherEventUpdateService    the service for handling other types of event updates
   * @param memberRepository           the repository for accessing member data
   * @param streamRepository           the repository for accessing stream data
   * @param streamAttendeeRepository   the repository for accessing stream attendee data
   * @param localizer                  the service for handling localization of messages
   * @param streamMapper               the mapper for transforming stream-related data models
   */
  public EventJoinServiceImpl(
      final ChatSpaceMemberService chatSpaceMemberService,
      final MiscService miscService,
      final StreamAttendeeService attendeeService,
      final StreamService streamService,
      final OtherEventUpdateService otherEventUpdateService,
      final StreamRepository streamRepository,
      final MemberRepository memberRepository,
      final StreamAttendeeRepository streamAttendeeRepository,
      final Localizer localizer,
      final StreamMapper streamMapper) {
    this.chatSpaceMemberService = chatSpaceMemberService;
    this.miscService = miscService;
    this.attendeeService = attendeeService;
    this.streamService = streamService;
    this.otherEventUpdateService = otherEventUpdateService;
    this.memberRepository = memberRepository;
    this.streamRepository = streamRepository;
    this.streamAttendeeRepository = streamAttendeeRepository;
    this.streamMapper = streamMapper;
    this.localizer = localizer;
  }

  /**
   * Handles the join request for a private stream based on the attendee's membership in a chat space.
   *
   * <p>This method checks if the stream has an associated chat space and whether the attendee is a member
   * of that chat space. If the attendee is a member, their join request is approved, and their details are
   * saved. Additionally, an invitation is sent based on their membership status.</p>
   *
   * @param stream the stream the user is attempting to join
   * @param streamAttendee the attendee attempting to join the stream
   * @param comment any comment provided by the attendee with their join request
   * @param user the user making the join request
   */
  @Override
  @Transactional
  public void handleJoinRequestForPrivateStreamBasedOnChatSpaceMembership(final FleenStream stream, final StreamAttendee streamAttendee, final String comment, final FleenUser user) {
    // Check if the attendee is a member of a chat space if there is one associated with the stream and approve their join request
    final boolean isMemberPartOfChatSpace = chatSpaceMemberService.checkIfStreamHasChatSpaceAndAttendeeIsAMemberOfChatSpace(stream, streamAttendee);

    if (isMemberPartOfChatSpace) {
      // Approve attendee request to join stream if its a member of the chat space and save
      streamAttendee.approveUserAttendance();
      // Save the attendee details
      streamAttendeeRepository.save(streamAttendee);
    }
    // Verify if the attendee is a member of the chat space and send invitation
    attendeeService.checkIfAttendeeIsMemberOfChatSpaceAndSendInvitationForJoinStreamRequest(isMemberPartOfChatSpace, stream.getExternalId(), comment, user);
  }

  /**
   * Adds a new attendee to the specified event (stream).
   *
   * <p>This method finds the stream by its ID, retrieves the calendar associated with the user's country,
   * verifies the stream details (such as owner and event status), and processes the request to add the
   * attendee to the event. After handling the request, the stream is saved in the repository, and a localized
   * response with the added attendee's details is returned.</p>
   *
   * @param eventId the ID of the event (stream) to which the attendee is being added
   * @param addNewAttendeeDto the DTO containing the attendee's email address and other relevant details
   * @param user the user initiating the addition of the attendee
   * @return a localized response containing the details of the added attendee
   * @throws StreamNotFoundException if the stream with the given ID is not found
   * @throws CalendarNotFoundException if the calendar associated with the user's country is not found
   * @throws StreamNotCreatedByUserException if the stream was not created by the current user
   * @throws StreamAlreadyHappenedException if the event has already occurred
   * @throws StreamAlreadyCanceledException if the event has been canceled
   * @throws FailedOperationException if the operation fails for other reasons
   */
  @Override
  @Transactional
  public AddNewStreamAttendeeResponse addEventAttendee(final Long eventId, final AddNewStreamAttendeeDto addNewAttendeeDto, final FleenUser user)
      throws StreamNotFoundException, CalendarNotFoundException, StreamNotCreatedByUserException,
        StreamAlreadyHappenedException, StreamAlreadyCanceledException, FailedOperationException {
    // Find the stream by its ID
    final FleenStream stream = streamService.findStream(eventId);
    // Verify if the stream's type is the same as the stream type of the request
    stream.checkStreamTypeNotEqual(addNewAttendeeDto.getStreamType());
    // Find the calendar associated with the user's country
    final Calendar calendar = miscService.findCalendar(user.getCountry());

    // Verify stream details like the owner, event date and active status of the event
    verifyStreamDetails(stream, user);
    // Handle the request to add the attendee to the event
    handleAttendeeRequest(calendar, stream, addNewAttendeeDto);

    // Save stream to the repository
    streamRepository.save(stream);
    // Return a localized response with the details of the added attendee
    return localizer.of(AddNewStreamAttendeeResponse.of(eventId, addNewAttendeeDto.getEmailAddress(), streamMapper.toStreamResponseNoJoinStatus(stream)));
  }

  /**
   * Handles a request to add a new attendee to a stream by checking if the attendee already exists.
   *
   * <p>This method searches for an existing attendee based on the provided email address. If an attendee
   * is found, it processes the existing attendee's request. If no attendee is found, it approves the
   * new attendee and sends an invitation to join the stream.</p>
   *
   * @param calendar The calendar associated with the stream where the attendee is to be added.
   * @param stream The stream for which the attendee request is being handled.
   * @param addNewStreamAttendeeDto The DTO containing the information of the attendee to be added.
   */
  protected void handleAttendeeRequest(final Calendar calendar, final FleenStream stream, final AddNewStreamAttendeeDto addNewStreamAttendeeDto) {
    // Find an existing stream attendee by their email address
    streamAttendeeRepository.findDistinctByEmail(addNewStreamAttendeeDto.getEmailAddress())
      .ifPresentOrElse(
        // If an existing attendee is found, process their request
        streamAttendee -> processExistingAttendee(calendar, stream, streamAttendee, addNewStreamAttendeeDto),
        // If no existing attendee is found, approve and add the new attendee to the stream
        () -> approveAndAddAttendeeToStreamAttendeesAndSendInvitation(calendar, stream, addNewStreamAttendeeDto)
      );
  }

  /**
   * Processes an existing attendee for the specified stream by approving their request and adding them to the stream.
   *
   * <p>This method checks if the request status of the existing attendee is either pending or disapproved.
   * If so, it approves the attendee's request, sets the organizer's comment, and adds the attendee to the
   * event using the external service. Finally, it saves the updated attendee information to the repository.</p>
   *
   * @param calendar The calendar associated with the stream where the attendee is to be added.
   * @param stream The stream that the attendee is associated with.
   * @param streamAttendee The existing stream attendee whose request is being processed.
   * @param addNewAttendeeDto The DTO containing the information of the attendee to be added.
   */
  protected void processExistingAttendee(final Calendar calendar, final FleenStream stream, final StreamAttendee streamAttendee, final AddNewStreamAttendeeDto addNewAttendeeDto) {
      // Check if the stream attendee's request is pending or disapproved
    if (streamAttendee.isRequestToJoinDisapprovedOrPending()) {
      // Approve the attendee request to join
      streamAttendee.approvedByOrganizer(addNewAttendeeDto.getComment());
      // Add the attendee to the event using the external service
      addAttendeeToEventExternally(calendar.getExternalId(), stream.getExternalId(), addNewAttendeeDto.getEmailAddress(), addNewAttendeeDto.getAliasOrDisplayName());
      // Save the updated stream attendee information to the repository
      streamAttendeeRepository.save(streamAttendee);
    }
  }

  /**
   * Approves an attendee's request and adds the attendee to the specified stream.
   *
   * <p>This method checks if a member with the given email address already exists. If the member exists,
   * it creates a new {@link StreamAttendee} for the given {@link FleenStream} and approves the attendee's
   * request by setting the request status to {@code APPROVED} and adding the organizer's comment. The
   * new attendee is then saved to the repository.</p>
   *
   * @param stream the {@link FleenStream} to which the attendee is to be added.
   * @param addNewStreamAttendeeDto the DTO containing the new attendee's email address and the organizer's comment.
   */
  protected void approveAndAddAttendeeToStreamAttendees(final FleenStream stream, final AddNewStreamAttendeeDto addNewStreamAttendeeDto) {
    // Check if the member with the given email address already exists in the repository
    memberRepository.findByEmailAddress(addNewStreamAttendeeDto.getEmailAddress())
      .ifPresent(member -> {
        // If the member exists, proceed to create and approve the attendee
        final StreamAttendee streamAttendee = attendeeService.getExistingOrCreateNewStreamAttendee(stream, null, FleenUser.of(member.getMemberId()));
        // Approve the attendee request by the organizer
        streamAttendee.approvedByOrganizer(addNewStreamAttendeeDto.getComment());
        // Save the attendee and changes
        streamAttendeeRepository.save(streamAttendee);
    });
  }

  /**
   * Approves the attendee and adds them to the stream's attendees, then sends an invitation.
   *
   * <p>This method first approves the attendee and adds them to the list of attendees for the specified
   * {@code FleenStream}. After the attendee is successfully added, it sends an invitation by adding them
   * to the corresponding event using an external service.</p>
   *
   * @param calendar The calendar associated with the stream where the attendee is to be added.
   * @param stream The stream to which the attendee is being added.
   * @param addNewStreamAttendeeDto The DTO containing the information of the attendee to be added.
   */
  protected void approveAndAddAttendeeToStreamAttendeesAndSendInvitation(final Calendar calendar, final FleenStream stream, final AddNewStreamAttendeeDto addNewStreamAttendeeDto) {
    // Approve the attendee and add them to the stream's attendees list
    approveAndAddAttendeeToStreamAttendees(stream, addNewStreamAttendeeDto);
    // Add attendee to the event using an external service
    addAttendeeToEventExternally(calendar.getExternalId(), stream.getExternalId(), addNewStreamAttendeeDto.getEmailAddress(), addNewStreamAttendeeDto.getAliasOrDisplayName());
  }

  /**
   * Adds an attendee to an event in the specified calendar using the Google Calendar service.
   *
   * <p>This method constructs an {@link AddNewEventAttendeeRequest} using the provided calendar external ID,
   * stream external ID, and attendee email address. It then uses the Google Calendar service to add the
   * attendee to the event and logs the response.</p>
   *
   * @param calendarExternalId the external ID of the calendar
   * @param streamExternalId the external ID of the stream (event)
   * @param attendeeEmailAddress the email address of the attendee to be added
   * @param displayOrAliasName the alias to use for the attendee which is optional
   */
  @Override
  public void addAttendeeToEventExternally(final String calendarExternalId, final String streamExternalId, final String attendeeEmailAddress, final String displayOrAliasName) {
    // Create a request to add the user as an attendee to the calendar event
    final AddNewEventAttendeeRequest addNewEventAttendeeRequest = AddNewEventAttendeeRequest.of(
      calendarExternalId,
      streamExternalId,
      attendeeEmailAddress,
      displayOrAliasName
    );

    otherEventUpdateService.addNewAttendeeToCalendarEvent(addNewEventAttendeeRequest);
  }

}