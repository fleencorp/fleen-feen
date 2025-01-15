package com.fleencorp.feen.service.impl.stream.base;

import com.fleencorp.feen.constant.stream.StreamAttendeeRequestToJoinStatus;
import com.fleencorp.feen.constant.stream.StreamType;
import com.fleencorp.feen.exception.base.FailedOperationException;
import com.fleencorp.feen.exception.stream.*;
import com.fleencorp.feen.mapper.stream.StreamMapper;
import com.fleencorp.feen.model.domain.notification.Notification;
import com.fleencorp.feen.model.domain.stream.FleenStream;
import com.fleencorp.feen.model.domain.stream.StreamAttendee;
import com.fleencorp.feen.model.domain.user.Member;
import com.fleencorp.feen.model.dto.stream.attendance.ProcessAttendeeRequestToJoinStreamDto;
import com.fleencorp.feen.model.dto.stream.attendance.RequestToJoinStreamDto;
import com.fleencorp.feen.model.info.stream.StreamTypeInfo;
import com.fleencorp.feen.model.info.stream.attendance.AttendanceInfo;
import com.fleencorp.feen.model.other.Schedule;
import com.fleencorp.feen.model.projection.StreamAttendeeSelect;
import com.fleencorp.feen.model.response.base.FleenFeenResponse;
import com.fleencorp.feen.model.response.holder.TryToJoinPrivateOrProtectedStreamResponse;
import com.fleencorp.feen.model.response.holder.TryToJoinPublicStreamResponse;
import com.fleencorp.feen.model.response.stream.FleenStreamResponse;
import com.fleencorp.feen.model.response.stream.attendance.RequestToJoinStreamResponse;
import com.fleencorp.feen.model.security.FleenUser;
import com.fleencorp.feen.repository.stream.FleenStreamRepository;
import com.fleencorp.feen.repository.stream.StreamAttendeeRepository;
import com.fleencorp.feen.service.impl.notification.NotificationMessageService;
import com.fleencorp.feen.service.notification.NotificationService;
import com.fleencorp.feen.service.stream.attendee.StreamAttendeeService;
import com.fleencorp.feen.service.stream.common.StreamService;
import com.fleencorp.localizer.service.Localizer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

import static com.fleencorp.base.util.ExceptionUtil.*;
import static com.fleencorp.feen.service.impl.stream.attendee.StreamAttendeeServiceImpl.groupAttendeeAttendance;
import static com.fleencorp.feen.util.DateTimeUtil.convertToTimezone;
import static java.util.Objects.nonNull;

/**
 * Implementation of the {@link StreamService} interface for managing stream-related operations.
 *
 * <p>This class provides services related to stream management, such as handling stream creation, updating stream
 * information, managing stream attendees, and sending notifications regarding streams. It interacts with repositories
 * for stream and attendee data, as well as notification services to keep users informed about stream-related events.</p>
 *
 * <p>Common responsibilities of this service include creating, updating, and deleting streams, managing attendees'
 * participation in streams, and sending notifications to users based on stream events.</p>
 *
 * <p>Note that this class is expected to be a Spring service and utilizes various injected dependencies for
 * repository access and service orchestration.</p>
 *
 * @author Yusuf Àlàmù Musa
 * @version 1.0
 */
@Slf4j
@Service
public class StreamServiceImpl implements StreamService {

  private final NotificationService notificationService;
  private final NotificationMessageService notificationMessageService;
  private final StreamAttendeeService attendeeService;
  private final FleenStreamRepository streamRepository;
  private final StreamAttendeeRepository streamAttendeeRepository;
  private final Localizer localizer;
  private final StreamMapper streamMapper;

  /**
   * Constructor for {@link StreamServiceImpl} class.
   *
   * <p>This constructor initializes the required dependencies for the {@link StreamServiceImpl} class, which are injected
   * through the constructor. The dependencies include services for notifications, attendee management, stream data repository,
   * and mapping for stream-related objects.</p>
   *
   * @param notificationService the service responsible for managing notifications
   * @param notificationMessageService the service for managing notification messages
   * @param attendeeService the service responsible for handling stream attendees
   * @param streamRepository the repository for managing stream data
   * @param streamAttendeeRepository the repository for managing stream attendee data
   * @param localizer the service for handling localized responses
   * @param streamMapper the mapper for converting stream-related objects to different representations
   */
  public StreamServiceImpl(
      final NotificationService notificationService,
      final NotificationMessageService notificationMessageService,
      @Lazy final StreamAttendeeService attendeeService,
      final FleenStreamRepository streamRepository,
      final StreamAttendeeRepository streamAttendeeRepository,
      final Localizer localizer,
      final StreamMapper streamMapper) {
    this.notificationService = notificationService;
    this.notificationMessageService = notificationMessageService;
    this.attendeeService = attendeeService;
    this.streamRepository = streamRepository;
    this.streamAttendeeRepository = streamAttendeeRepository;
    this.localizer = localizer;
    this.streamMapper = streamMapper;
  }

  /**
   * Finds a stream by its ID or throws an exception if the stream is not found.
   *
   * <p>This method attempts to retrieve a {@link FleenStream} from the repository using the provided stream ID.
   * If no stream is found, it throws a {@link FleenStreamNotFoundException} with the given ID.</p>
   *
   * @param streamId the ID of the stream to find; must not be null
   * @return the {@link FleenStream} corresponding to the given ID
   * @throws FleenStreamNotFoundException if no stream is found with the provided ID
   */
  @Override
  public FleenStream findStream(final Long streamId) throws FleenStreamNotFoundException {
    return streamRepository.findById(streamId)
      .orElseThrow(FleenStreamNotFoundException.of(streamId));
  }

  /**
   * Handles a request to join a public stream.
   *
   * <p>This method verifies the stream details, processes the user's request to join a public stream, creates a
   * new StreamAttendee entry, approves user attendance, and saves the stream and attendee information. It then
   * converts the stream to a response and generates attendance information to be included in the response.</p>
   *
   * @param streamId the ID of the stream the user is requesting to join
   * @param comment an optional comment provided by the user when joining the stream
   * @param user the {@link FleenUser} attempting to join the public stream
   * @return a {@link TryToJoinPublicStreamResponse} containing the stream, the stream attendee, and
   *         attendance information
   */
  @Override
  @Transactional
  public TryToJoinPublicStreamResponse tryToJoinPublicStream(final Long streamId, final String comment, final FleenUser user)
    throws FleenStreamNotFoundException, StreamAlreadyCanceledException, StreamAlreadyHappenedException,
      CannotJointStreamWithoutApprovalException, AlreadyRequestedToJoinStreamException, AlreadyApprovedRequestToJoinException {
    // Verify the stream details and attempt to join the stream
    final FleenStream stream = verifyDetailsAndTryToJoinStream(streamId, user);
    // Create a new StreamAttendee entry for the user
    final StreamAttendee streamAttendee = attendeeService.getExistingOrCreateNewStreamAttendee(stream, comment, user);
    // Approve user attendance if the stream is public
    streamAttendee.approveUserAttendance();
    // Add the new StreamAttendee to the stream's attendees list and save
    streamAttendeeRepository.save(streamAttendee);
    // Convert the stream to the equivalent stream response
    final FleenStreamResponse streamResponse = streamMapper.toFleenStreamResponse(stream);
    // Get the attendance information for the stream attendee
    final AttendanceInfo attendanceInfo = streamMapper.toAttendanceInfo(streamResponse, streamAttendee.getRequestToJoinStatus(), streamAttendee.isAttending());
    // Return the stream
    return TryToJoinPublicStreamResponse.of(stream, streamAttendee, attendanceInfo);
  }

  /**
   * Verifies the details of the stream and user, then attempts to allow the user to join the stream.
   *
   * <p>This method performs several checks to ensure that the user can join the specified stream.
   * It first retrieves the stream using the provided stream ID. It then verifies whether the user is the owner
   * of the stream, failing the operation if so, since the owner is automatically considered a member of the entity.</p>
   *
   * <p>Next, it checks that the stream has not been cancelled and that it has not already ended.
   * If the stream is private, it checks the privacy settings to ensure proper access control.
   * The method also verifies that the user is not already an attendee of the stream.</p>
   *
   * <p>After all checks pass, the stream is saved to the repository and returned.</p>
   *
   * @param streamId the ID of the stream to join; must not be null
   * @param user     the user attempting to join the stream; must not be null
   * @return the {@link FleenStream} if all verifications pass
   * @throws FailedOperationException if any validation or stream state check fails
   * @throws StreamAlreadyCanceledException if the stream is cancelled
   * @throws StreamAlreadyHappenedException if the stream has already ended
   * @throws CannotJointStreamWithoutApprovalException if the stream is private
   * @throws AlreadyRequestedToJoinStreamException is the user already requested to join the stream
   * @throws AlreadyApprovedRequestToJoinException if the user's request to join is already approved
   */
  protected FleenStream verifyDetailsAndTryToJoinStream(final Long streamId, final FleenUser user) {
    final FleenStream stream = findStream(streamId);

    // Verify if the user is the owner and fail the operation because the owner is automatically a member of an entity
    verifyIfUserIsAuthorOrCreatorOrOwnerTryingToPerformAction(Member.of(stream.getMemberId()), user);
    // Verify stream is not canceled
    verifyStreamIsNotCancelled(stream);
    // Check if the stream is still active and can be joined.
    verifyStreamHasNotHappenedAlready(stream);
    // Check if the stream is private
    checkIfStreamIsPrivate(streamId, stream);
    // Check if the user is already an attendee
    verifyIfUserIsNotAlreadyAnAttendee(stream, user.getId());
    // Save the stream to the repository
    streamRepository.save(stream);

    return stream;
  }

  /**
   * Handles the process of requesting to join a private or protected stream and returns a response.
   *
   * <p>This method verifies the user details and attempts to join the stream. It processes the request by checking the
   * eligibility of the user and stream, creating or retrieving the corresponding stream attendee entry, and handling
   * notifications. Finally, it returns a localized response with the attendance information and stream type details.</p>
   *
   * @param streamId the ID of the stream the user is attempting to join
   * @param requestToJoinStreamDto the data transfer object containing the request details for joining the stream
   * @param user the {@link FleenUser} who is requesting to join the stream
   * @return a {@link RequestToJoinStreamResponse} containing the details of the join request and related information
   */
  @Override
  @Transactional
  public RequestToJoinStreamResponse requestToJoinStream(final Long streamId, final RequestToJoinStreamDto requestToJoinStreamDto, final FleenUser user)
      throws FleenStreamNotFoundException, StreamAlreadyCanceledException, StreamAlreadyHappenedException,
        AlreadyRequestedToJoinStreamException, AlreadyApprovedRequestToJoinException {
    // Verify the user details and attempt to join the stream
    final TryToJoinPrivateOrProtectedStreamResponse tryToJoinResponse = tryToRequestToToJoinPrivateOrProtectedStream(streamId, requestToJoinStreamDto, user);
    // Extract the stream
    final FleenStream stream = tryToJoinResponse.stream();
    // Verify if the stream's type is the same as the stream type of the request
    isStreamTypeEqual(stream.getStreamType(), requestToJoinStreamDto.getStreamType());
    // Extract the attendee
    final StreamAttendee streamAttendee = tryToJoinResponse.attendee();
    // Extract the attendance info
    final AttendanceInfo attendanceInfo = tryToJoinResponse.attendanceInfo();
    // Get stream type info
    final StreamTypeInfo streamTypeInfo = streamMapper.toStreamTypeInfo(stream.getStreamType());

    // Send and save notifications
    sendJoinRequestNotificationForPrivateStream(stream, streamAttendee, user);
    // Return the localized response of the request to join the stream
    return localizer.of(RequestToJoinStreamResponse.of(stream.getStreamId(), attendanceInfo, streamTypeInfo, tryToJoinResponse));
  }

  /**
   * Handles a request to join a private or protected stream.
   *
   * <p>This method processes a user's request to join a specific private or protected stream by validating the
   * stream's details, ensuring the user's eligibility, and updating the stream attendee information. The stream
   * and attendee details are saved, and attendance information is generated for the response.</p>
   *
   * @param streamId the ID of the stream the user is requesting to join
   * @param requestToJoinStreamDto the {@link RequestToJoinStreamDto} containing the join request details (e.g., comment)
   * @param user the {@link FleenUser} attempting to join the stream
   * @return a {@link TryToJoinPrivateOrProtectedStreamResponse} containing the stream, the stream attendee, and
   *         attendance information
   */
  protected TryToJoinPrivateOrProtectedStreamResponse tryToRequestToToJoinPrivateOrProtectedStream(final Long streamId, final RequestToJoinStreamDto requestToJoinStreamDto, final FleenUser user) {
    // Find the stream by its ID
    final FleenStream stream = findStream(streamId);
    // Validate the stream details and eligibility of the user
    validateStreamAndUserForProtectedStream(stream, user);
    // Handle the join request and update stream attendee info
    final StreamAttendee streamAttendee = handleStreamAttendeeJoinRequestForProtectedStream(stream, requestToJoinStreamDto, user);
    // Save the stream and the attendee
    saveStreamAndAttendee(stream, streamAttendee);

    // Convert the stream to equivalent stream response
    final FleenStreamResponse streamResponse = streamMapper.toFleenStreamResponse(stream);
    // Get the attendance information for the stream attendee
    final AttendanceInfo attendanceInfo = streamMapper.toAttendanceInfo(streamResponse, streamAttendee.getRequestToJoinStatus(), streamAttendee.isAttending());
    // Return the localized response of the request to join the stream
    return TryToJoinPrivateOrProtectedStreamResponse.of(stream, streamAttendee, attendanceInfo);
  }


  /**
   * Validates the {@link FleenStream} and {@link FleenUser} for a protected stream.
   *
   * <p>This method performs several checks to ensure that the stream and user are valid for participation
   * in a protected stream. It verifies that the stream is not public, ensures the user is not the owner
   * (since owners are automatically members of the entity), checks whether the stream has already occurred,
   * verifies that the stream has not been canceled, and confirms that the user is not already an attendee.</p>
   *
   * @param stream the {@link FleenStream} to validate
   * @param user the {@link FleenUser} to validate against the stream
   * @throws FailedOperationException if the stream is public, already happened, is canceled, or the user
   *         is already an attendee.
   */
  protected void validateStreamAndUserForProtectedStream(final FleenStream stream, final FleenUser user) {
    // Check if the stream is public and halt the operation
    checkIsTrue(stream.isPublic(), FailedOperationException::new);
    // Verify if the user is the owner and fail the operation because the owner is automatically a member of an entity
    verifyIfUserIsAuthorOrCreatorOrOwnerTryingToPerformAction(Member.of(stream.getMemberId()), user);
    // Check if the stream is still active and can be joined.
    verifyStreamHasNotHappenedAlready(stream);
    // Check if stream is not cancelled
    verifyStreamIsNotCancelled(stream);
    // CHeck if the user is already an attendee
    verifyIfUserIsNotAlreadyAnAttendee(stream, user.getId());
  }

  /**
   * Handles the join request of a {@link StreamAttendee} for a protected stream.
   *
   * <p>This method retrieves an existing {@link StreamAttendee} for the specified {@link FleenUser}
   * and {@link FleenStream}, or creates a new one if none exists. If the stream is private, the attendee's
   * request to join status is set to pending. The method returns the {@link StreamAttendee}.</p>
   *
   * @param stream the {@link FleenStream} to which the attendee wants to join
   * @param requestToJoinStreamDto the DTO containing information such as a comment
   * @param user the {@link FleenUser} making the join request
   * @return the {@link StreamAttendee} associated with the user and stream
   */
  protected StreamAttendee handleStreamAttendeeJoinRequestForProtectedStream(final FleenStream stream, final RequestToJoinStreamDto requestToJoinStreamDto, final FleenUser user) {
    // Retrieve the stream attendee entry associated with the user or create a new StreamAttendee entry if none for the user
    final StreamAttendee streamAttendee = attendeeService.getExistingOrCreateNewStreamAttendee(stream, requestToJoinStreamDto.getComment(), user);
    // If the stream is private, set the request to join status to pending
    setAttendeeRequestToJoinPendingIfStreamIsPrivate(streamAttendee, stream);
    // Return the stream attendee
    return streamAttendee;
  }

  /**
   * Saves the given {@link FleenStream} and {@link StreamAttendee} to their respective repositories.
   *
   * <p>This method first saves the {@link StreamAttendee} to the attendee repository,
   * and then saves the {@link FleenStream} to the stream repository. It ensures that any changes
   * made to the attendee or stream are persisted in the database.</p>
   *
   * @param stream the {@link FleenStream} to be saved
   * @param streamAttendee the {@link StreamAttendee} to be saved
   */
  protected void saveStreamAndAttendee(final FleenStream stream, final StreamAttendee streamAttendee) {
    // Add the new StreamAttendee to the stream's attendees list and save
    streamAttendeeRepository.save(streamAttendee);
    // Save the stream to the repository
    streamRepository.save(stream);
  }

  /**
   * Sends a join request notification for a private stream to the stream's owner.
   *
   * <p>This method creates a notification for the owner of the stream when a user submits a request to join the private stream.
   * The notification is generated using the details of the {@link FleenStream}, the {@link StreamAttendee}, and the requesting {@link FleenUser},
   * and is then saved to the notification service.</p>
   *
   * @param stream the {@link FleenStream} the user is attempting to join
   * @param streamAttendee the {@link StreamAttendee} representing the user's join request
   * @param user the {@link FleenUser} who is attempting to join the stream
   */
  @Override
  public void sendJoinRequestNotificationForPrivateStream(final FleenStream stream, final StreamAttendee streamAttendee, final FleenUser user) {
    // Create and save notification
    final Notification notification = notificationMessageService.ofReceived(stream, streamAttendee, stream.getOrganizer(), user.toMember());
    // Save the notification
    notificationService.save(notification);
  }

  /**
   * Determines the join status of a user for a list of streams and updates the join status
   * in the corresponding FleenStreamResponse objects.
   *
   * <p>This method first validates the user and the responses. If the input is valid, it extracts
   * the stream IDs from the responses, finds the user's attendance history in the streams, and
   * updates the join status for each response based on the user's attendance information.</p>
   *
   * @param responses a list of FleenStreamResponse objects representing streams.
   * @param user the FleenUser for whom the join status is to be determined.
   */
  @Override
  public void determineUserJoinStatusForStream(final List<FleenStreamResponse> responses, final FleenUser user) {
    // Check the details and confirm they are valid
    final boolean isInputValid = isUserAndResponsesValid(responses, user);

    if (isInputValid) {
      // Extract the stream IDs from the search result views
      final List<Long> streamIds = extractAndGetStreamIds(responses);
      // Find the user attendance history in the streams
      final Map<Long, StreamAttendeeSelect> userAttendance = findUserAttendanceInStreams(user.toMember(), streamIds);
      // Update each stream's join status in the response views
      updateJoinStatusInResponses(responses, userAttendance);
    }
  }

  /**
   * Finds and returns a map of user attendance information for a given member in the specified streams.
   *
   * <p>The method first retrieves a list of StreamAttendeeSelect objects representing the user's attendance
   * in the streams, and then groups them by stream ID into a map.</p>
   *
   * @param member the Member for whom attendance is being checked.
   * @param streamIds a list of stream IDs where attendance information is requested.
   * @return a map where the key is the stream ID (Long) and the value is the StreamAttendeeSelect object
   *         containing attendance details for the specified member.
   */
  protected Map<Long, StreamAttendeeSelect> findUserAttendanceInStreams(final Member member, final List<Long> streamIds) {
    // Retrieve the list of attendance details for the given member in the specified streams
    final List<StreamAttendeeSelect> attendeeAttendance = findAttendeeAttendance(member, streamIds);
    // Group the attendance details by stream ID and return as a map
    return groupAttendeeAttendance(attendeeAttendance);
  }

  /**
   * Updates the join status of each stream response based on the provided attendance status map.
   *
   * <p>This method iterates over a list of FleenStreamResponse objects and updates their join status
   * according to the corresponding status found in the provided attendanceStatusMap.
   * If a stream response is non-null and has a corresponding status in the map,
   * the join status is updated with a label derived from the status.</p>
   *
   * @param responses A list of FleenStreamResponse objects representing the streams to be updated.
   * @param attendanceStatusMap A map containing attendance status keyed by stream number IDs.
   */
  protected void updateJoinStatusInResponses(final List<FleenStreamResponse> responses, final Map<Long, StreamAttendeeSelect> attendanceStatusMap) {
    if (nonNull(responses) && nonNull(attendanceStatusMap)) {
      // Update each stream's join status in the response views
      responses.stream()
        .filter(Objects::nonNull)
        .forEach(stream -> {
          // Retrieve the attendee status for a specific ID which can be null because the member has not join or requested to join the stream
          final Optional<StreamAttendeeSelect> existingAttendance = Optional.ofNullable(attendanceStatusMap.get(stream.getNumberId()));
          // If member is an attendee, retrieve the status and set view label
          existingAttendance.ifPresent(attendance -> {
            // Update the request to join status, join status and is attending info
            log.info("The join status is {}", attendance.getRequestToJoinStatus());
            streamMapper.update(stream, attendance.getRequestToJoinStatus(), attendance.getJoinStatus(), attendance.isAttending());
          });
      });
    }
  }

  /**
   * Adjusts the schedule of streams based on the user's timezone.
   *
   * <p>This method checks whether the input user and responses are valid. For each valid stream,
   * it compares the stream's timezone with the user's timezone. If they are different, it converts
   * the stream's schedule to match the user's timezone and updates the stream with the adjusted
   * schedule. If the timezones are the same, an empty schedule is set.</p>
   *
   * @param responses a collection of FleenStreamResponse objects representing the streams.
   * @param user the FleenUser whose timezone is used for schedule adjustment.
   */
  @Override
  public void setOtherScheduleBasedOnUserTimezone(final Collection<FleenStreamResponse> responses, final FleenUser user) {
    if (isUserAndResponsesValid(responses, user)) {
      responses.stream()
        .filter(Objects::nonNull)
        .forEach(stream -> {
          // Get the stream's original timezone
          final String streamTimezone = stream.getSchedule().getTimezone();
          // Get the user's timezone
          final String userTimezone = user.getTimezone();

          // Check if the stream's timezone and user's timezone are different
          if (!streamTimezone.equalsIgnoreCase(userTimezone)) {
            // Convert the stream's schedule to the user's timezone
            final Schedule otherSchedule = createSchedule(stream, userTimezone);
            // Set the converted dates and user's timezone in the stream's other schedule
            stream.setOtherSchedule(otherSchedule);
          } else {
            // If the timezones are the same, set an empty schedule
            stream.setOtherSchedule(Schedule.of());
          }
      });
    }
  }

  /**
   * Updates the request status of an attendee for a stream.
   *
   * <p>This method retrieves the requested status for the attendee to join the stream from the
   * provided DTO. It then updates the attendee's status and sets any organizer's comments
   * regarding the request.</p>
   *
   * @param streamAttendee the StreamAttendee object representing the attendee whose status is being updated.
   * @param processAttendeeRequestToJoinDto the {@link ProcessAttendeeRequestToJoinStreamDto} containing the new status and any comments from the organizer.
   */
  @Override
  public void updateAttendeeRequestStatus(final StreamAttendee streamAttendee, final ProcessAttendeeRequestToJoinStreamDto processAttendeeRequestToJoinDto) {
    // Retrieve the requested status for the attendee to join the stream
    final StreamAttendeeRequestToJoinStatus requestToJoinStatus = processAttendeeRequestToJoinDto.getActualJoinStatus();
    // Update the attendee's request status and set any organizer comments
    streamAttendee.updateRequestStatusAndSetOrganizerComment(requestToJoinStatus, processAttendeeRequestToJoinDto.getComment());
  }

  /**
   * Increases the total number of attendees or guests for a given stream and saves the updated stream.
   *
   * @param stream The stream where the number of attendees or guests is to be increased.
   */
  @Override
  @Transactional
  public FleenStream increaseTotalAttendeesOrGuestsAndSaveBecauseOfOrganizer(final FleenStream stream) {
    // Increase total attendees or guests in the stream
    stream.increaseTotalAttendees();
    // Save the updated stream to the repository
    return streamRepository.save(stream);
  }

  /**
   * Decreases the total number of attendees or guests for a given stream and saves the updated stream.
   *
   * @param stream The stream where the number of attendees or guests is to be decreased.
   */
  @Override
  @Transactional
  public void decreaseTotalAttendeesOrGuestsAndSave(final FleenStream stream) {
    // Decrease total attendees or guests in the stream
    stream.decreaseTotalAttendees();
    // Save the updated stream to the repository
    streamRepository.save(stream);
  }

  /**
   * Determines and sets various statuses and details for streams based on the user's context.
   *
   * <p>This method evaluates and updates the provided list of {@link FleenStreamResponse} objects
   * by determining the user's join status, the schedule status (live, past, or upcoming), and
   * adjusts schedule details based on the user's timezone.</p>
   *
   * @param views the list of stream responses to update with status and details.
   * @param user the user whose context will be used to adjust the statuses and schedule details.
   */
  @Override
  public void determineDifferentStatusesAndDetailsOfStreamBasedOnUser(final List<FleenStreamResponse> views, final FleenUser user) {
    // Determine the user's join status for each stream
    determineUserJoinStatusForStream(views, user);
    // Set other schedule details if user timezone is different
    setOtherScheduleBasedOnUserTimezone(views, user);
  }

  /**
   * Registers and approves the organizer of a stream as an attendee.
   *
   * <p>This method adds the stream organizer as an attendee of the stream and automatically
   * approves their request to join. The attendee is then saved to the repository for future
   * reference.</p>
   *
   * @param stream the FleenStream object representing the stream to which the organizer is joining.
   * @param user the FleenUser object representing the user (organizer) joining the stream as an attendee.
   */
  @Override
  @Transactional
  public void registerAndApproveOrganizerOfStreamAsAnAttendee(final FleenStream stream, final FleenUser user) {
    // Add the organizer as an attendee of the stream
    final StreamAttendee streamAttendee = StreamAttendee.of(user.toMember(), stream);
    // Approve the organizer's request to join automatically
    streamAttendee.approveUserAttendance();
    // Save attendee to the repository
    streamAttendeeRepository.save(streamAttendee);
  }

  /**
   * Retrieves the attendance records for the specified user across multiple streams.
   *
   * <p>This method fetches the attendance records for the given {@link Member} across the provided
   * list of stream IDs by querying the {@link StreamAttendeeRepository}. The result is a
   * list of {@link StreamAttendeeSelect} objects representing the user's attendance status for each stream.</p>
   *
   * @param member the {@link Member} whose attendance records are being retrieved
   * @param streamIds the list of stream IDs to check for the user's attendance
   * @return a list of {@link StreamAttendeeSelect} objects representing the user's attendance for each stream
   */
  protected List<StreamAttendeeSelect> findAttendeeAttendance(final Member member, final List<Long> streamIds) {
    // Retrieve the user's attendance records for the provided stream IDs
    return streamAttendeeRepository.findByMemberAndStreamIds(member, streamIds);
  }

  /**
   * Validates that the user and the list of responses are not null.
   *
   * <p>This method checks if the user, the member object derived from the user, and the list of
   * responses are all non-null to ensure the data is valid for further processing.</p>
   *
   * @param responses The list of FleenStreamResponse objects to be validated.
   * @param user The FleenUser object to be validated.
   * @return true if the user, user's member, and the responses are non-null; otherwise false.
   */
  protected boolean isUserAndResponsesValid(final Collection<FleenStreamResponse> responses, final FleenUser user) {
    // Check if user is non-null, user's member is non-null, and responses list is non-null
    return nonNull(user) && nonNull(user.toMember()) && nonNull(responses);
  }

  /**
   * Verifies if the given user is the owner and throws an exception if the user is attempting to perform an action
   * they are not authorized to perform.
   *
   * <p>This method checks if both the {@link Member} owner and the {@link FleenUser} are provided.
   * If the user is the owner (i.e., their user IDs match), a {@link FailedOperationException} is thrown.</p>
   *
   * @param owner the member who is the owner of the resource; must not be null
   * @param user  the user attempting to perform the action; must not be null
   * @throws FailedOperationException if the user is the owner of the resource
   */
  public static void verifyIfUserIsAuthorOrCreatorOrOwnerTryingToPerformAction(final Member owner, final FleenUser user) throws FailedOperationException {
    // Check if both the owner and user are provided
    if (nonNull(owner) && nonNull(user)) {
      // Retrieve the ID of the owner of a resource
      final Long ownerUserId = owner.getMemberId();
      // Get the user's ID
      final Long userId = user.getId();
      // If the user is the owner, throw an exception
      if (ownerUserId.equals(userId)) {
        throw new FailedOperationException();
      }
    }
  }

  /**
   * Validates if the given user is the creator of the specified stream and throws an exception if they are not.
   *
   * <p>This method first checks if either the {@link FleenStream} or {@link FleenUser} is null,
   * throwing a {@link FailedOperationException} if any of the values are null.
   * Then, it checks if the creator's ID of the stream matches the user's ID.
   * If the user is not the creator of the stream, a {@link StreamNotCreatedByUserException} is thrown.</p>
   *
   * @param stream the stream whose creator needs to be validated; must not be null
   * @param user   the user to validate as the creator of the stream; must not be null
   * @throws FailedOperationException if either the stream or user is null
   * @throws StreamNotCreatedByUserException if the user is not the creator of the stream
   */
  public static void validateCreatorOfStream(final FleenStream stream, final FleenUser user) {
    // Throw an exception if the any of the provided values is null
    checkIsNullAny(Set.of(stream, user), FailedOperationException::new);

    // Check if the stream creator's ID matches the user's ID
    final boolean isSame = Objects.equals(stream.getMemberId(), user.getId());
    if (!isSame) {
      throw StreamNotCreatedByUserException.of(user.getId());
    }
  }

  /**
   * Verifies the details of a given stream to ensure it is valid for further processing.
   *
   * <p>This method performs several checks on the provided stream: it validates if the user is the creator of the stream,
   * checks if the stream's scheduled end date has not passed, and verifies that the stream is not cancelled.</p>
   *
   * @param stream the FleenStream to be verified
   * @param user   the FleenUser to be validated as the creator of the stream
   */
  public static void verifyStreamDetails(final FleenStream stream, final FleenUser user) {
    // Validate if the user is the creator of the stream
    validateCreatorOfStream(stream, user);
    // Check if the stream is still active and can be joined.
    verifyStreamHasNotHappenedAlready(stream);
    // Verify the stream is not cancelled
    verifyStreamIsNotCancelled(stream);
  }

  /**
   * Creates a schedule for a stream adjusted to the user's timezone.
   *
   * <p>This method checks if the stream and user timezone are valid. If so, it retrieves the
   * stream's original start and end dates, converts them to the user's timezone, and returns
   * the converted schedule with the adjusted dates. If either the stream or user timezone is
   * invalid (null), an empty schedule is returned.</p>
   *
   * @param stream the FleenStreamResponse object representing the stream.
   * @param userTimezone the timezone of the user to which the schedule will be adjusted.
   * @return a Schedule object with the adjusted start and end dates in the user's timezone.
   */
  protected static Schedule createSchedule(final FleenStreamResponse stream, final String userTimezone) {
    if (nonNull(stream) && nonNull(userTimezone)) {
      // Get the stream's original timezone
      final String streamTimezone = stream.getSchedule().getTimezone();

      // Retrieve the start dates from the stream's schedule
      final LocalDateTime startDate = stream.getSchedule().getStartDate();
      // Retrieve the end dates from the stream's schedule
      final LocalDateTime endDate = stream.getSchedule().getEndDate();

      // Convert the stream's start date to the user's timezone
      final LocalDateTime userStartDate = convertToTimezone(startDate, streamTimezone, userTimezone);
      // Convert the stream's end date to the user's timezone
      final LocalDateTime userEndDate = convertToTimezone(endDate, streamTimezone, userTimezone);
      // Return the schedule with the dates in the user's timezone
      return Schedule.of(userStartDate, userEndDate, userTimezone);
    }
    // If the stream or userTimezone is null, return an empty schedule
    return Schedule.of();
  }

  /**
   * Verifies that the provided stream has not already ended.
   *
   * <p>This method checks if the stream has already ended by evaluating its end date. If the stream has ended,
   * it throws a {@link StreamAlreadyHappenedException}. If the provided stream is null, a {@link FailedOperationException} is thrown.</p>
   *
   * @param stream the {@link FleenStream} to check for its end status
   * @throws FailedOperationException if the provided stream is null
   * @throws StreamAlreadyHappenedException if the stream has already ended
   */
  protected static void verifyStreamHasNotHappenedAlready(final FleenStream stream) {
    // Throw an exception if the provided value is null
    checkIsNull(stream, FailedOperationException::new);

    // Check if the stream has already ended and throw an exception if true
    if (stream.hasEnded()) {
      throw new StreamAlreadyHappenedException(stream.getStreamId(), stream.getScheduledEndDate());
    }
  }

  /**
   * Verifies that the provided stream is not canceled.
   *
   * <p>This method checks whether the provided stream has been canceled. If the stream is canceled,
   * it throws a {@link StreamAlreadyCanceledException}. If the stream is null, a {@link FailedOperationException} is thrown.</p>
   *
   * @param stream the {@link FleenStream} to check for cancellation status
   * @throws FailedOperationException if the provided stream is null
   * @throws StreamAlreadyCanceledException if the stream has been canceled
   */
  protected static void verifyStreamIsNotCancelled(final FleenStream stream) throws FailedOperationException {
    // Throw an exception if the provided stream is null
    checkIsNull(stream, FailedOperationException::new);

    if (stream.isCanceled()) {
      throw StreamAlreadyCanceledException.of(stream.getStreamId());
    }
  }

  /**
   * Extracts and retrieves the stream IDs from a list of stream responses.
   *
   * <p>This method filters out any null stream responses and maps the remaining responses to their corresponding
   * stream IDs. If the input list is null, it returns an empty list.</p>
   *
   * @param streams the list of {@link FleenStreamResponse} objects containing stream details
   * @return a list of stream IDs extracted from the provided stream responses, or an empty list if the input list is null
   */
  protected static List<Long> extractAndGetStreamIds(final List<FleenStreamResponse> streams) {
    // Filter null responses and map to the corresponding stream ID
    if (nonNull(streams)) {
      return streams.stream()
        .filter(Objects::nonNull)
        .map(FleenFeenResponse::getNumberId)
        .toList();
    }
    // Return an empty list if the input list is null
    return List.of();
  }

  /**
   * Checks if the given stream is private and throws an exception if it is.
   *
   * <p>If the stream is private, this method throws a {@link CannotJointStreamWithoutApprovalException}
   * with the provided stream ID. The exception indicates that joining a private stream requires approval.</p>
   *
   * @param streamId the ID of the stream associated with the stream
   * @param stream  the stream to check; if the stream is private, an exception will be thrown
   * @throws CannotJointStreamWithoutApprovalException if the stream is private
   */
  protected static void checkIfStreamIsPrivate(final Long streamId, final FleenStream stream) {
    if (nonNull(stream) && stream.isJustPrivate()) {
      throw CannotJointStreamWithoutApprovalException.of(streamId);
    }
  }

  /**
   * Verifies if the given stream is ongoing.
   *
   * <p>This method checks whether the specified stream is currently ongoing. If the stream is ongoing, it throws an exception,
   * preventing any actions like cancellation or deletion.</p>
   *
   * @param streamId the unique identifier of the stream being checked
   * @param stream the {@link FleenStream} representing the stream to be verified
   * @throws CannotCancelOrDeleteOngoingStreamException if the stream is ongoing, preventing cancellation or deletion
   */
  public static void verifyIfStreamIsOngoing(final Long streamId, final FleenStream stream) throws CannotCancelOrDeleteOngoingStreamException {
    // Verify if the stream is still ongoing
    checkIsTrue(stream.isOngoing(), CannotCancelOrDeleteOngoingStreamException.of(streamId));
  }

  /**
   * Sets the request-to-join status of the attendee to pending if the stream is private.
   *
   * <p>If the provided {@link StreamAttendee} and {@link FleenStream} is not null and the stream is private,
   * this method sets the attendee's request-to-join status to pending by calling {@link StreamAttendee#markRequestAsPending()}.</p>
   *
   * @param streamAttendee the attendee whose request-to-join status may be updated; can be null
   * @param stream         the stream to check for privacy status
   */
  protected static void setAttendeeRequestToJoinPendingIfStreamIsPrivate(final StreamAttendee streamAttendee, final FleenStream stream) {
    if (nonNull(streamAttendee) && nonNull(stream) && stream.isPrivate()) {
      // If the stream is private, set the request-to-join status to pending
      streamAttendee.markRequestAsPending();
    }
  }

  /**
   * Verifies whether a user is already an attendee of the specified stream.
   *
   * <p>This method checks if the user has previously requested to join the stream and whether that request is pending
   * or already approved. If the user has a pending request or is already approved and attending, an exception will be thrown.</p>
   *
   * @param stream the {@link FleenStream} representing the event the user wants to join
   * @param userId the unique identifier of the user
   * @throws AlreadyRequestedToJoinStreamException if the user has already requested to join the stream
   * @throws AlreadyApprovedRequestToJoinException if the user's request to join the stream has been approved and they are attending
   * @throws FailedOperationException if any of the provided values is {@code null}
   */
  protected void verifyIfUserIsNotAlreadyAnAttendee(final FleenStream stream, final Long userId)
      throws AlreadyRequestedToJoinStreamException, AlreadyApprovedRequestToJoinException, FailedOperationException {
    // Throw an exception if the any of the provided values is null
    checkIsNullAny(Set.of(stream, userId), FailedOperationException::new);

    // Try to check if the user is an existing attendee
    final Optional<StreamAttendee> existingAttendee = attendeeService.findAttendee(stream, userId);
    // If the user is found as an attendee, throw an exception with the attendee's request to join status
    existingAttendee
      .ifPresent(streamAttendee -> {
        // Retrieve the attendee request to join status
        final String requestToJoinStatus = streamAttendee.getJoinStatus();

        if (streamAttendee.isRequestToJoinPending()) {
          // If the user is found as an attendee and the request is pending, throw an exception with the attendee's request to join status
          throw AlreadyRequestedToJoinStreamException.of(requestToJoinStatus);
        } else if (streamAttendee.isRequestToJoinApproved() && streamAttendee.isAttending()) {
          // If the user is found as an attendee and the request is approved, throw an exception with the attendee's request to join status
          throw AlreadyApprovedRequestToJoinException.of(requestToJoinStatus);
        }
    });
  }

  /**
   * Verifies if the provided stream type is equal to the original stream type.
   *
   * <p>This method checks whether the given {@code streamType} is equal to
   * the {@code originalStreamType}. If the {@code originalStreamType} is null or
   * not equal to the {@code originalStreamType}, a {@link FailedOperationException}
   * is thrown.</p>
   *
   * @param originalStreamType the original {@link StreamType} to compare against
   * @param streamType the {@link StreamType} to verify
   * @throws FailedOperationException if the {@code streamType} is null or not equal
   *                                  to the {@code originalStreamType}
   */
  public static void isStreamTypeEqual(final StreamType originalStreamType, final StreamType streamType) {
    if (originalStreamType == null || originalStreamType != streamType) {
      throw new FailedOperationException();
    }
  }

}
