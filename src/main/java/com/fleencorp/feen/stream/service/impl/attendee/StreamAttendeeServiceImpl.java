package com.fleencorp.feen.stream.service.impl.attendee;

import com.fleencorp.base.model.view.search.SearchResult;
import com.fleencorp.feen.calendar.model.domain.Calendar;
import com.fleencorp.feen.chat.space.model.search.core.RequestToJoinSearchResult;
import com.fleencorp.feen.common.exception.FailedOperationException;
import com.fleencorp.feen.common.service.misc.MiscService;
import com.fleencorp.feen.shared.member.contract.IsAMember;
import com.fleencorp.feen.shared.stream.contract.IsAStream;
import com.fleencorp.feen.shared.stream.contract.IsAttendee;
import com.fleencorp.feen.stream.constant.attendee.StreamAttendeeRequestToJoinStatus;
import com.fleencorp.feen.stream.exception.core.StreamNotCreatedByUserException;
import com.fleencorp.feen.stream.exception.core.StreamNotFoundException;
import com.fleencorp.feen.stream.mapper.StreamUnifiedMapper;
import com.fleencorp.feen.stream.model.domain.FleenStream;
import com.fleencorp.feen.stream.model.domain.StreamAttendee;
import com.fleencorp.feen.stream.model.request.search.StreamAttendeeSearchRequest;
import com.fleencorp.feen.stream.model.response.StreamResponse;
import com.fleencorp.feen.stream.model.response.attendee.StreamAttendeeResponse;
import com.fleencorp.feen.stream.model.search.attendee.StreamAttendeeSearchResult;
import com.fleencorp.feen.stream.service.attendee.StreamAttendeeOperationsService;
import com.fleencorp.feen.stream.service.attendee.StreamAttendeeService;
import com.fleencorp.feen.stream.service.common.StreamOperationsService;
import com.fleencorp.localizer.service.Localizer;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static com.fleencorp.base.util.ExceptionUtil.checkIsNullAny;
import static com.fleencorp.base.util.FleenUtil.toSearchResult;
import static java.util.Objects.nonNull;

/**
 * Implementation of the {@link StreamAttendeeService} interface.
 *
 * <p>This class provides services for managing stream attendees, including searching for attendees,
 * managing their join requests, and performing operations related to stream attendees within the system.</p>
 *
 * <p>The service interacts with various components, such as {@link StreamOperationsService}, {@link StreamAttendeeOperationsService},
 * and other supporting services to provide the required functionalities for managing attendees for streams.</p>
 *
 * @author Yusuf Àlàmù Musa
 * @version 1.0
 */
@Service
public class StreamAttendeeServiceImpl implements StreamAttendeeService {

  private final MiscService miscService;
  private final StreamAttendeeOperationsService streamAttendeeOperationsService;
  private final StreamOperationsService streamOperationsService;
  private final StreamUnifiedMapper streamUnifiedMapper;
  private final Localizer localizer;

  public static final int DEFAULT_NUMBER_OF_ATTENDEES_TO_GET_FOR_STREAM = 10;

  public StreamAttendeeServiceImpl(
      final MiscService miscService,
      @Lazy final StreamAttendeeOperationsService streamAttendeeOperationsService,
      final StreamOperationsService streamOperationsService,
      final StreamUnifiedMapper streamUnifiedMapper,
      final Localizer localizer) {
    this.miscService = miscService;
    this.streamAttendeeOperationsService = streamAttendeeOperationsService;
    this.streamOperationsService = streamOperationsService;
    this.streamUnifiedMapper = streamUnifiedMapper;
    this.localizer = localizer;
  }

  /**
   * Retrieves a paginated list of attendees for a specific stream based on the search request parameters.
   *
   * <p>This method first sets the default page size for the search request, retrieves the paginated list of attendees
   * from the {@link StreamAttendeeOperationsService}, and converts the list of attendees into response objects. The method then
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
    searchRequest.setDefaultPageSize();

    final Page<IsAttendee> page = streamAttendeeOperationsService.findAttendeesGoingToStream(FleenStream.of(streamId), searchRequest.getPage());
    final FleenStream stream = streamOperationsService.findStream(streamId);

    final StreamResponse streamResponse = streamUnifiedMapper.toStreamResponse(stream);
    final Collection<StreamAttendeeResponse> attendeeResponses = streamUnifiedMapper.toStreamAttendeeResponsesPublic(page.getContent(), streamResponse);
    final SearchResult searchResult = toSearchResult(attendeeResponses, page);
    final StreamAttendeeSearchResult streamAttendeeSearchResult = StreamAttendeeSearchResult.of(searchResult);

    return localizer.of(streamAttendeeSearchResult);
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
   * @param user the {@link IsAMember} attempting to join the stream
   */
  @Override
  @Transactional
  public void checkIfAttendeeIsMemberOfChatSpaceAndSendInvitationForJoinStreamRequest(final boolean isMemberOfChatSpace, final String streamExternalId, final String comment, final IsAMember user) {
    if (isMemberOfChatSpace) {
      // Find calendar associated with user's country
      final Calendar calendar = miscService.findCalendar(user.getCountry());
      // Create and add stream attendee to Calendar Event and send invitation
      streamAttendeeOperationsService.createNewEventAttendeeRequestAndSendInvitation(calendar.getExternalId(), streamExternalId, user.getEmailAddress(), comment);
    }
  }

  /**
   * Retrieves the set of attendees who are marked as attending a stream from the provided set of {@link StreamAttendee} objects.
   * This method filters the input set of attendees to include only those whose {@code attending} property is {@code true}.
   * If the input set is null, an empty set is returned.
   *
   * @param streamResponse The stream to use to search for attendees.
   *               Each attendee's attendance status is checked to determine if they are attending the stream.
   * @return A set of {@link StreamAttendee} objects that are attending the stream.
   *         Returns an empty set if the input set is null or if no attendees are marked as attending.
   */
  @Override
  public Collection<StreamAttendeeResponse> getAttendeesGoingToStream(final StreamResponse streamResponse) {
    if (nonNull(streamResponse)) {
      final List<IsAttendee> streamAttendees = streamAttendeeOperationsService.findAttendeesGoingToStream(streamResponse.getNumberId());
      return streamUnifiedMapper.toStreamAttendeeResponsesPublic(streamAttendees, streamResponse);
    }

    return new ArrayList<>();
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
   * @throws StreamNotFoundException if the stream with the specified streamId is not found
   */
  @Override
  public StreamAttendeeSearchResult getStreamAttendees(final Long streamId, final StreamAttendeeSearchRequest searchRequest) throws StreamNotFoundException {
    searchRequest.setDefaultPageSize();

    final FleenStream stream = streamOperationsService.findStream(streamId);
    final Page<IsAttendee> page = streamAttendeeOperationsService.findByStreamAndStreamType(stream, searchRequest.getStreamType(), searchRequest.getPage());
    final Collection<StreamAttendeeResponse> attendeeResponses = getAttendees(stream, page.getContent());
    final SearchResult searchResult = toSearchResult(attendeeResponses, page);
    final StreamAttendeeSearchResult streamAttendeeSearchResult = StreamAttendeeSearchResult.of(searchResult);

    return localizer.of(streamAttendeeSearchResult);
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
  protected Collection<StreamAttendeeResponse> getAttendees(final FleenStream stream, final Collection<IsAttendee> attendees) {
    // Check if the attendees list is not empty and set it to the list of attendees in the response
    if (nonNull(attendees) && !attendees.isEmpty()) {
      return attendees.stream()
        .filter(Objects::nonNull)
        .map(attendee -> {
          final StreamResponse streamResponse = streamUnifiedMapper.toStreamResponse(stream);
          return streamUnifiedMapper.toStreamAttendeeResponse(attendee, streamResponse);
        })
        .collect(Collectors.toSet());
    }
    return Set.of();
  }

  /**
   * Finds an attendee of the given stream based on the provided user ID.
   *
   * <p>This method first checks if the {@link IsAStream} or user ID is null, throwing a {@link FailedOperationException}
   * if any of the values are null. It then attempts to find the attendee in the repository using the stream and the user ID.
   * If the attendee exists, it is returned wrapped in an {@link Optional}; otherwise, an empty {@link Optional} is returned.</p>
   *
   * @param streamId the stream to check for the attendee; must not be null
   * @param userId the ID of the user to find as an attendee; must not be null
   * @return an {@link Optional} containing the attendee if found, or an empty {@link Optional} if not
   * @throws FailedOperationException if either the stream or user ID is null
   */
  @Override
  public Optional<StreamAttendee> findAttendeeByMemberId(final Long streamId, final Long userId) throws FailedOperationException {
    checkIsNullAny(Set.of(streamId, userId), FailedOperationException::new);
    return streamAttendeeOperationsService.findAttendeeByStreamAndUser(streamId, userId);
  }

  /**
   * Finds an attendee of a specified stream by their attendee ID.
   *
   * <p>This method checks if the provided stream and attendee ID are non-null. If either is null,
   * it throws a {@link FailedOperationException}. Upon successful validation, it queries the
   * {@link StreamAttendeeOperationsService} to find the attendee with the specified ID within the
   * given stream.</p>
   *
   * @param streamId The {@link IsAStream} instance representing the stream where the attendee is expected.
   * @param attendeeId The unique ID of the attendee to find.
   * @return An {@link Optional} containing the {@link StreamAttendee} if found, or an empty Optional if not found.
   * @throws FailedOperationException If either the stream or attendeeId is null.
   */
  @Override
  public Optional<StreamAttendee> findAttendee(final Long streamId, final Long attendeeId) throws FailedOperationException {
    checkIsNullAny(Set.of(streamId, attendeeId), FailedOperationException::new);

    return streamAttendeeOperationsService.findAttendeeByIdAndStream(attendeeId, streamId);
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
   * @throws StreamNotFoundException If the stream with the given ID cannot be found.
   * @throws StreamNotCreatedByUserException If the user is not the creator of the stream.
   */
  @Override
  public RequestToJoinSearchResult getAttendeeRequestsToJoinStream(final Long streamId, final StreamAttendeeSearchRequest searchRequest, final IsAMember user)
      throws StreamNotFoundException, StreamNotCreatedByUserException {
    final IsAStream stream = streamOperationsService.findStream(streamId);
    stream.checkIsOrganizer(user.getMemberId());

    final Set<StreamAttendeeRequestToJoinStatus> joinStatusesForSearch = searchRequest.forPendingOrDisapprovedRequestToJoinStatus();
    final Page<IsAttendee> page = streamAttendeeOperationsService.findByStreamAndRequestToJoinStatus(stream, joinStatusesForSearch, searchRequest.getPage());

    final StreamResponse streamResponse = streamUnifiedMapper.toStreamResponse(stream);
    final Collection<StreamAttendeeResponse> streamAttendeeResponses = streamUnifiedMapper.toStreamAttendeeResponsesPublic(page.getContent(), streamResponse);

    final SearchResult searchResult = toSearchResult(streamAttendeeResponses, page);
    final RequestToJoinSearchResult requestToJoinSearchResult = RequestToJoinSearchResult.of(searchResult);

    return localizer.of(requestToJoinSearchResult);
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
  public StreamAttendee getExistingOrCreateNewStreamAttendee(final IsAStream stream, final String comment, final IsAMember user) throws FailedOperationException {
    // Throw an exception if the any of the provided values is null
    checkIsNullAny(Set.of(stream, user), FailedOperationException::new);

    // Search for the member as an attendee and if it doesn't exist, create a new one
    return streamAttendeeOperationsService.findAttendeeByStreamAndUser(stream.getStreamId(), user.getMemberId())
      .orElseGet(() -> StreamAttendee.of(user, stream, comment));
  }

}