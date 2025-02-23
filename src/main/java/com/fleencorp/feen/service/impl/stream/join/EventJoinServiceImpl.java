package com.fleencorp.feen.service.impl.stream.join;

import com.fleencorp.feen.constant.stream.StreamType;
import com.fleencorp.feen.constant.stream.attendee.StreamAttendeeRequestToJoinStatus;
import com.fleencorp.feen.exception.base.FailedOperationException;
import com.fleencorp.feen.exception.calendar.CalendarNotFoundException;
import com.fleencorp.feen.exception.stream.*;
import com.fleencorp.feen.mapper.CommonMapper;
import com.fleencorp.feen.mapper.stream.StreamMapper;
import com.fleencorp.feen.model.domain.calendar.Calendar;
import com.fleencorp.feen.model.domain.notification.Notification;
import com.fleencorp.feen.model.domain.stream.FleenStream;
import com.fleencorp.feen.model.domain.stream.StreamAttendee;
import com.fleencorp.feen.model.domain.user.Member;
import com.fleencorp.feen.model.dto.event.AddNewStreamAttendeeDto;
import com.fleencorp.feen.model.dto.stream.attendance.JoinStreamDto;
import com.fleencorp.feen.model.dto.stream.attendance.NotAttendingStreamDto;
import com.fleencorp.feen.model.dto.stream.attendance.ProcessAttendeeRequestToJoinStreamDto;
import com.fleencorp.feen.model.dto.stream.attendance.RequestToJoinStreamDto;
import com.fleencorp.feen.model.info.stream.StreamTypeInfo;
import com.fleencorp.feen.model.info.stream.attendance.AttendanceInfo;
import com.fleencorp.feen.model.request.calendar.event.AddNewEventAttendeeRequest;
import com.fleencorp.feen.model.request.calendar.event.NotAttendingEventRequest;
import com.fleencorp.feen.model.request.stream.ExternalStreamRequest;
import com.fleencorp.feen.model.response.holder.TryToJoinPrivateOrProtectedStreamResponse;
import com.fleencorp.feen.model.response.holder.TryToJoinPublicStreamResponse;
import com.fleencorp.feen.model.response.stream.FleenStreamResponse;
import com.fleencorp.feen.model.response.stream.attendance.JoinStreamResponse;
import com.fleencorp.feen.model.response.stream.attendance.NotAttendingStreamResponse;
import com.fleencorp.feen.model.response.stream.attendance.ProcessAttendeeRequestToJoinStreamResponse;
import com.fleencorp.feen.model.response.stream.attendance.RequestToJoinStreamResponse;
import com.fleencorp.feen.model.response.stream.common.AddNewStreamAttendeeResponse;
import com.fleencorp.feen.model.security.FleenUser;
import com.fleencorp.feen.repository.stream.FleenStreamRepository;
import com.fleencorp.feen.repository.stream.StreamAttendeeRepository;
import com.fleencorp.feen.repository.user.MemberRepository;
import com.fleencorp.feen.service.chat.space.member.ChatSpaceMemberService;
import com.fleencorp.feen.service.common.MiscService;
import com.fleencorp.feen.service.impl.notification.NotificationMessageService;
import com.fleencorp.feen.service.notification.NotificationService;
import com.fleencorp.feen.service.stream.attendee.StreamAttendeeService;
import com.fleencorp.feen.service.stream.common.StreamService;
import com.fleencorp.feen.service.stream.join.EventJoinService;
import com.fleencorp.feen.service.stream.update.AttendeeUpdateService;
import com.fleencorp.feen.service.stream.update.EventUpdateService;
import com.fleencorp.feen.service.stream.update.OtherEventUpdateService;
import com.fleencorp.localizer.service.Localizer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.fleencorp.feen.service.common.CommonService.verifyIfUserIsAuthorOrCreatorOrOwnerTryingToPerformAction;
import static com.fleencorp.feen.service.impl.stream.base.StreamServiceImpl.verifyStreamDetails;

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
  private final AttendeeUpdateService attendeeUpdateService;
  private final EventUpdateService eventUpdateService;
  private final OtherEventUpdateService otherEventUpdateService;
  private final NotificationMessageService notificationMessageService;
  private final NotificationService notificationService;
  private final FleenStreamRepository streamRepository;
  private final StreamAttendeeRepository streamAttendeeRepository;
  private final CommonMapper commonMapper;
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
   * @param attendeeUpdateService      the service for handling attendee updates
   * @param eventUpdateService         the service for updating event details
   * @param otherEventUpdateService    the service for handling other types of event updates
   * @param notificationMessageService the service for composing notification messages
   * @param notificationService        the service for sending notifications
   * @param memberRepository           the repository for accessing member data
   * @param streamRepository           the repository for accessing stream data
   * @param streamAttendeeRepository   the repository for accessing stream attendee data
   * @param localizer                  the service for handling localization of messages
   * @param commonMapper               the mapper for transforming common data models
   * @param streamMapper               the mapper for transforming stream-related data models
   */
  public EventJoinServiceImpl(
      final ChatSpaceMemberService chatSpaceMemberService,
      final MiscService miscService,
      final StreamAttendeeService attendeeService,
      final StreamService streamService,
      final AttendeeUpdateService attendeeUpdateService,
      final EventUpdateService eventUpdateService,
      final OtherEventUpdateService otherEventUpdateService,
      final NotificationMessageService notificationMessageService,
      final NotificationService notificationService,
      final FleenStreamRepository streamRepository,
      final MemberRepository memberRepository,
      final StreamAttendeeRepository streamAttendeeRepository,
      final Localizer localizer,
      final CommonMapper commonMapper,
      final StreamMapper streamMapper) {
    this.chatSpaceMemberService = chatSpaceMemberService;
    this.miscService = miscService;
    this.attendeeService = attendeeService;
    this.streamService = streamService;
    this.attendeeUpdateService = attendeeUpdateService;
    this.eventUpdateService = eventUpdateService;
    this.otherEventUpdateService = otherEventUpdateService;
    this.notificationMessageService = notificationMessageService;
    this.notificationService = notificationService;
    this.memberRepository = memberRepository;
    this.streamRepository = streamRepository;
    this.streamAttendeeRepository = streamAttendeeRepository;
    this.commonMapper = commonMapper;
    this.streamMapper = streamMapper;
    this.localizer = localizer;
  }

  /**
   * Marks the user as not attending a specified event and updates their attendance status.
   *
   * <p>This method retrieves the event by its ID, checks the user's ownership or attendance status,
   * and updates their status to "not attending" if applicable. It also decreases the total number of attendees
   * for the event and updates the associated external services (e.g., Google Calendar) to reflect the user's
   * non-attendance. The method returns a localized response indicating the successful update.</p>
   *
   * <p>It may throw exceptions if the event is not found, if no calendar is associated with the user's country,
   * or if the operation fails for some reason.</p>
   *
   * @param eventId the ID of the event the user is opting out of
   * @param notAttendingStreamDto the DTO containing stream-related details for the non-attendance action
   * @param user the user opting out of the event
   * @return a {@link NotAttendingStreamResponse} containing details about the updated non-attendance status
   * @throws FleenStreamNotFoundException if the event with the given ID cannot be found
   * @throws CalendarNotFoundException if no calendar is found for the user's country and stream type
   * @throws FailedOperationException if the operation fails due to external service issues or other reasons
   */

  @Override
  @Transactional
  public NotAttendingStreamResponse notAttendingEvent(final Long eventId, final NotAttendingStreamDto notAttendingStreamDto, final FleenUser user)
      throws FleenStreamNotFoundException, CalendarNotFoundException, FailedOperationException {
    // Find the stream by its ID
    final FleenStream stream = streamService.findStream(eventId);
    // Retrieve the stream type
    final StreamType streamType = stream.getStreamType();
    // Verify if the stream's type is the same as the stream type of the request
    stream.verifyIfStreamTypeNotEqualAndFail(notAttendingStreamDto.getStreamType());
    // Find the calendar associated with the user's country
    final Calendar calendar = miscService.findCalendar(user.getCountry(), notAttendingStreamDto.getStreamType());
    // Verify if the user is the owner and fail the operation because the owner is automatically a member of the event
    verifyIfUserIsAuthorOrCreatorOrOwnerTryingToPerformAction(Member.of(stream.getMemberId()), user);

    // Find the existing attendee record for the user and event
    streamAttendeeRepository.findAttendeeByStreamAndUser(stream, user.toMember())
      .ifPresent(streamAttendee -> {
        // If an attendee record exists, update their attendance status to false
        streamAttendee.markAsNotAttending();
        // Decrease the total number of attendees to stream
        streamService.decreaseTotalAttendeesOrGuestsAndSave(stream);
        // Save the updated attendee record
        streamAttendeeRepository.save(streamAttendee);
        // Create a request that remove the attendee from the Google Calendar event
        final ExternalStreamRequest notAttendingStreamRequest = ExternalStreamRequest.ofNotAttending(calendar, stream, user.getEmailAddress(), streamType);
        // Send the request for the update of non-attendance
        notAttendingStreamExternally(notAttendingStreamRequest);
    });

    final NotAttendingStreamResponse notAttendingStreamResponse = commonMapper.notAttendingStream();
    // Retrieve the stream type info
    final StreamTypeInfo streamTypeInfo = streamMapper.toStreamTypeInfo(stream.getStreamType());
    // Set the stream type info
    notAttendingStreamResponse.setStreamTypeInfo(streamTypeInfo);
    // Return a localized response
    return localizer.of(notAttendingStreamResponse);
  }

  /**
   * Handles the external operation for marking a user as not attending an event.
   *
   * <p>This method checks if the stream is an event, and if so, it creates a request to remove the user
   * from the associated external service event. It then sends the non-attendance update to the external
   * event service to complete the operation.</p>
   *
   * @param notAttendingStreamRequest the request containing details for the non-attendance update
   */
  protected void notAttendingStreamExternally(final ExternalStreamRequest notAttendingStreamRequest) {
    if (notAttendingStreamRequest.isAnEvent() && notAttendingStreamRequest.isNotAttendingRequest()) {
      // Create a request that remove the attendee from the external service
      final NotAttendingEventRequest notAttendingEventRequest = NotAttendingEventRequest.of(
        notAttendingStreamRequest.calendarExternalId(),
        notAttendingStreamRequest.streamExternalId(),
        notAttendingStreamRequest.getAttendeeEmailAddress()
      );
      // Send the request for the update of non-attendance
      eventUpdateService.notAttendingEvent(notAttendingEventRequest);
    }
  }

  /**
   * Attempts to join a stream event and sends an invitation to the attendee.
   *
   * <p>This method finds the calendar associated with the user's country, verifies the user's details,
   * and attempts to join the specified event. If successful, it creates an invitation for the attendee
   * and returns a localized response containing the event join status and stream type information.</p>
   *
   * <p>This method may throw exceptions if the stream is not found, already canceled or happened,
   * or if the user cannot join the stream without approval.</p>
   *
   * @param eventId the ID of the event to join
   * @param joinStreamDto the data transfer object containing additional join details
   * @param user the user attempting to join the event
   * @return a {@link JoinStreamResponse} with details of the join event operation
   * @throws CalendarNotFoundException if no calendar is found for the user's country
   * @throws FleenStreamNotFoundException if the stream with the given ID is not found
   * @throws StreamAlreadyCanceledException if the stream has been canceled
   * @throws StreamAlreadyHappenedException if the stream has already occurred
   * @throws CannotJointStreamWithoutApprovalException if the user requires approval to join the stream
   * @throws AlreadyRequestedToJoinStreamException if the user has already requested to join the stream
   * @throws AlreadyApprovedRequestToJoinException if the user is already approved to join the stream
   */
  @Override
  @Transactional
  public JoinStreamResponse joinEvent(final Long eventId, final JoinStreamDto joinStreamDto, final FleenUser user)
      throws CalendarNotFoundException, FleenStreamNotFoundException, StreamAlreadyCanceledException, StreamAlreadyHappenedException,
        CannotJointStreamWithoutApprovalException, AlreadyRequestedToJoinStreamException, AlreadyApprovedRequestToJoinException {
    // Find the calendar associated with the user's country
    final Calendar calendar = miscService.findCalendar(user.getCountry());
    // Verify the user details and attempt to join the event
    final TryToJoinPublicStreamResponse tryToJoinResponse = streamService.tryToJoinPublicStream(eventId, joinStreamDto.getComment(), user);
    // Extract the stream
    final FleenStream stream = tryToJoinResponse.stream();
    // Verify if the stream's type is the same as the stream type of the request
    stream.verifyIfStreamTypeNotEqualAndFail(joinStreamDto.getStreamType());
    // Extract the attendance info
    final AttendanceInfo attendanceInfo = tryToJoinResponse.attendanceInfo();
    // Get stream type info
    final StreamTypeInfo streamTypeInfo = streamMapper.toStreamTypeInfo(stream.getStreamType());
    // Send invitation to new attendee
    attendeeUpdateService.createNewEventAttendeeRequestAndSendInvitation(calendar.getExternalId(), stream.getExternalId(), user.getEmailAddress(), joinStreamDto.getComment());
    // Return localized response of the join event including status
    return localizer.of(JoinStreamResponse.of(eventId, attendanceInfo, streamTypeInfo, stream.getTotalAttendees()));
  }

  /**
   * Handles a user's request to join a private or protected event.
   *
   * <p>This method processes the user's request to join a specific event (stream) by verifying the user's
   * details and attempting to join the event. It retrieves the necessary information such as the stream,
   * the user's attendance status, and stream type information. Notifications for the join request are
   * sent and saved, and chat space membership or invitations are handled if applicable.</p>
   *
   * @param eventId the ID of the event (stream) the user is requesting to join
   * @param requestToJoinStreamDto the {@link RequestToJoinStreamDto} containing the join request details (e.g., comment)
   * @param user the {@link FleenUser} attempting to join the event
   * @return a {@link RequestToJoinStreamResponse} containing the localized response of the request,
   *         including the stream ID, attendance information, and stream type details
   */
  @Override
  @Transactional
  public RequestToJoinStreamResponse requestToJoinEvent(final Long eventId, final RequestToJoinStreamDto requestToJoinStreamDto, final FleenUser user) {
    // Verify the user details and attempt to join the event
    final RequestToJoinStreamResponse requestToJoinStreamResponse = streamService.requestToJoinStream(eventId, requestToJoinStreamDto, user);
    // Verify the user details and attempt to join the event
    final TryToJoinPrivateOrProtectedStreamResponse tryToJoinResponse = requestToJoinStreamResponse.getTryToJoinResponse();
    // Extract the stream
    final FleenStream stream = tryToJoinResponse.stream();
    // Verify if the stream's type is the same as the stream type of the request
    stream.verifyIfStreamTypeNotEqualAndFail(requestToJoinStreamDto.getStreamType());
    // Extract the attendee
    final StreamAttendee streamAttendee = tryToJoinResponse.attendee();
    // Check and handle chat space membership and invitation
    handleJoinRequestForPrivateStreamBasedOnChatSpaceMembership(stream, streamAttendee, requestToJoinStreamDto.getComment(), user);
    // Return the request to join response
    return requestToJoinStreamResponse;
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
  protected void handleJoinRequestForPrivateStreamBasedOnChatSpaceMembership(final FleenStream stream, final StreamAttendee streamAttendee, final String comment, final FleenUser user) {
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
   * Processes an attendee's request to join an event and handles any associated operations.
   *
   * <p>This method retrieves the event (stream) by its ID, verifies the stream's details (such as ownership,
   * event date, and active status), and checks if the user is already an attendee. If the attendee is found,
   * their request to join the event is processed. A localized response is returned with the processed
   * request details, including any relevant stream information.</p>
   *
   * @param eventId the ID of the stream (event) the attendee wants to join
   * @param processAttendeeRequestToJoinStreamDto contains details of the attendee's join request
   * @param user the user making the request to join
   * @return a {@link ProcessAttendeeRequestToJoinStreamResponse} containing the result of the join request
   * @throws FleenStreamNotFoundException if the stream with the given ID is not found
   * @throws StreamNotCreatedByUserException if the stream was not created by the provided user
   * @throws StreamAlreadyHappenedException if the stream has already occurred and cannot be joined
   * @throws StreamAlreadyCanceledException if the stream has been canceled and cannot be joined
   * @throws FailedOperationException if the join request processing fails
   */
  @Override
  @Transactional
  public ProcessAttendeeRequestToJoinStreamResponse processAttendeeRequestToJoinEvent(final Long eventId, final ProcessAttendeeRequestToJoinStreamDto processAttendeeRequestToJoinStreamDto, final FleenUser user)
    throws FleenStreamNotFoundException, StreamNotCreatedByUserException, StreamAlreadyHappenedException,
      StreamAlreadyCanceledException, FailedOperationException {
    // Retrieve the event (FleenStream) using the event ID
    final FleenStream stream = streamService.findStream(eventId);
    // Verify if the stream's type is the same as the stream type of the request
    stream.verifyIfStreamTypeNotEqualAndFail(processAttendeeRequestToJoinStreamDto.getStreamType());
    // Verify stream details like the owner, event date and active status of the event
    verifyStreamDetails(stream, user);

    // Check if the user is already an attendee of the stream and process accordingly
    final Optional<StreamAttendee> existingAttendee = attendeeService.findAttendee(stream, processAttendeeRequestToJoinStreamDto.getAttendeeId());
    // Process the request to join the event if the attendee exists
    existingAttendee.ifPresent(streamAttendee -> processAttendeeRequestToJoin(stream, streamAttendee, processAttendeeRequestToJoinStreamDto, user));
    // Convert the stream to response
    final FleenStreamResponse streamResponse = streamMapper.toStreamResponse(stream);
    // Get a processed attendee request to join event response
    final ProcessAttendeeRequestToJoinStreamResponse processedRequestToJoin = commonMapper.processAttendeeRequestToJoinStream(streamResponse, existingAttendee);
    // Return a localized response with the processed stream details
    return localizer.of(processedRequestToJoin);
  }

  /**
   * Processes an attendee's request to join a stream (event) and updates the request status accordingly.
   *
   * <p>This method checks whether the attendee's request is still pending. If so, it updates the request status
   * and sets any comments provided by the organizer. If the request is approved, the necessary calendar invitation
   * is handled. Additionally, a notification is created and saved to notify the attendee of the approval or
   * disapproval of their request.</p>
   *
   * @param stream the event (stream) the attendee is requesting to join
   * @param attendee the attendee whose request is being processed
   * @param processRequestToJoinDto contains details of the attendee's join request, including the requested status and comments
   * @param user the user processing the attendee's request to join
   */
  protected void processAttendeeRequestToJoin(final FleenStream stream, final StreamAttendee attendee, final ProcessAttendeeRequestToJoinStreamDto processRequestToJoinDto,
      final FleenUser user) {
    // Check if the attendee's request is still pending
    if (attendee.isRequestToJoinNotPending()) {
      return;
    }

    // Get the requested status for joining the event
    final StreamAttendeeRequestToJoinStatus joinStatus = processRequestToJoinDto.getJoinStatus();
    // Update the attendee's request status and set any organizer comments
    attendee.updateRequestStatusAndSetOrganizerComment(joinStatus, processRequestToJoinDto.getComment());
    // Check if the request is approved and handle the approval
    checkIfRequestToJoinApprovedAndHandleApproval(processRequestToJoinDto, stream, attendee, user);

    // Create and save notification
    final Notification notification = notificationMessageService.ofApprovedOrDisapproved(stream, attendee, stream.getMember());
    notificationService.save(notification);
  }

  /**
   * Checks if the request to join has been approved and handles the approval process.
   *
   * <p>This method verifies if the request to join a event or stream has been approved. If the request is approved,
   * it proceeds to handle the approval by adding the attendee to the event through {@code handleApprovedRequest}.</p>
   *
   * @param processAttendeeRequestToJoinStreamDto the data transfer object containing the request details, including approval status and comments
   * @param stream The event or stream that the attendee is requesting to join.
   * @param attendee The stream attendee whose request is being evaluated.
   * @param user The user responsible for handling the request.
   */
  protected void checkIfRequestToJoinApprovedAndHandleApproval(final ProcessAttendeeRequestToJoinStreamDto processAttendeeRequestToJoinStreamDto, final FleenStream stream, final StreamAttendee attendee, final FleenUser user) {
    // If the request is approved, proceed with handling the approval
    if (processAttendeeRequestToJoinStreamDto.isApproved()) {
      // Retrieve the calendar based on the user's country
      final Calendar calendar = miscService.findCalendar(user.getCountry());
      // Increase the total number of attendees to stream
      streamService.increaseTotalAttendeesOrGuestsAndSave(stream);
      // Save the attendee details in the repository
      streamAttendeeRepository.save(attendee);
      // Add the attendee to the event associated with the stream
      addAttendeeToEvent(calendar.getExternalId(), stream.getExternalId(), attendee.getEmailAddress(), null);
    }
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
   * @throws FleenStreamNotFoundException if the stream with the given ID is not found
   * @throws CalendarNotFoundException if the calendar associated with the user's country is not found
   * @throws StreamNotCreatedByUserException if the stream was not created by the current user
   * @throws StreamAlreadyHappenedException if the event has already occurred
   * @throws StreamAlreadyCanceledException if the event has been canceled
   * @throws FailedOperationException if the operation fails for other reasons
   */
  @Override
  @Transactional
  public AddNewStreamAttendeeResponse addEventAttendee(final Long eventId, final AddNewStreamAttendeeDto addNewAttendeeDto, final FleenUser user)
      throws FleenStreamNotFoundException, CalendarNotFoundException, StreamNotCreatedByUserException,
        StreamAlreadyHappenedException, StreamAlreadyCanceledException, FailedOperationException {
    // Find the stream by its ID
    final FleenStream stream = streamService.findStream(eventId);
    // Verify if the stream's type is the same as the stream type of the request
    stream.verifyIfStreamTypeNotEqualAndFail(addNewAttendeeDto.getStreamType());
    // Find the calendar associated with the user's country
    final Calendar calendar = miscService.findCalendar(user.getCountry());

    // Verify stream details like the owner, event date and active status of the event
    verifyStreamDetails(stream, user);
    // Handle the request to add the attendee to the event
    handleAttendeeRequest(calendar, stream, addNewAttendeeDto);

    // Save stream to the repository
    streamRepository.save(stream);
    // Return a localized response with the details of the added attendee
    return localizer.of(AddNewStreamAttendeeResponse.of(eventId, addNewAttendeeDto.getEmailAddress(), streamMapper.toFleenStreamResponseNoJoinStatus(stream)));
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
    if (isStreamAttendeeRequestPendingOrDisapproved(streamAttendee)) {
      // Approve the attendee request to join
      streamAttendee.approvedByOrganizer(addNewAttendeeDto.getComment());
      // Add the attendee to the event using the external service
      addAttendeeToEvent(calendar.getExternalId(), stream.getExternalId(), addNewAttendeeDto.getEmailAddress(), addNewAttendeeDto.getAliasOrDisplayName());
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
    addAttendeeToEvent(calendar.getExternalId(), stream.getExternalId(), addNewStreamAttendeeDto.getEmailAddress(), addNewStreamAttendeeDto.getAliasOrDisplayName());
  }

  /**
   * Checks if the request status of a given {@link StreamAttendee} is either pending or disapproved.
   *
   * <p>This method evaluates the request-to-join status of a {@link StreamAttendee} and returns true if the status
   * is either {@code PENDING} or {@code DISAPPROVED}. It can be used to determine if further action is needed
   * for the given attendee's request.</p>
   *
   * @param streamAttendee the {@link StreamAttendee} whose request status is to be checked.
   * @return {@code true} if the request status is {@code PENDING} or {@code DISAPPROVED}, {@code false} otherwise.
   */
  protected boolean isStreamAttendeeRequestPendingOrDisapproved(final StreamAttendee streamAttendee) {
    return streamAttendee.isRequestToJoinPending() || streamAttendee.isRequestToJoinDisapproved();
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
  public void addAttendeeToEvent(final String calendarExternalId, final String streamExternalId, final String attendeeEmailAddress, final String displayOrAliasName) {
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