package com.fleencorp.feen.stream.service.impl.core;

import com.fleencorp.feen.calendar.exception.core.CalendarNotFoundException;
import com.fleencorp.feen.calendar.model.domain.Calendar;
import com.fleencorp.feen.common.exception.FailedOperationException;
import com.fleencorp.feen.common.service.misc.MiscService;
import com.fleencorp.feen.oauth2.constant.Oauth2ServiceType;
import com.fleencorp.feen.oauth2.exception.core.Oauth2InvalidAuthorizationException;
import com.fleencorp.feen.oauth2.model.domain.Oauth2Authorization;
import com.fleencorp.feen.oauth2.service.external.GoogleOauth2Service;
import com.fleencorp.feen.shared.member.contract.IsAMember;
import com.fleencorp.feen.shared.security.RegisteredUser;
import com.fleencorp.feen.stream.constant.core.StreamType;
import com.fleencorp.feen.stream.exception.core.StreamNotFoundException;
import com.fleencorp.feen.stream.exception.request.AlreadyApprovedRequestToJoinException;
import com.fleencorp.feen.stream.exception.request.AlreadyRequestedToJoinStreamException;
import com.fleencorp.feen.stream.model.domain.FleenStream;
import com.fleencorp.feen.stream.model.domain.StreamAttendee;
import com.fleencorp.feen.stream.model.holder.StreamOtherDetailsHolder;
import com.fleencorp.feen.stream.model.response.common.DataForRescheduleStreamResponse;
import com.fleencorp.feen.stream.service.attendee.StreamAttendeeOperationsService;
import com.fleencorp.feen.stream.service.common.StreamOperationsService;
import com.fleencorp.feen.stream.service.core.StreamService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;

import static com.fleencorp.base.util.ExceptionUtil.checkIsNull;
import static com.fleencorp.base.util.ExceptionUtil.checkIsNullAny;
import static com.fleencorp.feen.common.validator.impl.TimezoneValidValidator.getAvailableTimezones;
import static java.util.Objects.nonNull;

/**
 * Implementation of the {@link StreamService} interface for managing stream-related operations.
 *
 * <p>This class provides services related to stream management, such as handling stream creation, updating stream
 * information, managing stream attendees, and sending notifications regarding streams. It interacts with repositories
 * for stream and attendee data, as well as notification services to keep users informed about streams.</p>
 *
 * <p>Common responsibilities of this service include creating, updating, and deleting streams, managing attendees'
 * participation in streams, and sending notifications to users based on streams.</p>
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
  private final StreamAttendeeOperationsService streamAttendeeOperationsService;
  private final StreamOperationsService streamOperationsService;

  public StreamServiceImpl(
      final GoogleOauth2Service googleOauth2Service,
      final MiscService miscService,
      @Lazy final StreamAttendeeOperationsService streamAttendeeOperationsService,
      @Lazy final StreamOperationsService streamOperationsService) {
    this.googleOauth2Service = googleOauth2Service;
    this.miscService = miscService;
    this.streamAttendeeOperationsService = streamAttendeeOperationsService;
    this.streamOperationsService = streamOperationsService;
  }

  /**
   * Finds a stream by its ID or throws an exception if the stream is not found.
   *
   * <p>This method attempts to retrieve a {@link FleenStream} from the repository using the provided stream ID.
   * If no stream is found, it throws a {@link StreamNotFoundException} with the given ID.</p>
   *
   * @param streamId the ID of the stream to find; must not be null
   * @return the {@link FleenStream} corresponding to the given ID
   * @throws StreamNotFoundException if no stream is found with the provided ID
   */
  @Override
  public FleenStream findStream(final Long streamId) throws StreamNotFoundException {
    return streamOperationsService.findById(streamId)
      .orElseThrow(StreamNotFoundException.of(streamId));
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
  public void verifyStreamDetailAllDetails(final FleenStream stream, final RegisteredUser user) {
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

      return;
    }
    throw new FailedOperationException();
  }

  /**
   * Validates the {@link FleenStream} and {@link RegisteredUser} for a protected stream.
   *
   * <p>This method performs several checks to ensure that the stream and user are valid for participation
   * in a protected stream. It verifies that the stream is not public, ensures the user is not the owner
   * (since owners are automatically members of the entity), checks whether the stream has already occurred,
   * verifies that the stream has not been canceled, and confirms that the user is not already an attendee.</p>
   *
   * @param stream the {@link FleenStream} to validate
   * @param user the {@link RegisteredUser} to validate against the stream
   * @throws FailedOperationException if the stream is public, already happened, is canceled, or the user
   *         is already an attendee.
   */
  @Override
  public void validateStreamAndUserForProtectedStream(final FleenStream stream, final RegisteredUser user) {
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
      decreaseTotalAttendeesOrGuests(stream);
      // If an attendee record exists, update their attendance status to false
      attendee.markAsNotAttending();
      // Save the updated attendee record
      streamAttendeeOperationsService.save(attendee);
    }
  }

  /**
   * Increase the total number of attendees or guests for a given stream and saves the updated stream.
   *
   * @param stream The stream where the number of attendees or guests is to be increased.
   */
  @Override
  @Transactional
  public void increaseTotalAttendeesOrGuests(final FleenStream stream) {
    // Increase total attendees or guests in the stream
    streamOperationsService.updateTotalAttendeeCount(stream.getStreamId(), true);
  }

  /**
   * Decreases the total number of attendees or guests for a given stream and saves the updated stream.
   *
   * @param stream The stream where the number of attendees or guests is to be decreased.
   */
  @Transactional
  public void decreaseTotalAttendeesOrGuests(final FleenStream stream) {
    // Decrease total attendees or guests in the stream
    streamOperationsService.updateTotalAttendeeCount(stream.getStreamId(), false);
  }

  /**
   * Registers and approves the organizer of a stream as an attendee.
   *
   * <p>This method adds the stream organizer as an attendee of the stream and automatically
   * approves their request to join. The attendee is then saved to the repository for future
   * reference. Also mark the attendee as an organizer and speaker in the stream.</p>
   *
   * @param stream the FleenStream object representing the stream to which the organizer is joining.
   * @param user the FleenUser object representing the user (organizer) joining the stream as an attendee.
   */
  @Override
  @Transactional
  public void registerAndApproveOrganizerOfStreamAsAnAttendee(final FleenStream stream, final RegisteredUser user) {
    // Add the organizer as an attendee of the stream
    final StreamAttendee streamAttendee = StreamAttendee.of(user.toMember(), stream);
    // Approve the organizer's request to join automatically
    streamAttendee.approveUserAttendance();
    // Mark the organizer as a speaker
    streamAttendee.markAsOrganizer();
    // Save attendee to the repository
    streamAttendeeOperationsService.save(streamAttendee);
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
  public static void verifyStreamDetails(final FleenStream stream, final RegisteredUser user) {
    stream.checkIsOrganizer(user.getId());
    stream.checkNotEnded();
    stream.checkNotCancelled();
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
    final Optional<StreamAttendee> existingAttendee = streamAttendeeOperationsService.findAttendeeByMemberId(stream, userId);
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
   * @throws CalendarNotFoundException           if the external calendar associated with the stream cannot be found
   * @throws Oauth2InvalidAuthorizationException   if the OAuth2 authorization required for external services is invalid
   * @throws FailedOperationException if the stream is {@code null}
   */
  @Override
  @Transactional
  public StreamOtherDetailsHolder retrieveStreamOtherDetailsHolder(final FleenStream stream, final RegisteredUser user)
      throws CalendarNotFoundException, Oauth2InvalidAuthorizationException, FailedOperationException {
    checkIsNull(stream, FailedOperationException::new);

    // Retrieve the stream type
    final StreamType streamType = stream.getStreamType();

    // If the stream is an event, retrieve the calendar and handle external cancellation
    final Calendar calendar = StreamType.isEvent(streamType)
      ? miscService.findCalendarByStreamType(user.getCountry(), streamType) : null;

    // If the stream is a broadcast or live stream, retrieve the oauth2 authorization
    final Oauth2Authorization oauth2Authorization = StreamType.isLiveStream(streamType)
      ? googleOauth2Service.validateAccessTokenExpiryTimeOrRefreshToken(Oauth2ServiceType.youTube(), user) : null;

    return StreamOtherDetailsHolder.of(calendar, oauth2Authorization);
  }

  /**
   * Checks whether both the viewer and the target member have attended at least one common stream.
   *
   * <p>This method delegates to the {@code streamParticipationRepository} to verify the existence
   * of any mutual stream participation between the two members.</p>
   *
   * @param viewer the member initiating the profile view or request
   * @param target the target member being viewed or evaluated
   * @return {@code true} if both members have attended at least one shared stream, {@code false} otherwise
   */
  @Override
  public boolean existsByAttendees(final IsAMember viewer, final IsAMember target) {
    return streamOperationsService.existsByAttendees(viewer.getMemberId(), target.getMemberId());
  }
}
