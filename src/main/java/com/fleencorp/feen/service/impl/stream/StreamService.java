package com.fleencorp.feen.service.impl.stream;

import com.fleencorp.feen.constant.stream.StreamAttendeeRequestToJoinStatus;
import com.fleencorp.feen.constant.stream.StreamVisibility;
import com.fleencorp.feen.exception.stream.*;
import com.fleencorp.feen.model.domain.stream.FleenStream;
import com.fleencorp.feen.model.domain.stream.StreamAttendee;
import com.fleencorp.feen.model.response.stream.StreamAttendeeResponse;
import com.fleencorp.feen.model.security.FleenUser;
import com.fleencorp.feen.repository.stream.FleenStreamRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.fleencorp.base.util.ExceptionUtil.checkIsNull;
import static com.fleencorp.base.util.ExceptionUtil.checkIsNullAny;
import static com.fleencorp.feen.constant.stream.StreamAttendeeRequestToJoinStatus.PENDING;
import static java.util.Objects.nonNull;

@Service
public class StreamService {

  private final FleenStreamRepository fleenStreamRepository;

  public StreamService(final FleenStreamRepository fleenStreamRepository) {
    this.fleenStreamRepository = fleenStreamRepository;
  }

  /**
   * Converts a set of {@link StreamAttendee} entities to a set of {@link StreamAttendeeResponse} objects.
   *
   * <p>This method transforms each {@code StreamAttendee} entity into a corresponding {@code StreamAttendeeResponse},
   * which includes the attendee's ID and full name. If the provided set of {@code StreamAttendee} is {@code null},
   * an empty set is returned.</p>
   *
   * @param streamAttendees the set of {@code StreamAttendee} entities to convert.
   * @return a set of {@code StreamAttendeeResponse} objects or an empty set if the input is {@code null}.
   */
  public Set<StreamAttendeeResponse> toStreamAttendeeResponses(final Set<StreamAttendee> streamAttendees) {
    if (nonNull(streamAttendees)) {
      return streamAttendees
        .stream()
        .map(attendee -> StreamAttendeeResponse.of(attendee.getStreamAttendeeId(), attendee.getMember().getMemberId(), attendee.getMember().getFullName()))
        .collect(Collectors.toSet());
    }
    return Set.of();
  }

  /**
   * Validates if the specified user is the creator of the given event.
   *
   * <p>This method checks if the user ID associated with the event creator matches
   * the ID of the user trying to perform an action on the event. If the IDs do not
   * match, a FleenStreamNotCreatedByUserException is thrown.</p>
   *
   * @param stream the FleenStream representing the event to validate
   * @param user the user attempting to perform an action on the event
   * @throws UnableToCompleteOperationException if one of the input is invalid
   * @throws FleenStreamNotCreatedByUserException if the event was not created by the specified user
   */
  public void validateCreatorOfEvent(final FleenStream stream, final FleenUser user) {
    // Throw an exception if the any of the provided values is null
    checkIsNullAny(Set.of(stream, user), UnableToCompleteOperationException::new);

    // Check if the event creator's ID matches the user's ID
    final boolean isSame = Objects.equals(stream.getMember().getMemberId(), user.getId());
    if (!isSame) {
      throw new FleenStreamNotCreatedByUserException(user.getId());
    }
  }

  /**
   * Verifies if the stream end date is in the future.
   *
   * <p>This method checks if the provided stream end date is before the current date and time.
   * If the stream end date is in the past, it throws a FleenStreamNotCreatedByUserException with the end date as a message.</p>
   *
   * @param stream the stream end date and time to verify
   * @throws UnableToCompleteOperationException if one of the input is invalid
   * @throws StreamAlreadyHappenedException if the stream end date is in the past
   */
  public void verifyStreamEndDate(final FleenStream stream) {
    // Throw an exception if the provided value is null
    checkIsNull(stream, UnableToCompleteOperationException::new);

    if (stream.hasEnded()) {
      throw new StreamAlreadyHappenedException(stream.getFleenStreamId(), stream.getScheduledEndDate());
    }
  }

  /**
   * Checks if a user is already an attendee of the given stream and throws an exception if they are.
   *
   * <p>This method is used to ensure that a user cannot request to join a stream if they are already an attendee.
   * It checks the list of attendees in the provided stream for the given user ID. <p>
   *
   * <p>If the user is found as an attendee, an {@link AlreadyRequestedToJoinStreamException} is thrown
   * with the attendee's request to join status.</p>
   *
   * @param stream the {@link FleenStream} to check for existing attendees
   * @param userId the ID of the user to check for
   * @throws UnableToCompleteOperationException if one of the input is invalid
   * @throws AlreadyRequestedToJoinStreamException if the user already requested to join the stream or is already an attendee of the stream
   */
  public void checkIfUserIsAlreadyAnAttendeeAndThrowError(final FleenStream stream, final Long userId) {
    // Throw an exception if the any of the provided values is null
    checkIsNullAny(Set.of(stream, userId), UnableToCompleteOperationException::new);

    // If the user is found as an attendee, throw an exception with the attendee's request to join status
    checkIfUserIsAlreadyAnAttendee(stream, userId)
      .ifPresent(streamAttendee -> {
        // If the user is found as an attendee, throw an exception with the attendee's request to join status
        throw new AlreadyRequestedToJoinStreamException(streamAttendee.getStreamAttendeeRequestToJoinStatus().getValue());
      });
  }

  /**
   * Checks if the given stream is private, and if so, throws a {@link CannotJointStreamWithoutApprovalException}.
   *
   * @param eventId the ID of the event associated with the stream
   * @param stream  the {@link FleenStream} object to check for privacy
   * @throws CannotJointStreamWithoutApprovalException if the stream's visibility is set to PRIVATE
   */
  public void checkIfStreamIsPrivate(final Long eventId, final FleenStream stream) {
    if (stream.isJustPrivate()) {
      throw new CannotJointStreamWithoutApprovalException(eventId);
    }
  }

  /**
   * Checks if the user is already an attendee of the given stream.
   *
   * <p>This method checks if the user is already an attendee of the specified stream by filtering the list of attendees.
   * If the user is found in the list, it throws a FleenStreamNotCreatedByUserException with the attendee's request to join status as a message.</p>
   *
   * @param stream the stream to check for the user's attendance
   * @param userId the user's ID to check
   * @return Optional that may contain the user found as an attendee
   * @throws UnableToCompleteOperationException if one of the input is invalid
   */
  public Optional<StreamAttendee> checkIfUserIsAlreadyAnAttendee(final FleenStream stream, final Long userId) {
    // Throw an exception if the any of the provided values is null
    checkIsNullAny(Set.of(stream, userId), UnableToCompleteOperationException::new);
    // Find if the user is already an attendee of the stream
    return stream.getAttendees()
      .stream()
      .filter(attendee -> userId.equals(attendee.getMember().getMemberId()))
      .findAny();
  }

  /**
   * Creates a new StreamAttendee for the specified stream and user.
   *
   * <p>This method creates a new StreamAttendee object for a given stream and user, setting the request to join status to approved.</p>
   *
   * @param stream the stream to be joined
   * @param user the user requesting to join the stream
   * @return the created StreamAttendee
   * @throws UnableToCompleteOperationException if one of the input is invalid
   */
  public StreamAttendee createStreamAttendee(final FleenStream stream, final FleenUser user) {
    // Throw an exception if the any of the provided values is null
    checkIsNullAny(Set.of(stream, user), UnableToCompleteOperationException::new);

    return StreamAttendee.of(user.toMember(), stream);
  }

  /**
   * Creates a new {@link StreamAttendee} with an additional comment for the given stream and user.
   *
   * <p>This method first creates a {@link StreamAttendee} by calling the {@link #createStreamAttendee(FleenStream, FleenUser)}
   * method. It then sets the provided comment on the newly created {@link StreamAttendee} before returning it.</p>
   *
   * <p>The method is useful for cases where an attendee needs to be created with an additional comment
   * indicating some special information or note regarding their attendance.</p>
   *
   * @param stream the {@link FleenStream} to which the attendee is to be added
   * @param user the {@link FleenUser} who is being added as an attendee
   * @param comment the comment to be added to the {@link StreamAttendee}
   * @return the newly created {@link StreamAttendee} with the added comment
   * @throws UnableToCompleteOperationException if one of the input is invalid
   */
  public StreamAttendee createStreamAttendeeWithComment(final FleenStream stream, final FleenUser user, final String comment) {
    // Throw an exception if the any of the provided values is null
    checkIsNullAny(Set.of(stream, user), UnableToCompleteOperationException::new);

    final StreamAttendee streamAttendee = createStreamAttendee(stream, user);
    streamAttendee.setAttendeeComment(comment);
    return streamAttendee;
  }

  /**
   * Checks if an event is active and not cancelled.
   *
   * <p>This method verifies whether a given FleenStream event is active and not in the cancelled status.
   * It returns true if the event is active, and false otherwise.</p>
   *
   * <p>The method first checks if the provided FleenStream object is not null.
   * If the object is null, it returns false.
   * If the object is not null, it checks the stream status and returns true if the status is not CANCELLED.</p>
   *
   * @param stream the FleenStream event to check
   * @throws UnableToCompleteOperationException if one of the input is invalid
   * @throws StreamAlreadyCancelledException if the event or stream has been cancelled
   */
  public void verifyEventOrStreamIsNotCancelled(final FleenStream stream) {
    // Throw an exception if the provided stream is null
    checkIsNull(stream, UnableToCompleteOperationException::new);

    if (stream.isCanceled()) {
      throw new StreamAlreadyCancelledException(stream.getFleenStreamId());
    }
  }

  /**
   * Verifies the details of a given stream to ensure it is valid for further processing.
   *
   * <p>This method performs several checks on the provided stream: it validates if the user is the creator of the event,
   * checks if the stream's scheduled end date has not passed, and verifies that the event is not cancelled.</p>
   *
   * @param stream the FleenStream to be verified
   * @param user the FleenUser to be validated as the creator of the event
   */
  public void verifyStreamDetails(final FleenStream stream, final FleenUser user) {
    // Validate if the user is the creator of the event
    validateCreatorOfEvent(stream, user);
    // Check if the stream is still active and can be joined.
    verifyStreamEndDate(stream);
    // Verify the event is not cancelled
    verifyEventOrStreamIsNotCancelled(stream);
  }

  /**
   * Retrieves the set of attendees who are marked as attending a stream from the provided set of {@link StreamAttendee} objects.
   * This method filters the input set of attendees to include only those whose {@code isAttending} property is {@code true}.
   * If the input set is null, an empty set is returned.
   *
   * @param streamAttendees A set of {@link StreamAttendee} objects to be filtered.
   *                        Each attendee's attendance status is checked to determine if they are attending the stream.
   * @return A set of {@link StreamAttendee} objects that are attending the stream.
   *         Returns an empty set if the input set is null or if no attendees are marked as attending.
   */
  public Set<StreamAttendee> getAttendeesGoingToStream(final Set<StreamAttendee> streamAttendees) {
    if (nonNull(streamAttendees)) {
      return streamAttendees.stream()
        .filter(StreamAttendee::getIsAttending)
        .collect(Collectors.toSet());
    }
    return new HashSet<>();
  }

  /**
   * Sets the attendee's request to join status as pending if the stream is private.
   * This method checks the stream's visibility and, if the stream is private
   * (either {@link StreamVisibility#PRIVATE} or {@link StreamVisibility#PROTECTED}),
   * updates the attendee's request status to {@link StreamAttendeeRequestToJoinStatus#PENDING}.
   *
   * @param streamAttendee the stream attendee whose request status may be updated
   * @param stream the stream being checked for privacy status
   */
  public void setAttendeeRequestToJoinPendingIfStreamIsPrivate(final StreamAttendee streamAttendee, final FleenStream stream) {
    if (nonNull(streamAttendee)) {
      if (stream.isPrivate()) {
        streamAttendee.setStreamAttendeeRequestToJoinStatus(PENDING);
      }
    }
  }

  /**
   * Retrieves a {@link FleenStream} by its identifier.
   * This method fetches the stream from the repository using the provided event ID.
   * If the stream with the given ID does not exist, it throws a {@link FleenStreamNotFoundException}.
   *
   * @param eventId the ID of the event associated with the stream to be retrieved
   * @return the {@link FleenStream} associated with the given event ID
   * @throws FleenStreamNotFoundException if no stream is found with the specified event ID
   */
  public FleenStream findStream(final Long eventId) {
    return fleenStreamRepository.findById(eventId)
      .orElseThrow(() -> new FleenStreamNotFoundException(eventId));
  }

  /**
   * Allows a user to join a specific event or stream, identified by its unique ID.
   *
   * <p>This method handles the process of a user joining a stream or event by performing a series
   * of validations and updates. It checks the stream's status, ensures it is not canceled or private,
   * verifies that the stream is still active, and confirms that the user is not already an attendee.
   * Upon successful validation, it creates a new {@link StreamAttendee} entry for the user, approves
   * their attendance, and adds the user to the stream's attendee list.</p>
   *
   * <p>The method is annotated with {@code @Transactional} to ensure that the operation is executed
   * within a transactional context, maintaining data consistency and integrity across all operations
   * performed during the user's join process.</p>
   *
   * @param eventOrStreamId the unique identifier of the event or stream that the user intends to join.
   * @param user the {@link FleenUser} attempting to join the event or stream. This parameter includes
   *             the user's identity and any relevant permissions required for the join action.
   * @return the updated {@link FleenStream} object, reflecting the user's successful addition as an attendee.
   */
  @Transactional
  public FleenStream joinEventOrStream(final Long eventOrStreamId, final FleenUser user) {
    final FleenStream stream = findStream(eventOrStreamId);

    // Verify event is not canceled
    verifyEventOrStreamIsNotCancelled(stream);
    // Check if the stream is still active and can be joined.
    verifyStreamEndDate(stream);
    // Check if the stream is private
    checkIfStreamIsPrivate(eventOrStreamId, stream);
    // Check if the user is already an attendee
    checkIfUserIsAlreadyAnAttendeeAndThrowError(stream, user.getId());
    // Create a new StreamAttendee entry for the user
    final StreamAttendee streamAttendee = createStreamAttendee(stream, user);
    streamAttendee.approveUserAttendance();

    // Add the new StreamAttendee to the event's attendees list and save
    stream.getAttendees().add(streamAttendee);
    fleenStreamRepository.save(stream);

    return stream;
  }

}