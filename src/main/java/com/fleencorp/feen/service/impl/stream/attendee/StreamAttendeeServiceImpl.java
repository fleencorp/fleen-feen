package com.fleencorp.feen.service.impl.stream.attendee;

import com.fleencorp.feen.exception.base.FailedOperationException;
import com.fleencorp.feen.exception.stream.FleenStreamNotFoundException;
import com.fleencorp.feen.exception.stream.StreamNotCreatedByUserException;
import com.fleencorp.feen.mapper.stream.StreamMapper;
import com.fleencorp.feen.mapper.stream.attendee.StreamAttendeeMapper;
import com.fleencorp.feen.model.domain.calendar.Calendar;
import com.fleencorp.feen.model.domain.stream.FleenStream;
import com.fleencorp.feen.model.domain.stream.StreamAttendee;
import com.fleencorp.feen.model.domain.user.Member;
import com.fleencorp.feen.model.info.JoinStatusInfo;
import com.fleencorp.feen.model.info.stream.attendance.AttendanceInfo;
import com.fleencorp.feen.model.info.stream.attendee.IsAttendingInfo;
import com.fleencorp.feen.model.info.stream.attendee.StreamAttendeeRequestToJoinStatusInfo;
import com.fleencorp.feen.model.projection.StreamAttendeeSelect;
import com.fleencorp.feen.model.request.search.stream.StreamAttendeeSearchRequest;
import com.fleencorp.feen.model.response.stream.FleenStreamResponse;
import com.fleencorp.feen.model.response.stream.attendee.StreamAttendeeResponse;
import com.fleencorp.feen.model.search.join.EmptyRequestToJoinSearchResult;
import com.fleencorp.feen.model.search.join.RequestToJoinSearchResult;
import com.fleencorp.feen.model.search.stream.attendee.EmptyStreamAttendeeSearchResult;
import com.fleencorp.feen.model.search.stream.attendee.StreamAttendeeSearchResult;
import com.fleencorp.feen.model.security.FleenUser;
import com.fleencorp.feen.repository.stream.StreamAttendeeRepository;
import com.fleencorp.feen.service.common.MiscService;
import com.fleencorp.feen.service.stream.attendee.StreamAttendeeService;
import com.fleencorp.feen.service.stream.common.StreamService;
import com.fleencorp.feen.service.stream.update.AttendeeUpdateService;
import com.fleencorp.localizer.service.Localizer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.fleencorp.base.util.ExceptionUtil.checkIsNullAny;
import static com.fleencorp.base.util.FleenUtil.handleSearchResult;
import static com.fleencorp.base.util.FleenUtil.toSearchResult;
import static com.fleencorp.feen.constant.stream.StreamAttendeeRequestToJoinStatus.APPROVED;
import static com.fleencorp.feen.constant.stream.StreamAttendeeRequestToJoinStatus.PENDING;
import static com.fleencorp.feen.service.impl.stream.base.StreamServiceImpl.validateCreatorOfStream;
import static java.util.Objects.nonNull;

/**
 * Implementation of the {@link StreamAttendeeService} interface.
 *
 * <p>This class provides services for managing stream attendees, including searching for attendees,
 * managing their join requests, and performing operations related to stream attendees within the system.</p>
 *
 * <p>The service interacts with various components, such as {@link StreamService}, {@link StreamAttendeeRepository},
 * and other supporting services to provide the required functionalities for managing attendees for streams.</p>
 *
 * @author Yusuf Àlàmù Musa
 * @version 1.0
 */
@Service
public class StreamAttendeeServiceImpl implements StreamAttendeeService {

  private final MiscService miscService;
  private final StreamService streamService;
  private final AttendeeUpdateService attendeeUpdateService;
  private final StreamAttendeeRepository streamAttendeeRepository;
  private final Localizer localizer;
  private final StreamAttendeeMapper attendeeMapper;
  private final StreamMapper streamMapper;
  private static final int DEFAULT_NUMBER_OF_ATTENDEES_TO_GET_FOR_STREAM = 10;

  /**
   * Constructs a new instance of {@code StreamAttendeeServiceImpl} with the specified services and dependencies.
   *
   * @param miscService               the service for miscellaneous operations
   * @param streamService             the service for managing streams
   * @param attendeeUpdateService     the service for handling attendee updates
   * @param streamAttendeeRepository  the repository for managing stream attendee data
   * @param localizer                 the service for localization tasks
   * @param attendeeMapper            the mapper for mapping attendee data
   * @param streamMapper              the mapper for mapping stream data
   */
  public StreamAttendeeServiceImpl(
      final MiscService miscService,
      final StreamService streamService,
      final AttendeeUpdateService attendeeUpdateService,
      final StreamAttendeeRepository streamAttendeeRepository,
      final Localizer localizer,
      final StreamAttendeeMapper attendeeMapper,
      final StreamMapper streamMapper) {
    this.miscService = miscService;
    this.streamService = streamService;
    this.attendeeUpdateService = attendeeUpdateService;
    this.streamAttendeeRepository = streamAttendeeRepository;
    this.localizer = localizer;
    this.attendeeMapper = attendeeMapper;
    this.streamMapper = streamMapper;
  }

  /**
   * Retrieves a paginated list of attendees for a specific stream based on the search request parameters.
   *
   * <p>This method first sets the default page size for the search request, retrieves the paginated list of attendees
   * from the {@link StreamAttendeeRepository}, and converts the list of attendees into response objects. The method then
   * returns a localized search result, which includes the list of attendees and pagination details.</p>
   *
   * <p>If no attendees are found, it returns an empty result.</p>
   *
   * @param streamId The ID of the stream for which attendees are being searched.
   * @param searchRequest The request object containing search criteria and pagination details.
   * @return A {@link StreamAttendeeSearchResult} containing the list of attendees and pagination information.
   */
  @Override
  public StreamAttendeeSearchResult findStreamAttendees(final Long streamId, final StreamAttendeeSearchRequest searchRequest) {
    // Set default number of attendees to retrieve during the search
    searchRequest.setDefaultPageSize();
    // Retrieve paginated list of attendees associated with the given event or stream
    final Page<StreamAttendee> page = streamAttendeeRepository.findByStream(FleenStream.of(streamId), searchRequest.getPage());
    // Retrieve the fleen stream
    final FleenStream stream = streamService.findStream(streamId);
    // Convert the list of attendees to response objects
    final List<StreamAttendeeResponse> views = toStreamAttendeeResponses(streamMapper.toFleenStreamResponse(stream), page.getContent());
    // Return a search result view with the attendees responses and pagination details
    return handleSearchResult(
      page,
      localizer.of(StreamAttendeeSearchResult.of(toSearchResult(views, page))),
      localizer.of(EmptyStreamAttendeeSearchResult.of(toSearchResult(List.of(), page)))
    );
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
  protected List<StreamAttendeeResponse> toStreamAttendeeResponses(final FleenStreamResponse streamResponse, final List<StreamAttendee> streamAttendees) {
    // Fetch a paginated list of stream attendees for the given stream ID
    final Set<StreamAttendee> streamAttendeesSet = new HashSet<>(streamAttendees);
    // Convert the list of StreamAttendee entities to StreamAttendeeResponse views
    final Set<StreamAttendeeResponse> streamAttendeeResponses = toStreamAttendeeResponsesSet(streamResponse, streamAttendeesSet);
    // Convert to a search result view, including the attendees and pagination details
    return new ArrayList<>(streamAttendeeResponses);
  }

  /**
   * Converts a collection of stream attendees to a set of {@link StreamAttendeeResponse} objects, including detailed
   * attendance information for each attendee.
   *
   * <p>This method checks if the provided collection of stream attendees is not null. It then maps each attendee to a
   * response object that includes details such as the attendee's request to join status, join status, and attending status.
   * The resulting response objects are collected into a {@link Set} and returned. If the input collection is null, an empty
   * set is returned.</p>
   *
   * @param streamResponse The response object representing the stream, used to determine join status and attendance information.
   * @param streamAttendees The collection of {@link StreamAttendee} objects to be converted into response objects.
   * @return A {@link Set} of {@link StreamAttendeeResponse} objects, each containing attendee details and attendance status.
   */
  @Override
  public Set<StreamAttendeeResponse> toStreamAttendeeResponsesSet(final FleenStreamResponse streamResponse, final Collection<StreamAttendee> streamAttendees) {
    // Check if the streamAttendees set is not null
    if (nonNull(streamAttendees)) {
      // Convert each StreamAttendee into a StreamAttendeeResponse and collect into a set
      return streamAttendees.stream()
        // Filter for non empty attendee
        .filter(Objects::nonNull)
        // Map each attendee to a response object
        .map(attendee -> {
          // Convert the attendee's request to join status to a response-friendly format
          final StreamAttendeeRequestToJoinStatusInfo requestToJoinStatusInfo = streamMapper.toRequestToJoinStatus(attendee.getRequestToJoinStatus());
          // Determine the join status info based on the stream and attendee details
          final JoinStatusInfo joinStatusInfo = streamMapper.toJoinStatus(streamResponse, attendee.getRequestToJoinStatus(), attendee.isAttending());
          // Determine the is attending information based on the user's status attendee status
          final IsAttendingInfo attendingInfo = streamMapper.toIsAttendingInfo(attendee.isAttending());
          // Create a stream attendee response with basic info
          final StreamAttendeeResponse attendeeResponse = StreamAttendeeResponse.of(attendee.getStreamAttendeeId(), attendee.getMemberId(), attendee.getFullName());
          // Add the attendance info on the attendee response
          attendeeResponse.setAttendanceInfo(AttendanceInfo.of(requestToJoinStatusInfo, joinStatusInfo, attendingInfo));
          // Return a new StreamAttendeeResponse object with the attendee's details and status info
          return attendeeResponse;
        })
        // Collect all mapped responses into a set
        .collect(Collectors.toSet());
    }
    // Return an empty set if the input set is null
    return Set.of();
  }

  /**
   * Sets the first 10 attendees who are approved and attending a stream for each stream in the provided list of {@link FleenStreamResponse}.
   *
   * <p>This method iterates over the given list of streams, fetching the first 10 approved attendees who are attending the stream.
   * It then converts the list of {@link StreamAttendee} objects into a set of {@link StreamAttendeeResponse} objects, which are
   * set as the attendees for each stream. If the list of streams is null, no action is taken.</p>
   *
   * @param streams The list of {@link FleenStreamResponse} objects representing the streams, each stream's attendees will be updated with the first 10 attending members.
   */
  @Override
  public void setFirst10AttendeesAttendingInAnyOrderOnStreams(final List<FleenStreamResponse> streams) {
    if (nonNull(streams)) {
      streams.stream()
        .filter(Objects::nonNull)
        .forEach(stream -> {
          final Long streamId = Long.parseLong(stream.getId().toString());
          // Create a pageable request to get the first 10 attendees
          final Pageable pageable = PageRequest.of(1, DEFAULT_NUMBER_OF_ATTENDEES_TO_GET_FOR_STREAM);
          // Fetch attendees who are approved and attending the stream
          final Page<StreamAttendee> page = streamAttendeeRepository
            .findAllByStreamAndRequestToJoinStatusAndAttending(FleenStream.of(streamId), APPROVED, true, pageable);
          // Convert the list of stream attendees to list of stream attendee responses
          final List<StreamAttendeeResponse> streamAttendees = toStreamAttendeeResponses(stream, page.getContent());
          // Set the attendees on the response
          stream.setSomeAttendees(new HashSet<>(streamAttendees));
      });
    }
  }

  /**
   * Sets the number of attendees for each stream in the provided list of {@link FleenStreamResponse} objects.
   *
   * <p>This method iterates over a list of {@link FleenStreamResponse} instances, calculates the total number of attendees
   * who have been approved to join and are attending each stream, and updates the total attending count for each stream.
   * The count is based on the data retrieved from the `streamAttendeeRepository.</p>
   *
   * @param streams the list of {@link FleenStreamResponse} objects whose attendee counts are to be updated.
   */
  @Override
  public void setStreamAttendeesAndTotalAttendeesAttending(final List<FleenStreamResponse> streams) {
    if (nonNull(streams)) {
      streams.stream()
        .filter(Objects::nonNull)
        .forEach(stream -> {
          final Long streamId = Long.parseLong(stream.getId().toString());
          // Count total attendees whose request to join stream is approved and are attending the stream because they are interested
          final long totalAttendees = streamAttendeeRepository.
            countByStreamAndRequestToJoinStatusAndAttending(FleenStream.of(streamId), APPROVED, true);
          stream.setTotalAttending(totalAttendees);
        });
    }
  }

  /**
   * Checks if the attendee is a member of a chat space and, if so, sends an invitation for joining the stream.
   *
   * <p>If the attendee is a member of the chat space associated with the stream, this method retrieves the calendar
   * associated with the user's country, creates an stream attendee entry, and sends an invitation for the user to join
   * the stream.</p>
   *
   * @param isMemberOfChatSpace a boolean indicating whether the user is a member of the chat space associated with the stream
   * @param streamExternalId the external ID of the stream the user is trying to join
   * @param comment the comment provided by the user when requesting to join the stream
   * @param user the {@link FleenUser} attempting to join the stream
   */
  @Override
  @Transactional
  public void checkIfAttendeeIsMemberOfChatSpaceAndSendInvitationForJoinStreamRequest(final boolean isMemberOfChatSpace, final String streamExternalId, final String comment, final FleenUser user) {
    if (isMemberOfChatSpace) {
      // Find calendar associated with user's country
      final Calendar calendar = miscService.findCalendar(user.getCountry());
      // Create and add stream attendee to Calendar Event and send invitation
      attendeeUpdateService.createNewEventAttendeeRequestAndSendInvitation(calendar.getExternalId(), streamExternalId, user.getEmailAddress(), comment);
    }
  }

  /**
   * Retrieves the set of attendees who are marked as attending a stream from the provided set of {@link StreamAttendee} objects.
   * This method filters the input set of attendees to include only those whose {@code attending} property is {@code true}.
   * If the input set is null, an empty set is returned.
   *
   * @param stream The stream to use to search for attendees.
   *               Each attendee's attendance status is checked to determine if they are attending the stream.
   * @return A set of {@link StreamAttendee} objects that are attending the stream.
   *         Returns an empty set if the input set is null or if no attendees are marked as attending.
   */
  @Override
  public Set<StreamAttendee> getAttendeesGoingToStream(final FleenStream stream) {
    if (nonNull(stream)) {
      return streamAttendeeRepository.findAttendeesGoingToStream(stream);
    }
    return new HashSet<>();
  }

  /**
   * Retrieves the list of attendees for a given stream identified by streamId and converts them to an StreamAttendeeResponse DTO.
   *
   * <p>If the stream with the specified streamId is not found in the database, a FleenStreamNotFoundException is thrown.</p>
   *
   * <p>The method fetches the stream details from the database using the streamId and then delegates to getAttendees method
   * to convert the attendees into a structured response.</p>
   *
   * @param streamId the unique identifier of the stream
   * @return an StreamAttendeeSearchResult containing the search result list of attendees for the stream
   * @throws FleenStreamNotFoundException if the stream with the specified streamId is not found
   */
  @Override
  public StreamAttendeeSearchResult getStreamAttendees(final Long streamId, final StreamAttendeeSearchRequest searchRequest) throws FleenStreamNotFoundException {
    // Set default number of attendees to retrieve during the search
    searchRequest.setDefaultPageSize();
    // Find and retrieve the stream
    final FleenStream stream = streamService.findStream(streamId);
    // Perform a search and retrieve the page and search result of attendees
    final Page<StreamAttendee> page = streamAttendeeRepository.findByFleenStreamAndStreamType(stream, searchRequest.getStreamType(), searchRequest.getPage());
    // Get stream attendees
    final Collection<StreamAttendeeResponse> attendeeResponses = getAttendees(stream, page.getContent());
    // Return a search result view with the attendees responses and pagination details
    return handleSearchResult(
      page,
      localizer.of(StreamAttendeeSearchResult.of(toSearchResult(List.of(attendeeResponses), page))),
      localizer.of(EmptyStreamAttendeeSearchResult.of(toSearchResult(List.of(), page)))
    );
  }

  /**
   * Retrieves the list of attendees for a given stream and converts them to an StreamAttendeeResponse DTO.
   *
   * <p>If the provided set of attendees is not null and not empty, it converts each StreamAttendee entity to
   * an StreamAttendeeResponse DTO and sets them in the response object.</p>
   *
   * <p>If no attendees are found (either the set is null or empty), it returns an empty StreamAttendeeResponse object.</p>
   *
   * @param stream the stream
   * @param attendees the set of StreamAttendee entities representing the attendees of the stream
   * @return an {@link StreamAttendeeResponse} DTO containing the list of attendees, or an empty StreamAttendeeResponse if there are no attendees
   */
  protected Collection<StreamAttendeeResponse> getAttendees(final FleenStream stream, final Collection<StreamAttendee> attendees) {
    // Check if the attendees list is not empty and set it to the list of attendees in the response
    if (nonNull(attendees) && !attendees.isEmpty()) {
      return attendees.stream()
        .filter(Objects::nonNull)
        .map(attendee -> attendeeMapper.toStreamAttendeeResponse(attendee, streamMapper.toFleenStreamResponse(stream)))
        .collect(Collectors.toSet());
    }
    return Set.of();
  }

  /**
   * Finds an attendee of the given stream based on the provided user ID.
   *
   * <p>This method first checks if the {@link FleenStream} or user ID is null, throwing a {@link FailedOperationException}
   * if any of the values are null. It then attempts to find the attendee in the repository using the stream and the user ID.
   * If the attendee exists, it is returned wrapped in an {@link Optional}; otherwise, an empty {@link Optional} is returned.</p>
   *
   * @param stream the stream to check for the attendee; must not be null
   * @param userId the ID of the user to find as an attendee; must not be null
   * @return an {@link Optional} containing the attendee if found, or an empty {@link Optional} if not
   * @throws FailedOperationException if either the stream or user ID is null
   */
  @Override
  public Optional<StreamAttendee> findAttendee(final FleenStream stream, final Long userId) throws FailedOperationException {
    // Throw an exception if the any of the provided values is null
    checkIsNullAny(Set.of(stream, userId), FailedOperationException::new);
    // Find if the user is already an attendee of the stream
    return streamAttendeeRepository.findAttendeeByStreamAndUser(stream, Member.of(userId));
  }

  /**
   * Retrieves the requests of attendees who are pending approval to join a specific stream.
   *
   * <p>This method fetches the stream based on the provided {@code streamId}, validates that the user is the creator of the stream,
   * and then retrieves a paginated list of attendees whose request to join the stream is still pending. The results are then converted
   * into response objects and returned in a structured search result format.</p>
   *
   * @param streamId The ID of the stream for which to retrieve attendee requests.
   * @param searchRequest The search request containing pagination and other filtering criteria.
   * @param user The user making the request, who must be the creator of the stream.
   * @return A {@link RequestToJoinSearchResult} containing a list of {@link StreamAttendeeResponse} objects
   *         representing attendees with pending join requests and pagination details.
   * @throws FleenStreamNotFoundException If the stream with the given ID cannot be found.
   * @throws StreamNotCreatedByUserException If the user is not the creator of the stream.
   */
  @Override
  public RequestToJoinSearchResult getAttendeeRequestsToJoinStream(final Long streamId, final StreamAttendeeSearchRequest searchRequest, final FleenUser user)
      throws FleenStreamNotFoundException, StreamNotCreatedByUserException {
    // Find the stream by its ID
    final FleenStream stream = streamService.findStream(streamId);
    // Validate owner of the stream
    validateCreatorOfStream(stream, user);

    // Find pending attendees requesting to join a stream
    final Page<StreamAttendee> page = streamAttendeeRepository.findByStreamAndRequestToJoinStatus(stream, PENDING, searchRequest.getPage());
    // Convert the stream attendee to their equivalent responses
    final List<StreamAttendeeResponse> views = toStreamAttendeeResponsesWithStatus(page.getContent(), stream);
    // Return a search result view with the attendee responses and pagination details
    return handleSearchResult(
      page,
      localizer.of(RequestToJoinSearchResult.of(toSearchResult(views, page))),
      localizer.of(EmptyRequestToJoinSearchResult.of(toSearchResult(List.of(), page)))
    );
  }

  /**
   * Converts a list of {@link StreamAttendee} objects into a list of {@link StreamAttendeeResponse} objects,
   * including each attendee's ID, full name, and request-to-join status.
   * The conversion removes duplicate attendees by transforming the list into a set before mapping.
   *
   * @param streamAttendees A list of {@link StreamAttendee} objects to be converted.
   *                        If null, an empty list will be returned.
   * @return A list of {@link StreamAttendeeResponse} objects containing the attendee's ID, full name,
   *         and request-to-join status. Duplicate entries in the input list are removed.
   */
  protected List<StreamAttendeeResponse> toStreamAttendeeResponsesWithStatus(final List<StreamAttendee> streamAttendees, final FleenStream stream) {
    // Extract the uniquely identify attendees
    final Set<StreamAttendee> streamAttendeesSet = new HashSet<>(streamAttendees);
    // Convert the attendees to their equivalent response
    final Set<StreamAttendeeResponse> streamAttendeeResponses = toStreamAttendeeResponsesSet(streamMapper.toFleenStreamResponse(stream), streamAttendeesSet);
    // Return the attendees as an array
    return new ArrayList<>(streamAttendeeResponses);
  }

  /**
   * Retrieves the set of attendee IDs from a list of StreamAttendee objects.
   *
   * @param attendees List of StreamAttendees from which the IDs will be extracted.
   * @return A set of attendee IDs.
   */
  public static Set<Long> getAttendeeIds(final List<StreamAttendee> attendees) {
    // Stream over the list of StreamAttendees
    if (nonNull(attendees)) {
      // Map each StreamAttendee to its StreamAttendeeId and collect the IDs into a set
      return attendees.stream()
        .map(StreamAttendee::getStreamAttendeeId)
        .collect(Collectors.toSet());
    }
    return Set.of();
  }

  /**
   * Groups attendee request-to-join statuses by the stream ID.
   * This method processes a list of user attendances, filters out any null values,
   * and maps the stream ID to the corresponding request-to-join status.
   *
   * @param userAttendances A list of user attendances with stream data.
   * @return A map where the key is the stream ID and the value is the request-to-join status.
   * If the input list is null or empty, an empty map is returned.
   */
  protected static Map<Long, StreamAttendeeSelect> groupAttendeeAttendanceByStreamId(final List<StreamAttendeeSelect> userAttendances) {
    // Filter null values and map stream ID to request-to-join status
    if (nonNull(userAttendances) && !userAttendances.isEmpty()) {
      return userAttendances.stream()
        .filter(Objects::nonNull)
        .collect(Collectors.toMap(StreamAttendeeSelect::getStreamId, Function.identity()));
    }
    // Return an empty map if the input list is null or empty
    return Map.of();
  }

  /**
   * Groups a list of StreamAttendeeSelect objects by their stream or stream ID.
   *
   * <p>If the list is non-null, the method delegates the grouping to the
   * {@link #groupAttendeeAttendanceByStreamId(List)} method. If the list is null, an empty map is returned.</p>
   *
   * @param attendeeAttendance a list of StreamAttendeeSelect objects containing attendance information.
   * @return a map where the key is the stream or stream ID (Long) and the value is the corresponding
   *         StreamAttendeeSelect object.
   */
  public static Map<Long, StreamAttendeeSelect> groupAttendeeAttendance(final List<StreamAttendeeSelect> attendeeAttendance) {
    if (nonNull(attendeeAttendance)) {
      return groupAttendeeAttendanceByStreamId(attendeeAttendance);
    }
    return new HashMap<>();
  }

  /**
   * Retrieves email addresses of attendees from a list of StreamAttendee objects.
   *
   * <p>This method filters out null {@link StreamAttendee} objects, retrieves the email addresses of the
   * associated attendees or members, and collects them into a set of strings.</p>
   *
   * @param streamAttendees the list of StreamAttendee objects
   * @return a set of email addresses of attendees, or an empty set if streamAttendees is null
   */
  public static Set<String> getAttendeesEmailAddresses(final List<StreamAttendee> streamAttendees) {
    if (nonNull(streamAttendees)) {
      return streamAttendees.stream()
        .filter(Objects::nonNull)
        .map(StreamAttendee::getEmailAddress)
        .collect(Collectors.toSet());
    }
    return Collections.emptySet();
  }

  /**
   * Retrieves an existing attendee for the given stream and user, or creates a new one if none exists.
   *
   * <p>This method first checks if any of the provided parameters are null and throws a {@link FailedOperationException}
   * if they are. Then, it attempts to find an attendee in the repository for the specified stream and user.
   * If an attendee is found, it is returned; otherwise, a new {@link StreamAttendee} is created using the given user,
   * stream, and comment, and the new attendee is returned.</p>
   *
   * @param stream  the stream to which the attendee is associated; must not be null
   * @param comment an optional comment associated with the attendee; may be null
   * @param user    the user who is either an existing attendee or will become the new attendee; must not be null
   * @return the existing {@link StreamAttendee} or a newly created one if no match is found
   * @throws FailedOperationException if either the stream or user is null
   */
  @Override
  public StreamAttendee getExistingOrCreateNewStreamAttendee(final FleenStream stream, final String comment, final FleenUser user) throws FailedOperationException {
    // Throw an exception if the any of the provided values is null
    checkIsNullAny(Set.of(stream, user), FailedOperationException::new);

    // Search for the member as an attendee and if it doesn't exist, create a new one
    return streamAttendeeRepository.findAttendeeByStreamAndUser(stream, user.toMember())
      .orElseGet(() -> StreamAttendee.of(user.toMember(), stream, comment));
  }

}
