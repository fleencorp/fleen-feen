package com.fleencorp.feen.service.impl.stream.base;

import com.fleencorp.feen.constant.external.google.oauth2.Oauth2ServiceType;
import com.fleencorp.feen.constant.stream.StreamType;
import com.fleencorp.feen.exception.base.FailedOperationException;
import com.fleencorp.feen.exception.calendar.CalendarNotFoundException;
import com.fleencorp.feen.exception.google.oauth2.Oauth2InvalidAuthorizationException;
import com.fleencorp.feen.exception.stream.FleenStreamNotFoundException;
import com.fleencorp.feen.exception.stream.core.StreamAlreadyCanceledException;
import com.fleencorp.feen.exception.stream.core.StreamAlreadyHappenedException;
import com.fleencorp.feen.exception.stream.core.StreamNotCreatedByUserException;
import com.fleencorp.feen.exception.stream.join.request.AlreadyApprovedRequestToJoinException;
import com.fleencorp.feen.exception.stream.join.request.AlreadyRequestedToJoinStreamException;
import com.fleencorp.feen.mapper.stream.StreamMapper;
import com.fleencorp.feen.model.domain.auth.Oauth2Authorization;
import com.fleencorp.feen.model.domain.calendar.Calendar;
import com.fleencorp.feen.model.domain.stream.FleenStream;
import com.fleencorp.feen.model.domain.stream.StreamAttendee;
import com.fleencorp.feen.model.domain.user.Member;
import com.fleencorp.feen.model.holder.StreamOtherDetailsHolder;
import com.fleencorp.feen.model.other.Schedule;
import com.fleencorp.feen.model.projection.stream.attendee.StreamAttendeeSelect;
import com.fleencorp.feen.model.response.base.FleenFeenResponse;
import com.fleencorp.feen.model.response.stream.FleenStreamResponse;
import com.fleencorp.feen.model.response.stream.common.DataForRescheduleStreamResponse;
import com.fleencorp.feen.model.security.FleenUser;
import com.fleencorp.feen.repository.stream.FleenStreamRepository;
import com.fleencorp.feen.repository.stream.StreamAttendeeRepository;
import com.fleencorp.feen.service.common.MiscService;
import com.fleencorp.feen.service.external.google.oauth2.GoogleOauth2Service;
import com.fleencorp.feen.service.stream.attendee.StreamAttendeeService;
import com.fleencorp.feen.service.stream.common.StreamService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

import static com.fleencorp.base.util.ExceptionUtil.checkIsNull;
import static com.fleencorp.base.util.ExceptionUtil.checkIsNullAny;
import static com.fleencorp.feen.service.impl.stream.attendee.StreamAttendeeServiceImpl.groupAttendeeAttendance;
import static com.fleencorp.feen.util.DateTimeUtil.convertToTimezone;
import static com.fleencorp.feen.validator.impl.TimezoneValidValidator.getAvailableTimezones;
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

  private final GoogleOauth2Service googleOauth2Service;
  private final MiscService miscService;
  private final StreamAttendeeService attendeeService;
  private final FleenStreamRepository streamRepository;
  private final StreamAttendeeRepository streamAttendeeRepository;
  private final StreamMapper streamMapper;

  /**
   * Constructor for {@link StreamServiceImpl} class.
   *
   * <p>This constructor initializes the required dependencies for the {@link StreamServiceImpl} class, which are injected
   * through the constructor. The dependencies include services for notifications, attendee management, stream data repository,
   * and mapping for stream-related objects.</p>
   *
   * @param attendeeService the service responsible for handling stream attendees
   * @param streamRepository the repository for managing stream data
   * @param streamAttendeeRepository the repository for managing stream attendee data
   * @param streamMapper the mapper for converting stream-related objects to different representations
   */
  public StreamServiceImpl(
      final GoogleOauth2Service googleOauth2Service,
      final MiscService miscService,
      @Lazy final StreamAttendeeService attendeeService,
      final FleenStreamRepository streamRepository,
      final StreamAttendeeRepository streamAttendeeRepository,
      final StreamMapper streamMapper) {
    this.googleOauth2Service = googleOauth2Service;
    this.miscService = miscService;
    this.attendeeService = attendeeService;
    this.streamRepository = streamRepository;
    this.streamAttendeeRepository = streamAttendeeRepository;
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
   * Retrieves the necessary data for rescheduling a stream, including the available time zones.
   *
   * <p>This method fetches the available time zones from the system and returns a response object
   * that contains the time zone details needed for rescheduling a stream.</p>
   *
   * @return a {@link DataForRescheduleStreamResponse} object containing the time zones available for rescheduling the stream
   */
  @Override
  public DataForRescheduleStreamResponse getDataForRescheduleStream() {
    // Retrieve the available timezones from the system
    final Set<String> timezones = getAvailableTimezones();
    // Return the response containing the details to reschedule stream
    return DataForRescheduleStreamResponse.of(timezones);
  }

  /**
   * Verifies the details of the stream and checks whether the user can join the stream.
   *
   * <p>This method begins by ensuring that the provided {@link FleenStream} is not {@code null}.
   * It checks whether the user is the owner or author of the stream, as owners are automatically
   * members and cannot request to join.</p>
   *
   * <p>The method then verifies that the stream has not been canceled and has not already occurred.
   * If the stream is private, it performs additional checks to handle private access, and finally,
   * ensures that the user is not already listed as an attendee.</p>
   *
   * <p>If any of these conditions are not met, an appropriate exception is thrown to indicate the failure.</p>
   *
   * @param stream the stream to verify
   * @param user   the user attempting to join the stream
   * @throws FailedOperationException if the verification fails
   */
  @Override
  public void verifyStreamDetailAllDetails(final FleenStream stream, final FleenUser user) {
    if (nonNull(stream)) {
      // Verify if the user is the owner and fail the operation because the owner is automatically a member of an entity
      stream.checkIsNotOrganizer(user.getId());
      // Verify stream is not canceled
      stream.checkNotCancelled();
      // Check if the stream is still active and can be joined.
      stream.checkNotEnded();
      // Check if the stream is private
      stream.checkNotPrivateForJoining();
      // Check if the user is already an attendee
      verifyIfUserIsNotAlreadyAnAttendee(stream, user.getId());
    }
    throw new FailedOperationException();
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
  @Override
  public void validateStreamAndUserForProtectedStream(final FleenStream stream, final FleenUser user) {
    // Check if the stream is public and halt the operation
    stream.checkIsPublicForRequestToJoin();
    // Verify if the user is the owner and fail the operation because the owner is automatically a member of an entity
    stream.checkIsNotOrganizer(user.getId());
    // Check if the stream is still active and can be joined.
    stream.checkNotEnded();
    // Check if stream is not cancelled
    stream.checkNotCancelled();
    // CHeck if the user is already an attendee
    verifyIfUserIsNotAlreadyAnAttendee(stream, user.getId());
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
   * Processes the attendee's decision to not attend the stream.
   *
   * <p>This method handles the case where an attendee who was previously marked as attending
   * decides not to attend the stream. It decreases the total number of attendees for the stream,
   * updates the attendee's attendance status to indicate they are no longer attending, and saves
   * the updated attendee record.</p>
   *
   * @param stream   the stream for which the attendee's attendance is being processed.
   * @param attendee the attendee who is no longer attending the stream.
   */
  @Override
  @Transactional
  public void processNotAttendingStream(final FleenStream stream, final StreamAttendee attendee) {
    if (nonNull(attendee) && attendee.isAttending()) {
      // Decrease the total number of attendees to stream
      decreaseTotalAttendeesOrGuestsAndSave(stream);
      // If an attendee record exists, update their attendance status to false
      attendee.markAsNotAttending();
      // Save the updated attendee record
      streamAttendeeRepository.save(attendee);
    }
  }

  /**
   * Increase the total number of attendees or guests for a given stream and saves the updated stream.
   *
   * @param stream The stream where the number of attendees or guests is to be increased.
   */
  @Override
  @Transactional
  public void increaseTotalAttendeesOrGuestsAndSave(final FleenStream stream) {
    // Increase total attendees or guests in the stream
    streamRepository.incrementTotalAttendees(stream.getStreamId());
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
    streamRepository.decrementTotalAttendees(stream.getStreamId());
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
    final boolean isSame = Objects.equals(stream.getOrganizerId(), user.getId());
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
    stream.checkIsOrganizer(user.getId());
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
  protected static void verifyStreamHasNotHappenedAlready(final FleenStream stream) throws StreamAlreadyHappenedException, FailedOperationException {
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
  protected static void verifyStreamIsNotCancelled(final FleenStream stream) throws StreamAlreadyCanceledException, FailedOperationException {
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
   * Sets the request-to-join status of the attendee to pending if the stream is private.
   *
   * <p>If the provided {@link StreamAttendee} and {@link FleenStream} is not null and the stream is private,
   * this method sets the attendee's request-to-join status to pending by calling {@link StreamAttendee#markRequestAsPending()}.</p>
   *
   * @param streamAttendee the attendee whose request-to-join status may be updated; can be null
   * @param stream         the stream to check for privacy status
   */
  protected static void setAttendeeRequestToJoinPendingIfStreamIsPrivate(final StreamAttendee streamAttendee, final FleenStream stream) {
    if (nonNull(streamAttendee) && nonNull(stream) && stream.isPrivateOrProtected()) {
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
   * @param stream the {@link FleenStream} representing the stream the user wants to join
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
    final Optional<StreamAttendee> existingAttendee = attendeeService.findAttendeeByMemberId(stream, userId);
    // If the user is found as an attendee, throw an exception with the attendee's request to join status
    existingAttendee
      .ifPresent(streamAttendee -> {
        // Retrieve the attendee request to join status
        final String requestToJoinStatusLabel = streamAttendee.getRequestToJoinStatus().getValue();

        if (streamAttendee.isRequestToJoinPending()) {
          // If the user is found as an attendee and the request is pending, throw an exception with the attendee's request to join status
          throw AlreadyRequestedToJoinStreamException.of(requestToJoinStatusLabel);
        } else if (streamAttendee.isRequestToJoinApproved() && streamAttendee.isAttending()) {
          // If the user is found as an attendee and the request is approved, throw an exception with the attendee's request to join status
          throw AlreadyApprovedRequestToJoinException.of(requestToJoinStatusLabel);
        }
    });
  }

  /**
   * Retrieves additional details related to the stream, such as calendar information and OAuth2 authorization,
   * based on the stream type and user information.
   *
   * <p>The method first checks if the provided {@link FleenStream} is {@code null}, throwing a
   * {@link FailedOperationException} if it is. Then, it retrieves the {@link StreamType} to determine
   * the appropriate external details to fetch.</p>
   *
   * <p>If the stream is of type event, the method retrieves the associated {@link Calendar} from an external
   * service based on the user's country and the stream type. If the stream is a live or broadcast stream,
   * it retrieves the {@link Oauth2Authorization} by checking the validity of the user's YouTube access token,
   * refreshing it if necessary.</p>
   *
   * @param stream the stream for which additional details are being retrieved
   * @param user   the user associated with the stream
   * @return a {@link StreamOtherDetailsHolder} object containing the retrieved calendar and OAuth2 authorization details
   * @throws FailedOperationException if the stream is {@code null}
   */
  @Override
  @Transactional
  public StreamOtherDetailsHolder retrieveStreamOtherDetailsHolder(final FleenStream stream, final FleenUser user)
      throws CalendarNotFoundException, Oauth2InvalidAuthorizationException, FailedOperationException {
    checkIsNull(stream, FailedOperationException::new);

    // Retrieve the stream type
    final StreamType streamType = stream.getStreamType();

    // If the stream is an event, retrieve the calendar and handle external cancellation
    final Calendar calendar = StreamType.isEvent(streamType)
      ? miscService.findCalendar(user.getCountry(), streamType) : null;

    // If the stream is a broadcast or live stream, retrieve the oauth2 authorization
    final Oauth2Authorization oauth2Authorization = StreamType.isLiveStream(streamType)
      ? googleOauth2Service.validateAccessTokenExpiryTimeOrRefreshToken(Oauth2ServiceType.youTube(), user) : null;

    return StreamOtherDetailsHolder.of(calendar, oauth2Authorization);
  }

}
