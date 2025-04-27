package com.fleencorp.feen.service.impl.stream.common;

import com.fleencorp.feen.constant.stream.StreamType;
import com.fleencorp.feen.exception.base.FailedOperationException;
import com.fleencorp.feen.exception.calendar.CalendarNotFoundException;
import com.fleencorp.feen.exception.google.oauth2.Oauth2InvalidAuthorizationException;
import com.fleencorp.feen.exception.stream.FleenStreamNotFoundException;
import com.fleencorp.feen.exception.stream.attendee.StreamAttendeeNotFoundException;
import com.fleencorp.feen.exception.stream.core.StreamAlreadyCanceledException;
import com.fleencorp.feen.exception.stream.core.StreamAlreadyHappenedException;
import com.fleencorp.feen.exception.stream.core.StreamNotCreatedByUserException;
import com.fleencorp.feen.exception.stream.join.request.AlreadyApprovedRequestToJoinException;
import com.fleencorp.feen.exception.stream.join.request.AlreadyRequestedToJoinStreamException;
import com.fleencorp.feen.exception.stream.join.request.CannotJoinPrivateStreamWithoutApprovalException;
import com.fleencorp.feen.mapper.CommonMapper;
import com.fleencorp.feen.mapper.stream.StreamMapper;
import com.fleencorp.feen.mapper.stream.ToInfoMapper;
import com.fleencorp.feen.model.domain.auth.Oauth2Authorization;
import com.fleencorp.feen.model.domain.calendar.Calendar;
import com.fleencorp.feen.model.domain.notification.Notification;
import com.fleencorp.feen.model.domain.stream.FleenStream;
import com.fleencorp.feen.model.domain.stream.StreamAttendee;
import com.fleencorp.feen.model.dto.stream.attendance.JoinStreamDto;
import com.fleencorp.feen.model.dto.stream.attendance.NotAttendingStreamDto;
import com.fleencorp.feen.model.dto.stream.attendance.ProcessAttendeeRequestToJoinStreamDto;
import com.fleencorp.feen.model.dto.stream.attendance.RequestToJoinStreamDto;
import com.fleencorp.feen.model.holder.StreamOtherDetailsHolder;
import com.fleencorp.feen.model.info.stream.StreamTypeInfo;
import com.fleencorp.feen.model.info.stream.attendance.AttendanceInfo;
import com.fleencorp.feen.model.request.calendar.event.AddNewEventAttendeeRequest;
import com.fleencorp.feen.model.request.calendar.event.NotAttendingEventRequest;
import com.fleencorp.feen.model.request.stream.ExternalStreamRequest;
import com.fleencorp.feen.model.response.stream.FleenStreamResponse;
import com.fleencorp.feen.model.response.stream.attendance.JoinStreamResponse;
import com.fleencorp.feen.model.response.stream.attendance.NotAttendingStreamResponse;
import com.fleencorp.feen.model.response.stream.attendance.ProcessAttendeeRequestToJoinStreamResponse;
import com.fleencorp.feen.model.response.stream.attendance.RequestToJoinStreamResponse;
import com.fleencorp.feen.model.security.FleenUser;
import com.fleencorp.feen.repository.stream.StreamAttendeeRepository;
import com.fleencorp.feen.repository.stream.StreamRepository;
import com.fleencorp.feen.service.impl.notification.NotificationMessageService;
import com.fleencorp.feen.service.notification.NotificationService;
import com.fleencorp.feen.service.stream.attendee.StreamAttendeeService;
import com.fleencorp.feen.service.stream.common.CommonStreamJoinService;
import com.fleencorp.feen.service.stream.common.StreamService;
import com.fleencorp.feen.service.stream.join.EventJoinService;
import com.fleencorp.feen.service.stream.update.EventUpdateService;
import com.fleencorp.feen.service.stream.update.OtherEventUpdateService;
import com.fleencorp.localizer.service.Localizer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.fleencorp.feen.service.impl.stream.common.StreamServiceImpl.*;
import static java.util.Objects.nonNull;

@Service
public class CommonStreamJoinServiceImpl implements CommonStreamJoinService {

  private final EventJoinService eventJoinService;
  private final EventUpdateService eventUpdateService;
  private final StreamAttendeeService attendeeService;
  private final StreamService streamService;
  private final NotificationService notificationService;
  private final NotificationMessageService notificationMessageService;
  private final OtherEventUpdateService otherEventUpdateService;
  private final StreamRepository streamRepository;
  private final StreamAttendeeRepository streamAttendeeRepository;
  private final CommonMapper commonMapper;
  private final StreamMapper streamMapper;
  private final ToInfoMapper toInfoMapper;
  private final Localizer localizer;

  /**
   * Constructs an instance of {@code CommonStreamJoinServiceImpl} and initializes its dependencies for
   * managing stream attendees, event updates, notifications, and localization services.
   *
   * <p>This service implementation handles the core logic for joining streams, updating events,
   * sending notifications, and localizing responses. It requires multiple services and repositories
   * to function correctly, which are injected via this constructor.</p>
   *
   * <p>Dependencies injected include services for handling attendee management, event joining and updates,
   * notification sending, and localization. Additionally, it includes repositories for accessing stream
   * and attendee data, as well as mappers for converting entities and data objects.</p>
   *
   * @param attendeeService           the service managing stream attendees
   * @param eventUpdateService        the service for updating events
   * @param eventJoinService          the service for joining events
   * @param notificationService       the service for sending notifications
   * @param notificationMessageService the service for managing notification messages
   * @param otherEventUpdateService   the service for updating external event systems
   * @param streamService             the service for managing streams
   * @param streamRepository          the repository for accessing stream data
   * @param streamAttendeeRepository  the repository for accessing stream attendee data
   * @param commonMapper              the mapper for common data transformations
   * @param streamMapper              the mapper for stream-specific data transformations
   * @param toInfoMapper              the mapper for mapping information of chat space, streams and attendee data
   * @param localizer                 the service for localizing responses
   */
  public CommonStreamJoinServiceImpl(
      final StreamAttendeeService attendeeService,
      final EventUpdateService eventUpdateService,
      final EventJoinService eventJoinService,
      final NotificationService notificationService,
      final NotificationMessageService notificationMessageService,
      final OtherEventUpdateService otherEventUpdateService,
      final StreamService streamService,
      final StreamRepository streamRepository,
      final StreamAttendeeRepository streamAttendeeRepository,
      final CommonMapper commonMapper,
      final StreamMapper streamMapper,
      final ToInfoMapper toInfoMapper,
      final Localizer localizer) {
    this.attendeeService = attendeeService;
    this.eventJoinService = eventJoinService;
    this.eventUpdateService = eventUpdateService;
    this.notificationService = notificationService;
    this.notificationMessageService = notificationMessageService;
    this.otherEventUpdateService = otherEventUpdateService;
    this.streamService = streamService;
    this.streamRepository = streamRepository;
    this.streamAttendeeRepository = streamAttendeeRepository;
    this.localizer = localizer;
    this.commonMapper = commonMapper;
    this.streamMapper = streamMapper;
    this.toInfoMapper = toInfoMapper;
  }

  /**
   * Processes an attendee's request to join a stream. This method handles the retrieval of the stream, validation of the stream's
   * details (such as type, owner, and stream status), and processes the attendee's request by either approving or disapproving it.
   * It also handles external operations, such as adding the attendee to a calendar or live strean via OAuth2 authorization if necessary.
   *
   * <p>The method begins by retrieving the stream based on the provided stream ID. It validates the stream's type against the type in
   * the request DTO and verifies the stream's ownership, stream status, and other key details. The attendee's request is then processed
   * based on the approval or disapproval status in the DTO.</p>
   *
   * <p>Once processed, the stream's other relevant details, such as calendar and OAuth2 authorization, are retrieved, and an external
   * stream request is created to handle any necessary interactions with external services, such as updating a calendar or a live stream.
   * The method also handles sending invitations to the attendee if the request is approved.</p>
   *
   * <p>Finally, the method returns a localized response with the processed stream and attendee details.</p>
   *
   * @param streamId                  the ID of the stream for which the attendee request is being processed
   * @param processRequestToJoinDto    the DTO containing details of the attendee's request to join the stream
   * @param user                      the user processing the attendee's request; must be the creator or an admin
   * @return                          a localized response with the details of the processed attendee join request
   * @throws FleenStreamNotFoundException          if the stream with the provided ID is not found
   * @throws CalendarNotFoundException             if the calendar associated with the stream is not found
   * @throws Oauth2InvalidAuthorizationException   if the OAuth2 authorization required for external services is invalid
   * @throws StreamNotCreatedByUserException       if the stream was not created by the current user
   * @throws StreamAlreadyHappenedException        if the stream has already occurred and cannot be modified
   * @throws StreamAlreadyCanceledException        if the stream has been canceled and cannot be modified
   * @throws FailedOperationException              if any external operation, such as adding the attendee, fails
   */
  @Override
  @Transactional
  public ProcessAttendeeRequestToJoinStreamResponse processAttendeeRequestToJoinStream(final Long streamId, final ProcessAttendeeRequestToJoinStreamDto processRequestToJoinDto, final FleenUser user)
      throws FleenStreamNotFoundException, CalendarNotFoundException, Oauth2InvalidAuthorizationException,
        StreamNotCreatedByUserException, StreamAlreadyHappenedException, StreamAlreadyCanceledException, FailedOperationException {
    // Retrieve the stream using the stream ID
    final FleenStream stream = streamService.findStream(streamId);
    // Verify if the stream's type is the same as the stream type of the request
    stream.checkStreamTypeNotEqual(processRequestToJoinDto.getStreamType());
    // Verify stream details like the owner, stream date and active status of the stream
    verifyStreamDetails(stream, user);
    // Retrieve the stream type
    final StreamType streamType = stream.getStreamType();
    // Attempt to process the attendee join request
    final StreamAttendee attendee = processAttendeeRequestToJoin(stream, processRequestToJoinDto);

    // Get stream other details
    final StreamOtherDetailsHolder streamOtherDetailsHolder = streamService.retrieveStreamOtherDetailsHolder(stream, user);
    // Retrieve the calendar from the stream details
    final Calendar calendar = streamOtherDetailsHolder.calendar();
    // Retrieve the oauth2Authorization from the stream details
    final Oauth2Authorization oauth2Authorization = streamOtherDetailsHolder.oauth2Authorization();

    // Create external stream request
    final ExternalStreamRequest attendeeProcessedJoinRequest = ExternalStreamRequest.ofProcessAttendeeJoinRequest(calendar, oauth2Authorization, stream, attendee, streamType, processRequestToJoinDto);
    // Check if the attendee request is approved and send invitation
    addAttendeeToStreamExternally(attendeeProcessedJoinRequest);
    // Convert the stream to response
    final FleenStreamResponse streamResponse = streamMapper.toStreamResponse(stream);
    // Get a processed attendee request to join stream response
    final ProcessAttendeeRequestToJoinStreamResponse processedRequestToJoin = commonMapper.processAttendeeRequestToJoinStream(streamResponse, attendee);
    // Return a localized response with the processed stream details
    return localizer.of(processedRequestToJoin);
  }

  /**
   * Attempts to process an attendee's request to join a stream. This method retrieves the attendee from the
   * stream based on the provided DTO and checks if their request to join has not been disapproved or is still pending.
   * It then handles the approval or disapproval of the request, updates the organizer's comment, and saves the attendee's details.
   *
   * <p>If the attendee's request is already disapproved or pending, the method processes the request by either
   * approving or disapproving the attendee's status in the stream. Afterward, it updates any comments made by
   * the organizer and persists the attendee information in the repository.</p>
   *
   * @param stream                     the stream to which the attendee has requested to join
   * @param processRequestToJoinDto     the DTO containing details about the attendee's request to join the stream
   * @return                           the processed StreamAttendee after their request has been approved or disapproved
   * @throws StreamAttendeeNotFoundException      if the attendee with the provided ID is not found
   * @throws FailedOperationException            if any operation within the process fails
   */
  private StreamAttendee processAttendeeRequestToJoin(final FleenStream stream, final ProcessAttendeeRequestToJoinStreamDto processRequestToJoinDto)
      throws StreamAttendeeNotFoundException, FailedOperationException {
    // Retrieve the stream attendee from the dto
    final Long attendeeId = processRequestToJoinDto.getAttendeeId();
    // Check if the user is already an attendee of the stream and process accordingly
    final StreamAttendee attendee = attendeeService.findAttendee(stream, attendeeId)
      .orElseThrow(StreamAttendeeNotFoundException.of(attendeeId));

    if (attendee.isRequestToJoinDisapprovedOrPending()) {
      // Update the organizer comment
      attendee.setOrganizerComment(processRequestToJoinDto.getComment());
      // Handle approved or disapproved request
      handleAttendeeRequestApproval(stream, attendee, processRequestToJoinDto);
      // Save the attendee details in the repository
      streamAttendeeRepository.save(attendee);

      // Create the notification
      final Notification notification = notificationMessageService.ofApprovedOrDisapprovedStreamJoinRequest(attendee.getStream(), attendee, attendee.getMember());
      // Save the notification
      notificationService.save(notification);
    } else {
      // Throw an error due to invalid input because attendee status cannot be approved twice
      throw new FailedOperationException();
    }

    return attendee;
  }

  /**
   * Handles the approval or disapproval of an attendee's request to join a stream. If the request is approved,
   * the total number of attendees for the stream is increased, and the attendee's status is updated to reflect
   * their approved attendance. If the request is disapproved, the attendee's status is updated accordingly.
   *
   * <p>If the attendee's request is approved, the method increases the total number of attendees for the stream
   * and marks the attendee's attendance as approved. If the request is disapproved, the method marks the
   * attendee's attendance as disapproved without modifying the stream's attendee count.</p>
   *
   * @param stream                     the stream to which the attendee has requested to join
   * @param attendee                   the attendee whose request is being handled
   * @param processRequestToJoinDto     the DTO containing details about whether the request was approved or disapproved
   */
  private void handleAttendeeRequestApproval(final FleenStream stream, final StreamAttendee attendee, final ProcessAttendeeRequestToJoinStreamDto processRequestToJoinDto) {
    if (processRequestToJoinDto.isApproved()) {
      // Increase the total number of attendees to stream
      streamService.increaseTotalAttendeesOrGuests(stream);
      // Approve attendee request to join the stream
      attendee.approveUserAttendance();
    } else if (processRequestToJoinDto.isDisapproved()) {
      // Disapprove attendee request to join the stream
      attendee.disapproveUserAttendance();
    }
  }

  /**
   * Adds an attendee to an external event associated with a stream. This method handles the process of registering
   * an attendee in an external calendar or event service, provided the request is valid and approved. It works
   * specifically for events that are associated with streams, such as scheduled meetings or webinars.
   *
   * <p>The method begins by verifying that the provided {@code ExternalStreamRequest} is not null and that it represents
   * an attendee request process. It checks if the request pertains to an event and if the attendee's request to join
   * the stream has been approved. Once these validations pass, the method retrieves the stream, the attendee, and the
   * associated calendar details from the request.</p>
   *
   * <p>After the necessary details are gathered, the attendee is added to the event using the external event joining
   * service. The service uses the external calendar ID, stream ID, and attendee's email address to register the attendee
   * in the external system (e.g., Google Calendar, Zoom, or similar external services).</p>
   *
   * @param externalStreamRequest     the request object containing details about the stream, attendee, and calendar
   * @throws CalendarNotFoundException if the associated calendar for the event cannot be found
   */
  private void addAttendeeToStreamExternally(final ExternalStreamRequest externalStreamRequest) throws CalendarNotFoundException {
    if (nonNull(externalStreamRequest) && externalStreamRequest.isProcessAttendeeRequest()) {
      final ProcessAttendeeRequestToJoinStreamDto processAttendeeRequestToJoinStreamDto = externalStreamRequest.getProcessAttendeeRequestToJoinStreamDto();

      if (externalStreamRequest.isAnEvent() && processAttendeeRequestToJoinStreamDto.isApproved()) {
        // Retrieve the stream, attendee and calendar from the request
        final FleenStream stream = externalStreamRequest.getStream();
        final StreamAttendee attendee = externalStreamRequest.getAttendee();
        final Calendar calendar = externalStreamRequest.getCalendar();

        // Add the attendee to the event associated with the stream
        eventJoinService.addAttendeeToEventExternally(calendar.getExternalId(), stream.getExternalId(), attendee.getEmailAddress(), null);
      }
    }
  }

  /**
   * Joins a stream based on the provided stream ID, user details, and stream join request.
   *
   * <p>This method begins by retrieving the {@link FleenStream} associated with the given {@code streamId}.
   * It ensures that the stream type in the request matches the type of the retrieved stream using {@code verifyIfStreamTypeNotEqual}.</p>
   *
   * <p>Once the stream details are verified and the user's eligibility is confirmed, the method attempts to add the user as a
   * {@link StreamAttendee}. For public streams, the user is automatically approved to attend, and the corresponding attendee
   * entry is created or retrieved.</p>
   *
   * <p>After the attendee details are processed, the method retrieves other stream-related details such as the external
   * calendar and OAuth2 authorization, which are used to send an invitation to the external event (if applicable).</p>
   *
   * <p>The method concludes by constructing a response object that includes the user's attendance information, stream type
   * details, and the total number of attendees. A localized response is then returned, providing a user-friendly message and
   * status regarding the join request.</p>
   *
   * @param streamId     the ID of the stream to join
   * @param joinStreamDto contains the details of the user's request to join the stream
   * @param user         the user attempting to join the stream
   * @return a localized response including stream attendance details and status
   * @throws FleenStreamNotFoundException        if the stream cannot be found by the given ID
   * @throws CalendarNotFoundException           if the external calendar associated with the stream cannot be found
   * @throws StreamAlreadyCanceledException      if the stream has been canceled
   * @throws StreamAlreadyHappenedException      if the stream has already occurred
   * @throws CannotJoinPrivateStreamWithoutApprovalException if the user needs approval to join the stream
   * @throws AlreadyRequestedToJoinStreamException    if the user has already requested to join the stream
   * @throws AlreadyApprovedRequestToJoinException    if the user's request to join the stream has already been approved
   */
  @Override
  @Transactional
  public JoinStreamResponse joinStream(final Long streamId, final JoinStreamDto joinStreamDto, final FleenUser user)
    throws FleenStreamNotFoundException, CalendarNotFoundException, StreamAlreadyCanceledException, StreamAlreadyHappenedException,
      CannotJoinPrivateStreamWithoutApprovalException, AlreadyRequestedToJoinStreamException, AlreadyApprovedRequestToJoinException {
    // Retrieve the stream using the stream ID
    final FleenStream stream = streamService.findStream(streamId);
    // Verify if the stream's type is the same as the stream type of the request
    stream.checkStreamTypeNotEqual(joinStreamDto.getStreamType());
    // Verify the stream details and attempt to join the stream
    streamService.verifyStreamDetailAllDetails(stream, user);
    // Retrieve the stream type
    final StreamType streamType = stream.getStreamType();
    // Verify the user details and attempt to join the stream
    final StreamAttendee attendee = attemptToJoinPublicStream(stream, joinStreamDto.getComment(), user);

    // Get stream other details
    final StreamOtherDetailsHolder streamOtherDetailsHolder = streamService.retrieveStreamOtherDetailsHolder(stream, user);
    // Retrieve the calendar from the stream details
    final Calendar calendar = streamOtherDetailsHolder.calendar();
    // Retrieve the oauth2Authorization from the stream details
    final Oauth2Authorization oauth2Authorization = streamOtherDetailsHolder.oauth2Authorization();

    // Get stream type info
    final StreamTypeInfo streamTypeInfo = streamMapper.toStreamTypeInfo(stream.getStreamType());
    // Convert the stream to the equivalent stream response
    final FleenStreamResponse streamResponse = streamMapper.toStreamResponse(stream);
    // Get the attendance information for the stream attendee
    final AttendanceInfo attendanceInfo = toInfoMapper.toAttendanceInfo(streamResponse, attendee.getRequestToJoinStatus(), attendee.isAttending(), attendee.isASpeaker());

    // Create the external stream request
    final ExternalStreamRequest externalStreamRequest = ExternalStreamRequest.ofJoinStream(calendar, oauth2Authorization, stream, streamType, user.getEmailAddress(), joinStreamDto);
    // Send invitation to new attendee
    joinStreamExternally(externalStreamRequest);
    // Calculate total employees going to stream
    final Long totalAttendeesGoing = stream.getTotalAttendees() + 1;
    // Create the response
    final JoinStreamResponse joinStreamResponse = JoinStreamResponse.of(streamId, attendanceInfo, streamTypeInfo, stream.getStreamLink(), totalAttendeesGoing);
    // Return localized response of the joined stream including status
    return localizer.of(joinStreamResponse);
  }

  /**
   * Attempts to allow a user to join a public stream by increasing the total attendees, creating a new or
   * retrieving an existing {@link StreamAttendee} entry, and approving their attendance.
   *
   * <p>This method starts by increasing the total attendees or guests for the stream to reflect the user's request to join.
   * It then either retrieves an existing {@link StreamAttendee} entry for the user and stream, or creates a new one if none exists.</p>
   *
   * <p>Once the {@link StreamAttendee} is in place, the method automatically approves the user's attendance for public streams.
   * Finally, the new attendee is saved to the repository to persist their participation in the stream.</p>
   *
   * @param stream  the public stream to be joined
   * @param comment optional comment provided by the user when joining
   * @param user    the user attempting to join the stream
   * @return the {@link StreamAttendee} instance representing the user's participation
   */
  private StreamAttendee attemptToJoinPublicStream(final FleenStream stream, final String comment, final FleenUser user)
    throws FleenStreamNotFoundException, StreamAlreadyCanceledException, StreamAlreadyHappenedException,
      CannotJoinPrivateStreamWithoutApprovalException, AlreadyRequestedToJoinStreamException, AlreadyApprovedRequestToJoinException {
    // Create a new StreamAttendee entry for the user
    final StreamAttendee streamAttendee = attendeeService.getExistingOrCreateNewStreamAttendee(stream, comment, user);
    // Approve user attendance if the stream is public
    streamAttendee.approveUserAttendance();
    // Add the new StreamAttendee to the stream's attendees list and save
    streamAttendeeRepository.save(streamAttendee);
    // Increase total attendees or guests in the stream
    streamService.increaseTotalAttendeesOrGuests(stream);
    // Return the stream attendee details
    return streamAttendee;
  }

  /**
   * Registers an attendee for an external stream associated with a stream.
   * This method adds the attendee to the event via an external calendar or event service.
   *
   * @param externalStreamRequest the request containing stream and attendee details
   */
  private void joinStreamExternally(final ExternalStreamRequest externalStreamRequest) {
    if (nonNull(externalStreamRequest) && externalStreamRequest.isJoinStreamRequest()
        && externalStreamRequest.isAnEvent()) {
      final AddNewEventAttendeeRequest addNewEventAttendeeRequest = AddNewEventAttendeeRequest.withComment(
        externalStreamRequest.calendarExternalId(),
        externalStreamRequest.streamExternalId(),
        externalStreamRequest.getAttendeeEmailAddress(),
        externalStreamRequest.getJoinStreamDto().getComment()
      );

      // Send an invitation to the user in the Calendar & Event API
      otherEventUpdateService.addNewAttendeeToCalendarEvent(addNewEventAttendeeRequest);
    }
  }

  /**
   * Handles a user's request to join a stream and manages related processes such as attendee registration, notifications,
   * and chat space membership. This method validates the request, updates the stream attendee's status, sends notifications,
   * and returns a localized response with the relevant stream and attendance details.
   *
   * <p>The method begins by retrieving the stream based on the provided {@code streamId}. It verifies that the stream type
   * in the request matches the type of the stream, ensuring the integrity of the request. Once the stream is found, the method
   * performs validations to check if the user is eligible to join the stream. This includes verifying whether the stream is
   * private, protected, or has specific rules for joining.</p>
   *
   * <p>If the user is eligible, the method retrieves an existing {@code StreamAttendee} entry for the user, or creates a new
   * one if the user has not yet made a request to join the stream. For private streams, the attendee's request status is set
   * to pending, indicating that the request requires approval from the stream organizer.</p>
   *
   * <p>After the attendee's request status is updated, the stream and the attendee details are saved in the repository.
   * Following this, a notification is created and sent to notify the stream organizer that a new join request has been made
   * for the private stream. If the stream has chat space integration, the method also checks the user’s membership in the
   * associated chat space and handles necessary invitations or join requests for the chat space.</p>
   *
   * <p>The method then constructs a response object that includes details about the stream, such as attendance information
   * and stream type. Finally, the response is localized based on the user's locale, ensuring the user receives feedback in
   * their preferred language or region format.</p>
   *
   * @param streamId              the unique identifier of the stream to join
   * @param requestToJoinStreamDto the data transfer object containing the request details, such as a comment and stream type
   * @param user                  the user making the request to join the stream
   *
   * @return a localized {@code RequestToJoinStreamResponse} object containing the stream details and the user's attendance status
   *
   * @throws FleenStreamNotFoundException if the stream is not found by its ID
   * @throws CalendarNotFoundException if the calendar is not found
   * @throws StreamNotCreatedByUserException if the user is not authorized to join the stream
   * @throws StreamAlreadyHappenedException if the stream has already occurred and is no longer available for joining
   * @throws StreamAlreadyCanceledException if the stream has been canceled and cannot be joined
   * @throws FailedOperationException if an unexpected error occurs during the operation
   */
  @Override
  @Transactional
  public RequestToJoinStreamResponse requestToJoinStream(final Long streamId, final RequestToJoinStreamDto requestToJoinStreamDto, final FleenUser user)
    throws FleenStreamNotFoundException, CalendarNotFoundException, StreamAlreadyCanceledException,
      StreamAlreadyHappenedException, AlreadyRequestedToJoinStreamException, AlreadyApprovedRequestToJoinException {
    // Find the stream by its ID
    final FleenStream stream = streamService.findStream(streamId);
    // Verify if the stream's type is the same as the stream type of the request
    stream.checkStreamTypeNotEqual(requestToJoinStreamDto.getStreamType());
    // Validate the stream details and eligibility of the user
    streamService.validateStreamAndUserForProtectedStream(stream, user);
    // Retrieve the stream attendee entry associated with the user or create a new StreamAttendee entry if none for the user
    final StreamAttendee attendee = attendeeService.getExistingOrCreateNewStreamAttendee(stream, requestToJoinStreamDto.getComment(), user);
    // If the stream is private, set the request to join status to pending
    setAttendeeRequestToJoinPendingIfStreamIsPrivate(attendee, stream);
    // Save the stream and the attendee
    saveStreamAndAttendee(stream, attendee);

    // Convert the stream to equivalent stream response
    final FleenStreamResponse streamResponse = streamMapper.toStreamResponse(stream);
    // Get the attendance information for the stream attendee
    final AttendanceInfo attendanceInfo = toInfoMapper.toAttendanceInfo(streamResponse, attendee.getRequestToJoinStatus(), attendee.isAttending(), attendee.isASpeaker());
    // Get stream type info
    final StreamTypeInfo streamTypeInfo = streamMapper.toStreamTypeInfo(stream.getStreamType());
    // Send and save notifications
    sendJoinRequestNotificationForPrivateStream(stream, attendee, user);
    // Check and handle chat space membership and invitation
    handleJoinRequestForPrivateStreamBasedOnChatSpaceMembership(stream, attendee, requestToJoinStreamDto.getComment(), user);
    // Create the response
    final RequestToJoinStreamResponse requestToJoinStreamResponse = RequestToJoinStreamResponse.of(stream.getStreamId(), attendanceInfo, streamTypeInfo, stream.getTotalAttendees());
    // Return the localized response of the request to join the stream
    return localizer.of(requestToJoinStreamResponse);
  }

  /**
   * Saves both the stream and the stream attendee.
   * This method ensures that a newly added StreamAttendee is saved to the repository,
   * and the associated stream is updated and saved in its respective repository.
   *
   * @param stream         the stream associated with the attendee
   * @param streamAttendee the attendee to be added to the stream
   *
   */
  private void saveStreamAndAttendee(final FleenStream stream, final StreamAttendee streamAttendee) {
    if (nonNull(stream) && nonNull(streamAttendee)) {
      // Add the new StreamAttendee to the stream's attendees list and save
      streamAttendeeRepository.save(streamAttendee);
      // Save the stream to the repository
      streamRepository.save(stream);
    }
  }

  /**
   * Sends a notification for a join request to a private stream.
   * This method creates a notification when a user requests to join a private stream,
   * associating the stream, the requesting attendee, and the stream organizer.
   * The notification is then saved using the {@code notificationService}.
   *
   * @param stream         the private stream to which the join request notification is related
   * @param streamAttendee the attendee requesting to join the private stream
   * @param user           the user attempting to join the private stream, converted to a member
   */
  private void sendJoinRequestNotificationForPrivateStream(final FleenStream stream, final StreamAttendee streamAttendee, final FleenUser user) {
    // Create and save notification
    final Notification notification = notificationMessageService.ofReceivedStreamJoinRequest(stream, streamAttendee, stream.getOrganizer(), user.toMember());
    // Save the notification
    notificationService.save(notification);
  }

  /**
   * Handles join requests for a private stream based on the chat space membership of the user.
   * This method verifies if the provided stream, attendee, and user are non-null and if the stream is an event,
   * then delegates the join request handling to the {@code eventJoinService}.
   *
   * @param stream   the private stream to which the join request is being processed
   * @param attendee the attendee who is requesting to join the private stream
   * @param comment  any comment provided by the attendee when requesting to join the stream
   * @param user     the user attempting to join the private stream based on chat space membership
   */
  private void handleJoinRequestForPrivateStreamBasedOnChatSpaceMembership(final FleenStream stream, final StreamAttendee attendee, final String comment, final FleenUser user) {
   if (nonNull(stream) && stream.isAnEvent() && nonNull(attendee) && nonNull(user)) {
     eventJoinService.handleJoinRequestForPrivateStreamBasedOnChatSpaceMembership(stream, attendee, comment, user);
   }
  }

  /**
   * Marks the user as not attending a stream and processes the necessary updates, including external systems.
   *
   * <p>This method first verifies the stream details and ensures that the user is authorized to perform the action.
   * It then processes the user's request to not attend the stream by removing the attendee record, updating the Google
   * Calendar event, and notifying external systems.</p>
   *
   * <p>If any errors occur, such as the stream not being found or the user not having an attendee record for the event,
   * the method will throw a {@link FailedOperationException} or a relevant exception indicating the issue.</p>
   *
   * @param streamId              the ID of the stream the user is opting out of attending
   * @param notAttendingStreamDto  the data transfer object containing stream and user information
   * @param user                  the user who is marking themselves as not attending
   * @return a localized {@link NotAttendingStreamResponse} with the relevant stream details
   * @throws FleenStreamNotFoundException if the stream is not found
   * @throws CalendarNotFoundException    if the calendar associated with the event is not found
   * @throws FailedOperationException     if the operation fails, such as not finding the attendee record
   */
  @Override
  @Transactional
  public NotAttendingStreamResponse notAttendingStream(final Long streamId, final NotAttendingStreamDto notAttendingStreamDto, final FleenUser user)
    throws FleenStreamNotFoundException, CalendarNotFoundException, StreamAlreadyCanceledException,
      StreamAlreadyHappenedException, FailedOperationException {
    // Find the stream by its ID
    final FleenStream stream = streamService.findStream(streamId);
    // Retrieve the stream type
    final StreamType streamType = stream.getStreamType();
    // Verify if the stream's type is the same as the stream type of the request
    stream.checkStreamTypeNotEqual(notAttendingStreamDto.getStreamType());
    // Verify if the stream has not been canceled
    verifyStreamIsNotCancelled(stream);
    // Verify if the stream has not happened
    verifyStreamHasNotHappenedAlready(stream);
    // Verify if the user is the owner and fail the operation because the owner is automatically a member of the event
    stream.checkIsNotOrganizer(user.getId());

    // Find the existing attendee record for the user and event
    final StreamAttendee attendee = streamAttendeeRepository.findAttendeeByStreamAndUser(stream, user.toMember())
      .orElseThrow(FailedOperationException::new);
    // Get stream other details
    final StreamOtherDetailsHolder streamOtherDetailsHolder = streamService.retrieveStreamOtherDetailsHolder(stream, user);
    // Retrieve the calendar from the stream details
    final Calendar calendar = streamOtherDetailsHolder.calendar();
    // Process the not attending stream request
    streamService.processNotAttendingStream(stream, attendee);

    // Create a request that remove the attendee from the Google Calendar event
    final ExternalStreamRequest notAttendingStreamRequest = ExternalStreamRequest.ofNotAttending(calendar, stream, user.getEmailAddress(), streamType);
    // Send the request for the update of non-attendance
    notAttendingStreamExternally(notAttendingStreamRequest);

    final NotAttendingStreamResponse notAttendingStreamResponse = commonMapper.notAttendingStream();
    // Retrieve the stream type info
    final StreamTypeInfo streamTypeInfo = streamMapper.toStreamTypeInfo(streamType);
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

}
