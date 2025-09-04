package com.fleencorp.feen.stream.service.impl.core;

import com.fleencorp.feen.calendar.exception.core.CalendarNotFoundException;
import com.fleencorp.feen.calendar.model.domain.Calendar;
import com.fleencorp.feen.calendar.model.request.event.create.AddNewEventAttendeeRequest;
import com.fleencorp.feen.calendar.model.request.event.update.NotAttendingEventRequest;
import com.fleencorp.feen.common.exception.FailedOperationException;
import com.fleencorp.feen.notification.model.domain.Notification;
import com.fleencorp.feen.notification.service.NotificationService;
import com.fleencorp.feen.notification.service.impl.NotificationMessageService;
import com.fleencorp.feen.oauth2.exception.core.Oauth2InvalidAuthorizationException;
import com.fleencorp.feen.oauth2.model.domain.Oauth2Authorization;
import com.fleencorp.feen.shared.member.contract.IsAMember;
import com.fleencorp.feen.shared.security.RegisteredUser;
import com.fleencorp.feen.shared.stream.contract.IsAStream;
import com.fleencorp.feen.shared.stream.contract.IsAttendee;
import com.fleencorp.feen.stream.constant.core.StreamType;
import com.fleencorp.feen.stream.exception.attendee.StreamAttendeeNotFoundException;
import com.fleencorp.feen.stream.exception.core.StreamAlreadyCanceledException;
import com.fleencorp.feen.stream.exception.core.StreamAlreadyHappenedException;
import com.fleencorp.feen.stream.exception.core.StreamNotCreatedByUserException;
import com.fleencorp.feen.stream.exception.core.StreamNotFoundException;
import com.fleencorp.feen.stream.exception.request.AlreadyApprovedRequestToJoinException;
import com.fleencorp.feen.stream.exception.request.AlreadyRequestedToJoinStreamException;
import com.fleencorp.feen.stream.exception.request.CannotJoinPrivateStreamWithoutApprovalException;
import com.fleencorp.feen.stream.mapper.StreamUnifiedMapper;
import com.fleencorp.feen.stream.model.domain.FleenStream;
import com.fleencorp.feen.stream.model.domain.StreamAttendee;
import com.fleencorp.feen.stream.model.dto.attendee.JoinStreamDto;
import com.fleencorp.feen.stream.model.dto.attendee.NotAttendingStreamDto;
import com.fleencorp.feen.stream.model.dto.attendee.ProcessAttendeeRequestToJoinStreamDto;
import com.fleencorp.feen.stream.model.dto.attendee.RequestToJoinStreamDto;
import com.fleencorp.feen.stream.model.holder.StreamOtherDetailsHolder;
import com.fleencorp.feen.stream.model.info.attendance.AttendanceInfo;
import com.fleencorp.feen.stream.model.info.core.StreamTypeInfo;
import com.fleencorp.feen.stream.model.request.external.ExternalStreamRequest;
import com.fleencorp.feen.stream.model.response.StreamResponse;
import com.fleencorp.feen.stream.model.response.attendance.JoinStreamResponse;
import com.fleencorp.feen.stream.model.response.attendance.NotAttendingStreamResponse;
import com.fleencorp.feen.stream.model.response.attendance.ProcessAttendeeRequestToJoinStreamResponse;
import com.fleencorp.feen.stream.model.response.attendance.RequestToJoinStreamResponse;
import com.fleencorp.feen.stream.service.attendee.StreamAttendeeOperationsService;
import com.fleencorp.feen.stream.service.common.StreamOperationsService;
import com.fleencorp.feen.stream.service.core.CommonStreamJoinService;
import com.fleencorp.feen.stream.service.core.StreamService;
import com.fleencorp.feen.stream.service.event.EventOperationsService;
import com.fleencorp.localizer.service.Localizer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.fleencorp.feen.stream.service.impl.core.StreamServiceImpl.setAttendeeRequestToJoinPendingIfStreamIsPrivate;
import static com.fleencorp.feen.stream.service.impl.core.StreamServiceImpl.verifyStreamDetails;
import static java.util.Objects.nonNull;

@Service
public class CommonStreamJoinServiceImpl implements CommonStreamJoinService {

  private final EventOperationsService eventOperationsService;
  private final StreamAttendeeOperationsService streamAttendeeOperationsService;
  private final StreamService streamService;
  private final StreamOperationsService streamOperationsService;
  private final NotificationService notificationService;
  private final NotificationMessageService notificationMessageService;
  private final StreamUnifiedMapper streamUnifiedMapper;
  private final Localizer localizer;

  public CommonStreamJoinServiceImpl(
      final EventOperationsService eventOperationsService,
      final NotificationService notificationService,
      final NotificationMessageService notificationMessageService,
      final StreamAttendeeOperationsService streamAttendeeOperationsService,
      final StreamOperationsService streamOperationsService,
      final StreamService streamService,
      final StreamUnifiedMapper streamUnifiedMapper,
      final Localizer localizer) {
    this.eventOperationsService = eventOperationsService;
    this.notificationService = notificationService;
    this.notificationMessageService = notificationMessageService;
    this.streamAttendeeOperationsService = streamAttendeeOperationsService;
    this.streamOperationsService = streamOperationsService;
    this.streamService = streamService;
    this.streamUnifiedMapper = streamUnifiedMapper;
    this.localizer = localizer;
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
   * @throws StreamNotFoundException          if the stream with the provided ID is not found
   * @throws CalendarNotFoundException             if the calendar associated with the stream is not found
   * @throws Oauth2InvalidAuthorizationException   if the OAuth2 authorization required for external services is invalid
   * @throws StreamNotCreatedByUserException       if the stream was not created by the current user
   * @throws StreamAlreadyHappenedException        if the stream has already occurred and cannot be modified
   * @throws StreamAlreadyCanceledException        if the stream has been canceled and cannot be modified
   * @throws FailedOperationException              if any external operation, such as adding the attendee, fails
   */
  @Override
  @Transactional
  public ProcessAttendeeRequestToJoinStreamResponse processAttendeeRequestToJoinStream(final Long streamId, final ProcessAttendeeRequestToJoinStreamDto processRequestToJoinDto, final RegisteredUser user)
      throws StreamNotFoundException, CalendarNotFoundException, Oauth2InvalidAuthorizationException,
        StreamNotCreatedByUserException, StreamAlreadyHappenedException, StreamAlreadyCanceledException, FailedOperationException {
    final FleenStream stream = streamOperationsService.findStream(streamId);

    stream.checkStreamTypeNotEqual(processRequestToJoinDto.getStreamType());
    verifyStreamDetails(stream, user);

    final StreamType streamType = stream.getStreamType();
    final StreamAttendee attendee = processAttendeeRequestToJoin(stream, processRequestToJoinDto);

    final StreamOtherDetailsHolder streamOtherDetailsHolder = streamOperationsService.retrieveStreamOtherDetailsHolder(stream, user.toMember());
    final Calendar calendar = streamOtherDetailsHolder.calendar();
    final Oauth2Authorization oauth2Authorization = streamOtherDetailsHolder.oauth2Authorization();

    final ExternalStreamRequest attendeeProcessedJoinRequest = ExternalStreamRequest.ofProcessAttendeeJoinRequest(calendar, oauth2Authorization, stream, attendee, streamType, processRequestToJoinDto);
    addAttendeeToStreamExternally(attendeeProcessedJoinRequest);
    final StreamResponse streamResponse = streamUnifiedMapper.toStreamResponse(stream);
    final ProcessAttendeeRequestToJoinStreamResponse processedRequestToJoin = streamUnifiedMapper.processAttendeeRequestToJoinStream(streamResponse, attendee);

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
    final Long attendeeId = processRequestToJoinDto.getAttendeeId();
    final StreamAttendee attendee = streamAttendeeOperationsService.findAttendee(stream.getStreamId(), attendeeId)
      .orElseThrow(StreamAttendeeNotFoundException.of(attendeeId));

    if (attendee.isRequestToJoinDisapprovedOrPending()) {
      attendee.setOrganizerComment(processRequestToJoinDto.getComment());
      handleAttendeeRequestApproval(stream, attendee, processRequestToJoinDto);
      streamAttendeeOperationsService.save(attendee);

      final Notification notification = notificationMessageService.ofApprovedOrDisapprovedStreamJoinRequest(attendee.getStream(), attendee, attendee.getMemberId());
      notificationService.save(notification);
    } else {
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
      streamOperationsService.increaseTotalAttendeesOrGuests(stream);
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
        final IsAStream stream = externalStreamRequest.getStream();
        final IsAttendee attendee = externalStreamRequest.getAttendee();
        final Calendar calendar = externalStreamRequest.getCalendar();

        eventOperationsService.addAttendeeToEventExternally(calendar.getExternalId(), stream.getExternalId(), attendee.getEmailAddress(), null);
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
   * @throws StreamNotFoundException        if the stream cannot be found by the given ID
   * @throws CalendarNotFoundException           if the external calendar associated with the stream cannot be found
   * @throws StreamAlreadyCanceledException      if the stream has been canceled
   * @throws StreamAlreadyHappenedException      if the stream has already occurred
   * @throws CannotJoinPrivateStreamWithoutApprovalException if the user needs approval to join the stream
   * @throws AlreadyRequestedToJoinStreamException    if the user has already requested to join the stream
   * @throws AlreadyApprovedRequestToJoinException    if the user's request to join the stream has already been approved
   */
  @Override
  @Transactional
  public JoinStreamResponse joinStream(final Long streamId, final JoinStreamDto joinStreamDto, final RegisteredUser user)
    throws StreamNotFoundException, CalendarNotFoundException, StreamAlreadyCanceledException, StreamAlreadyHappenedException,
      CannotJoinPrivateStreamWithoutApprovalException, AlreadyRequestedToJoinStreamException, AlreadyApprovedRequestToJoinException {
    final FleenStream stream = streamService.findStream(streamId);

    stream.checkStreamTypeNotEqual(joinStreamDto.getStreamType());
    streamService.verifyStreamDetailAllDetails(stream, user.getId());

    final StreamType streamType = stream.getStreamType();
    final StreamAttendee attendee = attemptToJoinPublicStream(stream, joinStreamDto.getComment(), user.toMember());

    final StreamOtherDetailsHolder streamOtherDetailsHolder = streamService.retrieveStreamOtherDetailsHolder(stream, user.toMember());
    final Calendar calendar = streamOtherDetailsHolder.calendar();
    final Oauth2Authorization oauth2Authorization = streamOtherDetailsHolder.oauth2Authorization();

    final StreamTypeInfo streamTypeInfo = streamUnifiedMapper.toStreamTypeInfo(stream.getStreamType());
    final StreamResponse streamResponse = streamUnifiedMapper.toStreamResponse(stream);
    final AttendanceInfo attendanceInfo = streamUnifiedMapper.toAttendanceInfo(streamResponse, attendee.getRequestToJoinStatus(), attendee.isAttending(), attendee.isASpeaker());

    final ExternalStreamRequest externalStreamRequest = ExternalStreamRequest.ofJoinStream(calendar, oauth2Authorization, stream, streamType, user.getEmailAddress(), joinStreamDto);
    joinStreamExternally(externalStreamRequest);

    final Integer totalAttendeesGoing = stream.getTotalAttendees() + 1;
    final JoinStreamResponse joinStreamResponse = JoinStreamResponse.of(streamId, attendanceInfo, streamTypeInfo, stream.getStreamLink(), totalAttendeesGoing);
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
  private StreamAttendee attemptToJoinPublicStream(final FleenStream stream, final String comment, final IsAMember user)
    throws StreamNotFoundException, StreamAlreadyCanceledException, StreamAlreadyHappenedException,
      CannotJoinPrivateStreamWithoutApprovalException, AlreadyRequestedToJoinStreamException, AlreadyApprovedRequestToJoinException {
    final StreamAttendee streamAttendee = streamAttendeeOperationsService.getExistingOrCreateNewStreamAttendee(stream, comment, user);

    streamAttendee.approveUserAttendance();
    streamAttendeeOperationsService.save(streamAttendee);
    streamOperationsService.increaseTotalAttendeesOrGuests(stream);

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
      eventOperationsService.addNewAttendeeToCalendarEvent(addNewEventAttendeeRequest);
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
   * @throws StreamNotFoundException if the stream is not found by its ID
   * @throws CalendarNotFoundException if the calendar is not found
   * @throws StreamNotCreatedByUserException if the user is not authorized to join the stream
   * @throws StreamAlreadyHappenedException if the stream has already occurred and is no longer available for joining
   * @throws StreamAlreadyCanceledException if the stream has been canceled and cannot be joined
   * @throws FailedOperationException if an unexpected error occurs during the operation
   */
  @Override
  @Transactional
  public RequestToJoinStreamResponse requestToJoinStream(final Long streamId, final RequestToJoinStreamDto requestToJoinStreamDto, final RegisteredUser user)
    throws StreamNotFoundException, CalendarNotFoundException, StreamAlreadyCanceledException,
      StreamAlreadyHappenedException, AlreadyRequestedToJoinStreamException, AlreadyApprovedRequestToJoinException {
    final FleenStream stream = streamOperationsService.findStream(streamId);

    stream.checkStreamTypeNotEqual(requestToJoinStreamDto.getStreamType());
    streamOperationsService.validateStreamAndUserForProtectedStream(stream, user.getId());

    final StreamAttendee attendee = streamAttendeeOperationsService.getExistingOrCreateNewStreamAttendee(stream, requestToJoinStreamDto.getComment(), user.toMember());
    setAttendeeRequestToJoinPendingIfStreamIsPrivate(attendee, stream);
    saveStreamAndAttendee(stream, attendee);

    final StreamResponse streamResponse = streamUnifiedMapper.toStreamResponse(stream);
    final AttendanceInfo attendanceInfo = streamUnifiedMapper.toAttendanceInfo(streamResponse, attendee.getRequestToJoinStatus(), attendee.isAttending(), attendee.isASpeaker());
    final StreamTypeInfo streamTypeInfo = streamUnifiedMapper.toStreamTypeInfo(stream.getStreamType());

    sendJoinRequestNotificationForPrivateStream(stream, attendee, user);
    handleJoinRequestForPrivateStreamBasedOnChatSpaceMembership(stream, attendee, requestToJoinStreamDto.getComment(), user);

    final RequestToJoinStreamResponse requestToJoinStreamResponse = RequestToJoinStreamResponse.of(stream.getStreamId(), attendanceInfo, streamTypeInfo, stream.getTotalAttendees());
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
      streamAttendeeOperationsService.save(streamAttendee);
      // Save the stream to the repository
      streamOperationsService.save(stream);
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
  private void sendJoinRequestNotificationForPrivateStream(final FleenStream stream, final StreamAttendee streamAttendee, final RegisteredUser user) {
    // Create and save notification
    final Notification notification = notificationMessageService.ofReceivedStreamJoinRequest(stream, streamAttendee, stream.getOrganizerId(), user.toMember());
    // Save the notification
    notificationService.save(notification);
  }

  /**
   * Handles join requests for a private stream based on the chat space membership of the user.
   * This method verifies if the provided stream, attendee, and user are non-null and if the stream is an event,
   * then delegates the join request handling to the {@code eventOperationsService}.
   *
   * @param stream   the private stream to which the join request is being processed
   * @param attendee the attendee who is requesting to join the private stream
   * @param comment  any comment provided by the attendee when requesting to join the stream
   * @param user     the user attempting to join the private stream based on chat space membership
   */
  private void handleJoinRequestForPrivateStreamBasedOnChatSpaceMembership(final FleenStream stream, final StreamAttendee attendee, final String comment, final RegisteredUser user) {
   if (nonNull(stream) && stream.isAnEvent() && nonNull(attendee) && nonNull(user)) {
     eventOperationsService.handleJoinRequestForPrivateStreamBasedOnChatSpaceMembership(stream, attendee, comment, user);
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
   * @throws StreamNotFoundException if the stream is not found
   * @throws CalendarNotFoundException    if the calendar associated with the event is not found
   * @throws FailedOperationException     if the operation fails, such as not finding the attendee record
   */
  @Override
  @Transactional
  public NotAttendingStreamResponse notAttendingStream(final Long streamId, final NotAttendingStreamDto notAttendingStreamDto, final RegisteredUser user)
    throws StreamNotFoundException, CalendarNotFoundException, StreamAlreadyCanceledException,
      StreamAlreadyHappenedException, FailedOperationException {
    final FleenStream stream = streamOperationsService.findStream(streamId);

    final StreamType streamType = stream.getStreamType();
    stream.checkStreamTypeNotEqual(notAttendingStreamDto.getStreamType());
    stream.checkNotCancelled();
    stream.checkNotEnded();
    stream.checkIsNotOrganizer(user.getId());

    final StreamAttendee attendee = streamAttendeeOperationsService.findAttendeeByStreamAndUser(stream.getStreamId(), user.getId())
      .orElseThrow(FailedOperationException::new);

    final StreamOtherDetailsHolder streamOtherDetailsHolder = streamOperationsService.retrieveStreamOtherDetailsHolder(stream, user.toMember());
    final Calendar calendar = streamOtherDetailsHolder.calendar();
    streamService.processNotAttendingStream(stream, attendee);

    final ExternalStreamRequest notAttendingStreamRequest = ExternalStreamRequest.ofNotAttending(calendar, stream, user.getEmailAddress(), streamType);
    notAttendingStreamExternally(notAttendingStreamRequest);

    final NotAttendingStreamResponse notAttendingStreamResponse = streamUnifiedMapper.notAttendingStream();
    final StreamTypeInfo streamTypeInfo = streamUnifiedMapper.toStreamTypeInfo(streamType);
    notAttendingStreamResponse.setStreamTypeInfo(streamTypeInfo);

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
      eventOperationsService.notAttendingEvent(notAttendingEventRequest);
    }
  }

}
