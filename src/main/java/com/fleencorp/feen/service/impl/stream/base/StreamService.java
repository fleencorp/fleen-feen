package com.fleencorp.feen.service.impl.stream.base;

import com.fleencorp.feen.constant.stream.*;
import com.fleencorp.feen.exception.base.FailedOperationException;
import com.fleencorp.feen.exception.calendar.CalendarNotFoundException;
import com.fleencorp.feen.exception.stream.*;
import com.fleencorp.feen.model.domain.calendar.Calendar;
import com.fleencorp.feen.model.domain.stream.FleenStream;
import com.fleencorp.feen.model.domain.stream.StreamAttendee;
import com.fleencorp.feen.model.domain.user.Member;
import com.fleencorp.feen.model.dto.stream.ProcessAttendeeRequestToJoinEventOrStreamDto;
import com.fleencorp.feen.model.other.Schedule;
import com.fleencorp.feen.model.projection.StreamAttendeeSelect;
import com.fleencorp.feen.model.request.search.stream.StreamAttendeeSearchRequest;
import com.fleencorp.feen.model.request.search.stream.StreamSearchRequest;
import com.fleencorp.feen.model.response.base.FleenFeenResponse;
import com.fleencorp.feen.model.response.stream.EventOrStreamAttendeeResponse;
import com.fleencorp.feen.model.response.stream.EventOrStreamAttendeesResponse;
import com.fleencorp.feen.model.response.stream.PageAndFleenStreamResponse;
import com.fleencorp.feen.model.response.stream.StreamAttendeeResponse;
import com.fleencorp.feen.model.response.stream.base.FleenStreamResponse;
import com.fleencorp.feen.model.search.stream.attendee.EmptyStreamAttendeeSearchResult;
import com.fleencorp.feen.model.search.stream.attendee.StreamAttendeeSearchResult;
import com.fleencorp.feen.model.security.FleenUser;
import com.fleencorp.feen.repository.stream.FleenStreamRepository;
import com.fleencorp.feen.repository.stream.StreamAttendeeRepository;
import com.fleencorp.feen.service.common.CountryService;
import com.fleencorp.feen.service.common.MiscService;
import com.fleencorp.feen.service.i18n.LocalizedResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.fleencorp.base.util.ExceptionUtil.checkIsNull;
import static com.fleencorp.base.util.ExceptionUtil.checkIsNullAny;
import static com.fleencorp.base.util.FleenUtil.handleSearchResult;
import static com.fleencorp.base.util.FleenUtil.toSearchResult;
import static com.fleencorp.feen.constant.stream.JoinStatus.getJoinStatus;
import static com.fleencorp.feen.constant.stream.StreamAttendeeRequestToJoinStatus.APPROVED;
import static com.fleencorp.feen.constant.stream.StreamAttendeeRequestToJoinStatus.PENDING;
import static com.fleencorp.feen.mapper.FleenStreamMapper.toFleenStreamResponse;
import static com.fleencorp.feen.mapper.FleenStreamMapper.toFleenStreamResponses;
import static com.fleencorp.feen.mapper.StreamAttendeeMapper.toEventAttendeeResponses;
import static com.fleencorp.feen.util.DateTimeUtil.convertToTimezone;
import static java.util.Objects.nonNull;

@Slf4j
@Service
public class StreamService {

  private final MiscService miscService;
  private final FleenStreamRepository fleenStreamRepository;
  private final StreamAttendeeRepository streamAttendeeRepository;
  private final LocalizedResponse localizedResponse;
  private static final int DEFAULT_NUMBER_OF_ATTENDEES_TO_GET_FOR_STREAM = 10;
  protected static final int DEFAULT_NUMBER_OF_ATTENDEES_TO_GET_FOR_SEARCH = 100;

  /**
   * Constructs a new instance of the {@link StreamService}.
   *
   * <p>This constructor initializes the service with the required dependencies:
   * {@link MiscService}, {@link FleenStreamRepository}, {@link StreamAttendeeRepository}, and
   * {@link LocalizedResponse}.</p>
   *
   * <p>Each of these dependencies is injected to handle various operations such as miscellaneous tasks,
   * stream repository interactions, attendee management, and localized responses for users.</p>
   *
   * @param miscService the {@link MiscService} used for handling miscellaneous tasks
   * @param fleenStreamRepository the repository for handling operations on {@link FleenStream}
   * @param streamAttendeeRepository the repository for managing event attendees
   * @param localizedResponse the service for creating localized responses
   */
  public StreamService(
      final MiscService miscService,
      final FleenStreamRepository fleenStreamRepository,
      final StreamAttendeeRepository streamAttendeeRepository,
      final LocalizedResponse localizedResponse) {
    this.miscService = miscService;
    this.fleenStreamRepository = fleenStreamRepository;
    this.streamAttendeeRepository = streamAttendeeRepository;
    this.localizedResponse = localizedResponse;
  }

  /**
   * Finds and returns a paginated response of active streams based on the search criteria provided
   * in the {@code searchRequest}.
   *
   * <p>The search is performed based on the parameters in the {@code StreamSearchRequest}.
   * - If both the start date and end date are provided, it searches for streams within the specified date range.
   * - If a title is provided, it searches for streams by title.
   * - If no specific search criteria are provided, all active streams are retrieved.</p>
   *
   * <p>The results are returned as a {@code PageAndFleenStreamResponse}, which includes both the list of
   * streams and pagination details.</p>
   *
   * @param searchRequest the request containing search parameters such as start date, end date,
   *                      title, and pagination information.
   * @return a {@code PageAndFleenStreamResponse} containing the list of matching streams and pagination data.
   */
  protected PageAndFleenStreamResponse findEventsOrStreams(final StreamSearchRequest searchRequest) {
    final Page<FleenStream> page;

    // If both start and end dates are set, search by date range
    if (searchRequest.areAllDatesSet()) {
      page = fleenStreamRepository.findByDateBetween(searchRequest.getStartDateTime(), searchRequest.getEndDateTime(), StreamStatus.ACTIVE, searchRequest.getPage());
    } else if (nonNull(searchRequest.getTitle())) {
      // If title is set, search by stream title
      page = fleenStreamRepository.findByTitle(searchRequest.getTitle(), StreamStatus.ACTIVE, searchRequest.getPage());
    } else {
      // Default to searching for active streams without specific filters
      page = fleenStreamRepository.findMany(StreamStatus.ACTIVE, searchRequest.getPage());
    }

    // Return the response with a list of FleenStreams and pagination info
    return PageAndFleenStreamResponse.of(toFleenStreamResponses(page.getContent()), page);
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
    // Check if the streamAttendees set is not null
    if (nonNull(streamAttendees)) {
      // Convert each StreamAttendee into a StreamAttendeeResponse and collect into a set
      return streamAttendees
        .stream()
        .map(attendee -> StreamAttendeeResponse.of(attendee.getStreamAttendeeId(), attendee.getMemberId(), attendee.getFullName()))
        .collect(Collectors.toSet());
    }
    // Return an empty set if the input set is null
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
   * @throws FailedOperationException if one of the input is invalid
   * @throws FleenStreamNotCreatedByUserException if the event was not created by the specified user
   */
  public static void validateCreatorOfEvent(final FleenStream stream, final FleenUser user) {
    // Throw an exception if the any of the provided values is null
    checkIsNullAny(Set.of(stream, user), FailedOperationException::new);

    // Check if the event creator's ID matches the user's ID
    final boolean isSame = Objects.equals(stream.getMemberId(), user.getId());
    if (!isSame) {
      throw FleenStreamNotCreatedByUserException.of(user.getId());
    }
  }

  /**
   * Verifies if the stream end date is in the future.
   *
   * <p>This method checks if the provided stream end date is before the current date and time.
   * If the stream end date is in the past, it throws a FleenStreamNotCreatedByUserException with the end date as a message.</p>
   *
   * @param stream the stream end date and time to verify
   * @throws FailedOperationException if one of the input is invalid
   * @throws StreamAlreadyHappenedException if the stream end date is in the past
   */
  public void verifyStreamEndDate(final FleenStream stream) {
    // Throw an exception if the provided value is null
    checkIsNull(stream, FailedOperationException::new);

    // Check if the stream has already ended and throw an exception if true
    if (stream.hasEnded()) {
      throw new StreamAlreadyHappenedException(stream.getStreamId(), stream.getScheduledEndDate());
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
   * @throws FailedOperationException if one of the input is invalid
   * @throws AlreadyRequestedToJoinStreamException if the user already requested to join the stream or is already an attendee of the stream
   */
  public void checkIfUserIsAlreadyAnAttendeeAndThrowError(final FleenStream stream, final Long userId) {
    // Throw an exception if the any of the provided values is null
    checkIsNullAny(Set.of(stream, userId), FailedOperationException::new);

    // If the user is found as an attendee, throw an exception with the attendee's request to join status
    checkIfUserIsAlreadyAnAttendee(stream, userId)
      .ifPresent(streamAttendee -> {
        // If the user is found as an attendee, throw an exception with the attendee's request to join status
        throw AlreadyRequestedToJoinStreamException.of(streamAttendee.getRequestToJoinStatus().getValue());
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
      throw CannotJointStreamWithoutApprovalException.of(eventId);
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
   * @throws FailedOperationException if one of the input is invalid
   */
  public Optional<StreamAttendee> checkIfUserIsAlreadyAnAttendee(final FleenStream stream, final Long userId) {
    // Throw an exception if the any of the provided values is null
    checkIsNullAny(Set.of(stream, userId), FailedOperationException::new);
    // Find if the user is already an attendee of the stream
    return streamAttendeeRepository.findAttendeeByStreamAndUser(stream, Member.of(userId));
  }

  /**
   * Creates a new StreamAttendee for the specified stream and user.
   *
   * <p>This method creates a new StreamAttendee object for a given stream and user, setting the request to join status to approved.</p>
   *
   * @param stream the stream to be joined
   * @param user the user requesting to join the stream
   * @return the created StreamAttendee
   * @throws FailedOperationException if one of the input is invalid
   */
  public StreamAttendee createStreamAttendee(final FleenStream stream, final FleenUser user) {
    // Throw an exception if the any of the provided values is null
    checkIsNullAny(Set.of(stream, user), FailedOperationException::new);

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
   * @throws FailedOperationException if one of the input is invalid
   */
  public StreamAttendee createStreamAttendeeWithComment(final FleenStream stream, final FleenUser user, final String comment) {
    // Throw an exception if the any of the provided values is null
    checkIsNullAny(Set.of(stream, user), FailedOperationException::new);

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
   * @throws FailedOperationException if one of the input is invalid
   * @throws StreamAlreadyCanceledException if the event or stream has been cancelled
   */
  public void verifyEventOrStreamIsNotCancelled(final FleenStream stream) {
    // Throw an exception if the provided stream is null
    checkIsNull(stream, FailedOperationException::new);

    if (stream.isCanceled()) {
      throw StreamAlreadyCanceledException.of(stream.getStreamId());
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
   * @param stream The stream to use to search for attendees.
   *               Each attendee's attendance status is checked to determine if they are attending the stream.
   * @return A set of {@link StreamAttendee} objects that are attending the stream.
   *         Returns an empty set if the input set is null or if no attendees are marked as attending.
   */
  public Set<StreamAttendee> getAttendeesGoingToStream(final FleenStream stream) {
    if (nonNull(stream)) {
      return streamAttendeeRepository.findAttendeesGoingToStream(stream);
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
      // If the stream is private, set the request-to-join status to pending
      if (stream.isPrivate()) {
        streamAttendee.setRequestToJoinStatus(PENDING);
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
   * <p>This method handles the process of a user joining a event or stream by performing a series
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
  public FleenStream verifyDetailsAndTryToJoinEventOrStream(final Long eventOrStreamId, final FleenUser user) {
    final FleenStream stream = findStream(eventOrStreamId);

    // Verify if the user is the owner and fail the operation because the owner is automatically a member of the chat space
    verifyIfUserIsAuthorOrCreatorOrOwnerTryingToPerformAction(Member.of(stream.getMemberId()), user);
    // Verify event is not canceled
    verifyEventOrStreamIsNotCancelled(stream);
    // Check if the stream is still active and can be joined.
    verifyStreamEndDate(stream);
    // Check if the stream is private
    checkIfStreamIsPrivate(eventOrStreamId, stream);
    // Check if the user is already an attendee
    checkIfUserIsAlreadyAnAttendeeAndThrowError(stream, user.getId());
    // Save the fleen stream to the repository
    fleenStreamRepository.save(stream);

    return stream;
  }

  /**
   * Converts a list of {@link StreamAttendee} entities into a list of {@link StreamAttendeeResponse} objects.
   *
   * <p>This method first converts the list of {@code StreamAttendee} entities into a set to eliminate any duplicate
   * attendees. It then uses the {@code toStreamAttendeeResponses} method to perform the actual conversion,
   * and finally returns the result as a list.</p>
   *
   * @param streamAttendees the list of {@code StreamAttendee} entities to convert.
   * @return a list of {@code StreamAttendeeResponse} objects, with duplicates removed.
   */
  protected List<StreamAttendeeResponse> toStreamAttendeeResponses(final List<StreamAttendee> streamAttendees) {
    // Fetch a paginated list of stream attendees for the given stream ID
    final Set<StreamAttendee> streamAttendeesSet = new HashSet<>(streamAttendees);
    // Convert the list of StreamAttendee entities to StreamAttendeeResponse views
    final Set<StreamAttendeeResponse> streamAttendeeResponses = toStreamAttendeeResponses(streamAttendeesSet);
    // Convert to a search result view, including the attendees and pagination details
    return new ArrayList<>(streamAttendeeResponses);
  }

  /**
   * Finds attendees for a given event or stream based on the specified search request.
   *
   * @param eventOrStreamId The ID of the event or stream whose attendees are to be found.
   * @param searchRequest The request containing search parameters such as pagination information.
   * @return A {@code StreamAttendeeSearchResult} containing the list of stream attendees and pagination details.
   */
  protected StreamAttendeeSearchResult findEventOrStreamAttendees(final Long eventOrStreamId, final StreamAttendeeSearchRequest searchRequest) {
    // Retrieve paginated list of attendees associated with the given event or stream
    final Page<StreamAttendee> page = streamAttendeeRepository.findByFleenStream(FleenStream.of(eventOrStreamId), searchRequest.getPage());
    // Convert the list of attendees to response objects
    final List<StreamAttendeeResponse> views = toStreamAttendeeResponses(page.getContent());
    // Return a search result view with the attendees responses and pagination details
    return handleSearchResult(
      page,
      localizedResponse.of(StreamAttendeeSearchResult.of(toSearchResult(views, page))),
      localizedResponse.of(EmptyStreamAttendeeSearchResult.of(toSearchResult(List.of(), page)))
    );
  }

  /**
   * Retrieves the list of attendees for a given event identified by eventOrStreamId and converts them to an EventAttendeesResponse DTO.
   *
   * <p>If the event with the specified eventOrStreamId is not found in the database, a FleenStreamNotFoundException is thrown.</p>
   *
   * <p>The method fetches the event details from the database using the eventOrStreamId and then delegates to getAttendees method
   * to convert the attendees into a structured response.</p>
   *
   * @param eventOrStreamId the unique identifier of the event or stream
   * @param searchRequest the search request use to filter the attendees result
   * @param streamSource the source for which fleen streams will be filtered
   * @return an EventAttendeesResponse DTO containing the list of attendees for the event
   * @throws FleenStreamNotFoundException if the event with the specified eventOrStreamId is not found
   */
  public EventOrStreamAttendeesResponse getEventOrStreamAttendees(final Long eventOrStreamId, final StreamAttendeeSearchRequest searchRequest, final StreamSource streamSource) {
    // Convert the attendees to response objects
    final FleenStream stream = findStream(eventOrStreamId);
    // Perform a search and retrieve the page and search result of attendees
    final Page<StreamAttendee> page = streamAttendeeRepository.findByFleenStreamAndStreamSource(stream, streamSource, searchRequest.getPage());
    // Get event attendees
    final EventOrStreamAttendeesResponse eventOrStreamAttendeesResponse = getAttendees(eventOrStreamId, page.getContent());
    eventOrStreamAttendeesResponse.setEventId(eventOrStreamId);
    eventOrStreamAttendeesResponse.setPage(page);

    return localizedResponse.of(eventOrStreamAttendeesResponse);
  }

  /**
   * Retrieves the list of attendees for a given event and converts them to an EventAttendeesResponse DTO.
   *
   * <p>If the provided set of attendees is not null and not empty, it converts each StreamAttendee entity to
   * an EventAttendeeResponse DTO and sets them in the response object.</p>
   *
   * <p>If no attendees are found (either the set is null or empty), it returns an empty EventAttendeesResponse object.</p>
   *
   * @param eventOrStreamId the unique identifier of the event or stream
   * @param attendees the set of StreamAttendee entities representing the attendees of the event
   * @return an EventAttendeesResponse DTO containing the list of attendees, or an empty EventAttendeesResponse if there are no attendees
   */
  public EventOrStreamAttendeesResponse getAttendees(final Long eventOrStreamId, final Collection<StreamAttendee> attendees) {
    final EventOrStreamAttendeesResponse eventOrStreamAttendeesResponse = EventOrStreamAttendeesResponse.of(eventOrStreamId);
    // Check if the attendees list is not empty and set it to the list of attendees in the response
    if (nonNull(attendees) && !attendees.isEmpty()) {
      final List<EventOrStreamAttendeeResponse> attendeesResponses = toEventAttendeeResponses(new ArrayList<>(attendees));
      eventOrStreamAttendeesResponse.setAttendees(attendeesResponses);
    }
    return eventOrStreamAttendeesResponse;
  }

  /**
   * Determines the status of a user’s request to join various events or streams and updates the response views accordingly.
   *
   * <p>This method retrieves the user’s attendance information for a list of event or stream IDs,
   * maps their join request status to each corresponding event, and updates the provided list of
   * response views with the appropriate join status.</p>
   *
   * <p>The method follows these steps:</p>
   * <ul>
   *   <li>Retrieves the attendance records for the specified user and events.</li>
   *   <li>Maps the event or stream IDs to the user's request-to-join status.</li>
   *   <li>Iterates over the response views and updates the join status for each event or stream.</li>
   * </ul>
   *
   * @param responses List of response views (FleenStreamResponse) to be updated with the user’s join status.
   * @param user The user whose request-to-join status is being determined.
   */
  protected void determineUserJoinStatusForEventOrStream(final List<FleenStreamResponse> responses, final FleenUser user) {
    if (isUserAndResponsesValid(responses, user)) {
      // Extract the event or stream IDs from the search result views
      final List<Long> eventIds = extractAndGetEventOrStreamIds(responses);
      // Retrieve the user's attendance records for the provided event or stream IDs
      final List<StreamAttendeeSelect> userAttendances = streamAttendeeRepository.findByMemberAndEventOrStreamIds(user.toMember(), eventIds);
      // Map event or stream IDs to the user's request-to-join status
      final Map<Long, StreamAttendeeRequestToJoinStatus> attendanceStatusMap = groupAttendeeStatusByEventOrStreamId(userAttendances);

      // Update each stream's join status in the response views
      updateJoinStatusInResponses(responses, attendanceStatusMap);
    }
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
  protected void updateJoinStatusInResponses(final List<FleenStreamResponse> responses, final Map<Long, StreamAttendeeRequestToJoinStatus> attendanceStatusMap) {
    if (nonNull(responses) && nonNull(attendanceStatusMap)) {
      // Update each stream's join status in the response views
      responses.stream()
        .filter(Objects::nonNull)
        .forEach(stream -> {
          // Retrieve the attendee status for a specific ID which can be null because the member has not join or requested to join the event or stream
          final Optional<StreamAttendeeRequestToJoinStatus> existingStatus = Optional.ofNullable(attendanceStatusMap.get(stream.getNumberId()));
          // If member is an attendee, retrieve the status and set view label
          existingStatus.ifPresent(status -> {
            // Retrieve the join status based on the stream request to join status
            final JoinStatus joinStatus = getJoinStatus(status);
            // Get the status label based on the user's join status
            final String statusLabel = localizedResponse.of(joinStatus.getMessageCode());
            // Get the user's request to join status based on the stream
            stream.setRequestToJoinStatus(status);
            // Set the join status for the chatSpace
            stream.setJoinStatus(statusLabel);
            // Disable and reset the unmasked link if the user's join status is not approved
            stream.disableAndResetUnmaskedLinkIfNotApproved();
          });
      });
    }
  }

  /**
   * Extracts and returns a list of event or stream IDs from a list of FleenStreamResponse objects.
   * The method filters out any null responses and retrieves the numeric ID of each non-null response.
   *
   * @param responses A list of FleenStreamResponse objects from which to extract event or stream IDs.
   * @return A list of event or stream IDs. If the input list is null, an empty list is returned.
   */
  protected static List<Long> extractAndGetEventOrStreamIds(final List<FleenStreamResponse> responses) {
    // Filter null responses and map to the corresponding event or stream ID
    if (nonNull(responses)) {
      return responses.stream()
        .filter(Objects::nonNull)
        .map(FleenFeenResponse::getNumberId)
        .toList();
    }
    // Return an empty list if the input list is null
    return List.of();
  }

  /**
   * Groups attendee request-to-join statuses by the event or stream ID.
   * This method processes a list of user attendances, filters out any null values,
   * and maps the event or stream ID to the corresponding request-to-join status.
   *
   * @param userAttendances A list of user attendances with event or stream data.
   * @return A map where the key is the event or stream ID and the value is the request-to-join status.
   *         If the input list is null or empty, an empty map is returned.
   */
  protected static Map<Long, StreamAttendeeRequestToJoinStatus> groupAttendeeStatusByEventOrStreamId(final List<StreamAttendeeSelect> userAttendances) {
    // Filter null values and map event/stream ID to request-to-join status
    if (nonNull(userAttendances) && !userAttendances.isEmpty()) {
      return userAttendances.stream()
        .filter(Objects::nonNull)
        .collect(Collectors.toMap(StreamAttendeeSelect::getEventOrStreamId, StreamAttendeeSelect::getRequestToJoinStatus));
    }
    // Return an empty map if the input list is null or empty
    return Map.of();
  }

  /**
   * Determines the schedule status for each FleenStreamResponse in the provided list.
   * If the list is not null, the method calls updateStreamSchedule on each response
   * to update its schedule status.
   *
   * @param responses the list of FleenStreamResponse objects to update
   */
  protected void determineScheduleStatus(final List<FleenStreamResponse> responses) {
    if (nonNull(responses)) {
      responses.stream()
        .filter(Objects::nonNull)
        .forEach(FleenStreamResponse::updateStreamSchedule);
    }
  }

  /**
   * Adjusts the schedule of a collection of stream responses to the user's timezone.
   *
   * <p>For each stream in the collection, this method compares the stream's timezone with the user's timezone.
   * If the timezones differ, the stream's schedule is converted to the user's timezone. Otherwise, an empty
   * schedule is set.</p>
   *
   * @param responses The collection of {@link FleenStreamResponse} objects containing the stream schedules.
   * @param user      The {@link FleenUser} whose timezone is used for comparison and conversion.
   */
  protected void setOtherScheduleBasedOnUserTimezone(final Collection<FleenStreamResponse> responses, final FleenUser user) {
    if (isUserAndResponsesValid(responses, user)) {
      responses.stream()
        .filter(Objects::nonNull)
        .forEach(stream -> {
          // Get the stream's original timezone
          final String streamTimezone = stream.getSchedule().getTimezone();
          // Get the user's timezone
          final String userTimezone = user.getTimezone();

          // Check if the event's timezone and user's timezone are different
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
   * Creates a schedule for the given stream in the user's timezone.
   *
   * <p>This method takes a {@link FleenStreamResponse} object and the user's timezone,
   * then converts the stream's start and end dates from the stream's timezone to
   * the user's timezone. The converted schedule is returned in the form of a
   * {@link Schedule}.</p>
   *
   * @param stream       The {@link FleenStreamResponse} object containing the stream schedule
   *                     with the original timezone and dates.
   * @param userTimezone The timezone of the user to which the schedule should be converted.
   *                     This must be a valid timezone string (e.g., "America/New_York").
   * @return A {@link Schedule} object containing the start and end dates
   *         converted to the user's timezone. If the stream or user's timezone is null,
   *         an empty schedule is returned.
   */
  protected Schedule createSchedule(final FleenStreamResponse stream, final String userTimezone) {
    if (nonNull(stream) && nonNull(userTimezone)) {
      // Get the stream's original timezone
      final String streamTimezone = stream.getSchedule().getTimezone();

      // Retrieve the start and end dates from the stream's schedule
      final LocalDateTime startDate = stream.getSchedule().getStartDate();
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
   * Updates the request status of a StreamAttendee based on the provided DTO.
   *
   * <p>This method retrieves the desired join status from the given DTO and updates
   * the corresponding StreamAttendee's request status. It also sets any comments
   * provided by the organizer.</p>
   *
   * @param streamAttendee The StreamAttendee whose request status is to be updated.
   * @param processAttendeeRequestToJoinDto The DTO containing the new join status and comments.
   */
  protected void updateAttendeeRequestStatus(final StreamAttendee streamAttendee, final ProcessAttendeeRequestToJoinEventOrStreamDto processAttendeeRequestToJoinDto) {
    // Retrieve the requested status for the attendee to join the stream
    final StreamAttendeeRequestToJoinStatus requestToJoinStatus = processAttendeeRequestToJoinDto.getActualJoinStatus();
    // Update the attendee's request status and set any organizer comments
    streamAttendee.updateRequestStatusAndSetOrganizerComment(requestToJoinStatus, processAttendeeRequestToJoinDto.getComment());
  }

  /**
   * Increases the total number of attendees or guests for a given event stream and saves the updated stream.
   *
   * @param stream The event stream where the number of attendees or guests is to be increased.
   */
  protected FleenStream increaseTotalAttendeesOrGuestsAndSave(final FleenStream stream) {
    // Increase total attendees or guests in the event
    stream.increaseTotalAttendees();
    // Save the updated stream to the repository
    return fleenStreamRepository.save(stream);
  }

  /**
   * Decreases the total number of attendees or guests for a given event stream and saves the updated stream.
   *
   * @param stream The event stream where the number of attendees or guests is to be decreased.
   */
  protected void decreaseTotalAttendeesOrGuestsAndSave(final FleenStream stream) {
    // Decrease total attendees or guests in the event
    stream.decreaseTotalAttendees();
    // Save the updated stream to the repository
    fleenStreamRepository.save(stream);
  }

  /**
   * Determines and sets various statuses and details for events or streams based on the user's context.
   *
   * <p>This method evaluates and updates the provided list of {@link FleenStreamResponse} objects
   * by determining the user's join status, the schedule status (live, past, or upcoming), and
   * adjusts schedule details based on the user's timezone.</p>
   *
   * @param views the list of event or stream responses to update with status and details.
   * @param user the user whose context will be used to adjust the statuses and schedule details.
   */
  public void determineDifferentStatusesAndDetailsOfEventOrStreamBasedOnUser(final List<FleenStreamResponse> views, final FleenUser user) {
    // Determine the user's join status for each event or stream
    determineUserJoinStatusForEventOrStream(views, user);
    // Determine schedule status whether live, past or upcoming
    determineScheduleStatus(views);
    // Set other schedule details if user timezone is different
    setOtherScheduleBasedOnUserTimezone(views, user);
  }

  /**
   * Sets the number of attendees for each stream in the provided list of {@link FleenStreamResponse} objects.
   *
   * <p>This method iterates over a list of {@link FleenStreamResponse} instances, calculates the total number of attendees
   * who have been approved to join and are attending each event, and updates the total attending count for each stream.
   * The count is based on the data retrieved from the `streamAttendeeRepository.</p>
   *
   * @param streams the list of {@link FleenStreamResponse} objects whose attendee counts are to be updated.
   */
  public void setStreamAttendeesAndTotalAttendeesAttending(final List<FleenStreamResponse> streams) {
    if (nonNull(streams)) {
      streams.stream()
        .filter(Objects::nonNull)
        .forEach(stream -> {
          final Long streamId = Long.parseLong(stream.getId().toString());
          // Count total attendees whose request to join event is approved and are attending the event because they are interested
          final long totalAttendees = streamAttendeeRepository.
            countByFleenStreamAndRequestToJoinStatusAndIsAttending(FleenStream.of(streamId), APPROVED, true);
          stream.setTotalAttending(totalAttendees);
      });
    }
  }

  /**
   * Retrieves and sets the first 10 attendees (or fewer if not enough attendees) for each stream in the provided list of {@link FleenStreamResponse} objects.
   *
   * <p>This method iterates over a list of {@link FleenStreamResponse} instances, retrieves up to 10 attendees who have been approved
   * to join and are attending each event, and updates the list of attendees for each stream. The retrieval is done in an arbitrary order.</p>
   *
   * @param streams the list of {@link FleenStreamResponse} objects whose attendee lists are to be updated.
   */
  public void getFirst10AttendingInAnyOrder(final List<FleenStreamResponse> streams) {
    if (nonNull(streams)) {
      streams.stream()
        .filter(Objects::nonNull)
        .forEach(stream -> {
          final Long streamId = Long.parseLong(stream.getId().toString());
          // Create a pageable request to get the first 10 attendees
          final Pageable pageable = PageRequest.of(1, DEFAULT_NUMBER_OF_ATTENDEES_TO_GET_FOR_STREAM);
          // Fetch attendees who are approved and attending the event
          final Page<StreamAttendee> page = streamAttendeeRepository
            .findAllByFleenStreamAndRequestToJoinStatusAndIsAttending(FleenStream.of(streamId), APPROVED, true, pageable);
          // Convert the list of stream attendees to list of stream attendee responses
          final List<StreamAttendeeResponse> streamAttendees = toStreamAttendeeResponses(page.getContent());
          stream.setSomeAttendees(streamAttendees);
      });
    }
  }

  /**
   * Registers the organizer of an event as an attendee and automatically approves their attendance.
   *
   * @param stream The event the organizer is managing.
   * @param user   The organizer's user account.
   */
  @Transactional
  public void registerAndApproveOrganizerOfEventAsAnAttendee(final FleenStream stream, final FleenUser user) {
    // Add the organizer as an attendee of the event
    final StreamAttendee streamAttendee = StreamAttendee.of(user.toMember(), stream);
    // Approve the organizer's request to join automatically
    streamAttendee.approveUserAttendance();
    // Save attendee to the repository
    streamAttendeeRepository.save(streamAttendee);
  }

  /**
   * Increments the attendee count for the provided stream and returns the corresponding
   * {@link FleenStreamResponse}. If the response is non-null, it ensures the attendee
   * count is incremented exactly once.
   *
   * @param stream the {@link FleenStream} instance for which the attendee count will be incremented.
   * @return the updated {@link FleenStreamResponse} with the attendee count incremented.
   */
  public FleenStreamResponse incrementAttendeeBecauseOfOrganizerAndGetResponse(final FleenStream stream) {
    final FleenStreamResponse response = toFleenStreamResponse(stream);
    if (nonNull(response)) {
      response.incrementAttendeesOrGuestsAttendingJustOnce();
    }
    return response;
  }

  /**
   * Verifies if the user is the author, creator, or owner attempting to perform an action.
   *
   * <p>This method delegates the verification to the {@link MiscService} to check if the user
   * is the owner, creator, or author of a given resource, such as a chat space or event.
   * If the user matches the role of the owner, author, or creator, the action will be restricted,
   * potentially resulting in an exception being thrown.</p>
   *
   * <p>Use this method to ensure that unauthorized actions, like modifying or deleting resources
   * by the resource's owner, are properly controlled.</p>
   *
   * @param owner the {@link Member} representing the owner, author, or creator of the resource
   * @param user the {@link FleenUser} attempting to perform an action
   *
   * @throws FailedOperationException if the user is the author, creator, or owner of the resource
   */
  protected void verifyIfUserIsAuthorOrCreatorOrOwnerTryingToPerformAction(final Member owner, final FleenUser user) {
    miscService.verifyIfUserIsAuthorOrCreatorOrOwnerTryingToPerformAction(owner, user);
  }

  /**
   * Finds a {@link com.fleencorp.feen.model.domain.calendar.Calendar} based on the provided country title.
   * This method retrieves the country code for the given title using the {@link CountryService}, and then
   * searches for a {@link com.fleencorp.feen.model.domain.calendar.Calendar} in the repository using the country code.
   * If the country title or the calendar is not found, an exception is thrown.
   *
   * @param countryTitle The title of the country for which the calendar is to be found.
   * @return The {@link com.fleencorp.feen.model.domain.calendar.Calendar} associated with the given country title.
   * @throws CalendarNotFoundException If no calendar is found for the provided country title or code.
   */
  protected Calendar findCalendar(final String countryTitle) {
    return miscService.findCalendar(countryTitle);
  }

}
