package com.fleencorp.feen.service.impl.stream;

import com.fleencorp.base.model.view.search.SearchResultView;
import com.fleencorp.feen.constant.external.google.oauth2.Oauth2ServiceType;
import com.fleencorp.feen.constant.stream.StreamStatus;
import com.fleencorp.feen.exception.google.oauth2.Oauth2InvalidAuthorizationException;
import com.fleencorp.feen.exception.stream.FleenStreamNotFoundException;
import com.fleencorp.feen.model.domain.auth.Oauth2Authorization;
import com.fleencorp.feen.model.domain.stream.FleenStream;
import com.fleencorp.feen.model.domain.stream.StreamAttendee;
import com.fleencorp.feen.model.domain.user.Member;
import com.fleencorp.feen.model.dto.livebroadcast.CreateLiveBroadcastDto;
import com.fleencorp.feen.model.dto.livebroadcast.RescheduleLiveBroadcastDto;
import com.fleencorp.feen.model.dto.livebroadcast.UpdateLiveBroadcastDto;
import com.fleencorp.feen.model.dto.stream.ProcessAttendeeRequestToJoinEventOrStreamDto;
import com.fleencorp.feen.model.dto.stream.UpdateEventOrStreamVisibilityDto;
import com.fleencorp.feen.model.request.search.stream.StreamAttendeeSearchRequest;
import com.fleencorp.feen.model.request.search.youtube.LiveBroadcastSearchRequest;
import com.fleencorp.feen.model.request.youtube.broadcast.CreateLiveBroadcastRequest;
import com.fleencorp.feen.model.request.youtube.broadcast.RescheduleLiveBroadcastRequest;
import com.fleencorp.feen.model.request.youtube.broadcast.UpdateLiveBroadcastRequest;
import com.fleencorp.feen.model.response.broadcast.*;
import com.fleencorp.feen.model.response.external.google.youtube.CreateYouTubeLiveBroadcastResponse;
import com.fleencorp.feen.model.response.external.google.youtube.RescheduleYouTubeLiveBroadcastResponse;
import com.fleencorp.feen.model.response.external.google.youtube.UpdateYouTubeLiveBroadcastResponse;
import com.fleencorp.feen.model.response.external.google.youtube.category.YouTubeCategoriesResponse;
import com.fleencorp.feen.model.response.stream.DataForCreateStreamResponse;
import com.fleencorp.feen.model.response.stream.FleenStreamResponse;
import com.fleencorp.feen.model.response.stream.StreamAttendeeResponse;
import com.fleencorp.feen.model.security.FleenUser;
import com.fleencorp.feen.repository.oauth2.Oauth2AuthorizationRepository;
import com.fleencorp.feen.repository.stream.FleenStreamRepository;
import com.fleencorp.feen.repository.stream.StreamAttendeeRepository;
import com.fleencorp.feen.service.i18n.LocalizedResponse;
import com.fleencorp.feen.service.impl.external.google.oauth2.GoogleOauth2Service;
import com.fleencorp.feen.service.impl.external.google.youtube.YouTubeChannelService;
import com.fleencorp.feen.service.impl.external.google.youtube.YouTubeLiveBroadcastService;
import com.fleencorp.feen.service.stream.LiveBroadcastService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.fleencorp.base.util.FleenUtil.areNotEmpty;
import static com.fleencorp.base.util.FleenUtil.toSearchResult;
import static com.fleencorp.feen.constant.stream.StreamAttendeeRequestToJoinStatus.APPROVED;
import static com.fleencorp.feen.mapper.FleenStreamMapper.toFleenStreamResponse;
import static com.fleencorp.feen.mapper.FleenStreamMapper.toFleenStreams;
import static com.fleencorp.feen.service.impl.stream.EventServiceImpl.getAttendeesGoingToStream;
import static com.fleencorp.feen.service.impl.stream.EventServiceImpl.toStreamAttendeeResponses;
import static com.fleencorp.feen.util.ExceptionUtil.checkIsTrue;
import static com.fleencorp.feen.validator.impl.TimezoneValidValidator.getAvailableTimezones;
import static java.util.Objects.nonNull;

/**
 * Implementation of LiveBroadcastService that handles creating live broadcasts and searching for them.
 *
 * <p>This class interacts with YouTubeLiveBroadcastService to create live broadcasts,
 * FleenStreamRepository to store and retrieve live broadcast data, and GoogleOauth2AuthorizationRepository
 * to handle OAuth2 authorization.</p>
 *
 * <p>It is responsible for creating live broadcasts using details from CreateLiveBroadcastDto,
 * verifying OAuth2 authorization, and saving the broadcast data. Additionally, it supports
 * finding live broadcasts based on search criteria such as date range, title, or general pagination.</p>
 *
 * @author Yusuf Alamu Musa
 * @version 1.0
 */
@Slf4j
@Service
public class LiveBroadcastServiceImpl implements LiveBroadcastService {

  private final GoogleOauth2Service googleOauth2Service;
  private final YouTubeLiveBroadcastService youTubeLiveBroadcastService;
  private final YouTubeChannelService youTubeChannelService;
  private final FleenStreamRepository fleenStreamRepository;
  private final StreamAttendeeRepository streamAttendeeRepository;
  private final Oauth2AuthorizationRepository oauth2AuthorizationRepository;
  private final LocalizedResponse localizedResponse;

  /**
   * Constructs a new instance of LiveBroadcastServiceImpl.
   *
   * <p>This constructor initializes the necessary services and repositories for handling live broadcasts,
   * including the YouTubeLiveBroadcastService for creating broadcasts on YouTube, the FleenStreamRepository
   * for managing broadcast data, and the GoogleOauth2AuthorizationRepository for OAuth2 authorization.</p>
   *
   * @param googleOauth2Service the service for interacting with Google Oauth2 service
   * @param youTubeLiveBroadcastService the service to handle YouTube live broadcasts
   * @param youTubeChannelService the service for managing YouTube channels and categories
   * @param fleenStreamRepository the repository to manage FleenStream data
   * @param streamAttendeeRepository the repository for managing event or stream attendees
   * @param oauth2AuthorizationRepository the repository to handle OAuth2 authorization
   * @param localizedResponse the service for creating localized response message
   */
  public LiveBroadcastServiceImpl(
      final GoogleOauth2Service googleOauth2Service,
      final YouTubeLiveBroadcastService youTubeLiveBroadcastService,
      final YouTubeChannelService youTubeChannelService,
      final FleenStreamRepository fleenStreamRepository,
      final StreamAttendeeRepository streamAttendeeRepository,
      final Oauth2AuthorizationRepository oauth2AuthorizationRepository,
      final LocalizedResponse localizedResponse) {
    this.googleOauth2Service = googleOauth2Service;
    this.youTubeLiveBroadcastService = youTubeLiveBroadcastService;
    this.youTubeChannelService = youTubeChannelService;
    this.fleenStreamRepository = fleenStreamRepository;
    this.streamAttendeeRepository = streamAttendeeRepository;
    this.oauth2AuthorizationRepository = oauth2AuthorizationRepository;
    this.localizedResponse = localizedResponse;
  }

  /**
   * Retrieves the data required for creating a stream, including available timezones and YouTube categories.
   *
   * @return a {@link DataForCreateStreamResponse} containing the set of available timezones and
   *         the list of YouTube categories
   */
  @Override
  public DataForCreateStreamResponse getDataForCreateStream() {
    final Set<String> timezones = getAvailableTimezones();
    final YouTubeCategoriesResponse categoriesResponse = youTubeChannelService.listCategories();
    return DataForCreateStreamResponse.of(timezones, categoriesResponse.getCategories());
  }

  /**
   * Finds live broadcasts based on the search criteria provided in the LiveBroadcastSearchRequest.
   *
   * <p>This method searches for live broadcasts in the repository by checking if start and end dates
   * are provided in the search request. If both dates are provided, it searches for broadcasts within
   * the specified date range. If only a title is provided, it searches for broadcasts by title. If no
   * specific criteria are provided, it retrieves a default set of broadcasts.</p>
   *
   * <p>The method then converts the retrieved broadcasts into a list of FleenStreamResponse objects
   * and returns a SearchResultView containing the search results.</p>
   *
   * @param searchRequest the request object containing search criteria
   * @return a SearchResultView containing the search results
   */
  @Override
  public SearchResultView findLiveBroadcasts(final LiveBroadcastSearchRequest searchRequest) {
    final Page<FleenStream> page;
    if (areNotEmpty(searchRequest.getStartDate(), searchRequest.getEndDate())) {
      page = fleenStreamRepository.findByDateBetween(searchRequest.getStartDateTime(), searchRequest.getEndDateTime(), StreamStatus.ACTIVE, searchRequest.getPage());
    } else if (nonNull(searchRequest.getTitle())) {
      page = fleenStreamRepository.findByTitle(searchRequest.getTitle(), StreamStatus.ACTIVE, searchRequest.getPage());
    } else {
      page = fleenStreamRepository.findMany(StreamStatus.ACTIVE, searchRequest.getPage());
    }

    final List<FleenStreamResponse> views = toFleenStreams(page.getContent());
    return toSearchResult(views, page);
  }

  /**
   * Retrieves details about a specific stream, including its attendees and their statuses.
   *
   * @param streamId the ID of the stream to retrieve
   * @return a {@link RetrieveStreamResponse} containing details of the stream, its attendees, and the total count of approved attendees
   */
  @Override
  public RetrieveStreamResponse retrieveStream(final Long streamId) {
    // Find the stream by its ID
    final FleenStream stream = findStream(streamId);
    // Get all event or stream attendees
    final Set<StreamAttendee> streamAttendeesGoingToStream = getAttendeesGoingToStream(stream.getAttendees());
    // Convert the attendees to response objects
    final Set<StreamAttendeeResponse> streamAttendees = toStreamAttendeeResponses(streamAttendeesGoingToStream);
    // Count total attendees whose request to join event is approved and are attending the event because they are interested
    final long totalAttendees = streamAttendeeRepository.countByFleenStreamAndStreamAttendeeRequestToJoinStatusAndIsAttending(stream, APPROVED, true);
    return localizedResponse.of(RetrieveStreamResponse.of(streamId, toFleenStreamResponse(stream), streamAttendees, totalAttendees));
  }

  /**
   * Creates a live broadcast on YouTube using the provided details and associates it with a new FleenStream entity.
   *
   * <p>This method first checks if there is a valid OAuth2 authorization for the user. It constructs a
   * {@link CreateLiveBroadcastRequest} using the details from {@link CreateLiveBroadcastDto} and sets the access token
   * for the HTTP request. The live broadcast is created using {@link YouTubeLiveBroadcastService#createBroadcast(CreateLiveBroadcastRequest)}.</p>
   *
   * <p>Upon successful creation of the YouTube live broadcast, a new {@link FleenStream} entity is instantiated using
   * {@link CreateLiveBroadcastDto#toFleenStream()}. The live stream link and broadcast ID from the YouTube response are
   * set to the stream entity. Finally, the stream entity is saved to the repository, and a {@link CreateStreamResponse}
   * is returned with the newly created stream ID and its corresponding {@link FleenStreamResponse}.</p>
   *
   * @param createLiveBroadcastDto The DTO containing details for creating the live broadcast.
   * @param user                   The authenticated user initiating the broadcast creation.
   * @return A {@link CreateStreamResponse} object containing the details of the created stream.
   * @throws Oauth2InvalidAuthorizationException If the OAuth2 authorization for the user is invalid or missing.
   */
  @Override
  @Transactional
  public CreateStreamResponse createLiveBroadcast(final CreateLiveBroadcastDto createLiveBroadcastDto, final FleenUser user) {
    // Check if there is a valid OAuth2 authorization for the user
    final Oauth2Authorization oauth2Authorization = validateAccessTokenExpiryTimeOrRefreshToken(createLiveBroadcastDto.getOauth2ServiceType(), user);

    // Create a request object to create the live broadcast on YouTube
    final CreateLiveBroadcastRequest createLiveBroadcastRequest = CreateLiveBroadcastRequest.by(createLiveBroadcastDto);
    // Update access token needed to perform request
    createLiveBroadcastRequest.updateToken(oauth2Authorization.getAccessToken());
    // Create the live broadcast using YouTubeLiveBroadcastService
    final CreateYouTubeLiveBroadcastResponse createYouTubeLiveBroadcastResponse = youTubeLiveBroadcastService.createBroadcast(createLiveBroadcastRequest);

    // Create a new FleenStream entity based on the DTO and set YouTube response details
    final FleenStream stream = createLiveBroadcastDto.toFleenStream(user.toMember());
    stream.setStreamLink(createYouTubeLiveBroadcastResponse.getLiveStreamLink());
    stream.setExternalId(createYouTubeLiveBroadcastResponse.getLiveBroadcastId());

    fleenStreamRepository.save(stream);
    return localizedResponse.of(CreateStreamResponse.of(stream.getFleenStreamId(), toFleenStreamResponse(stream)));
  }

  /**
   * Updates a live broadcast associated with the specified stream ID using the provided details.
   *
   * <p>This method retrieves the {@link FleenStream} entity from the repository based on the given stream ID.
   * It verifies the OAuth2 authorization for the user and constructs an {@link UpdateLiveBroadcastRequest}
   * using the access token and update details from {@link UpdateLiveBroadcastDto}. The live broadcast is then
   * updated using {@link YouTubeLiveBroadcastService#updateLiveBroadcast(UpdateLiveBroadcastRequest)}.</p>
   *
   * <p>If the stream and authorization are valid, the method updates the stream's title, description, tags, and
   * location. It saves the updated stream back to the repository and returns an {@link UpdateStreamResponse} with
   * the updated stream ID and its corresponding {@link FleenStreamResponse}.</p>
   *
   * <p>If the stream ID does not exist in the repository or the OAuth2 authorization is invalid, appropriate
   * exceptions are thrown.</p>
   *
   * @param streamId               The ID of the stream to update.
   * @param updateLiveBroadcastDto The DTO containing updated details for the live broadcast.
   * @param user                   The authenticated user initiating the update.
   * @return An {@link UpdateStreamResponse} object representing the updated stream response.
   * @throws FleenStreamNotFoundException     If the specified stream ID does not exist in the repository.
   * @throws Oauth2InvalidAuthorizationException If the OAuth2 authorization for the user is invalid or missing.
   */
  @Override
  @Transactional
  public UpdateStreamResponse updateLiveBroadcast(final Long streamId, final UpdateLiveBroadcastDto updateLiveBroadcastDto, final FleenUser user) {
    final FleenStream stream = findStream(streamId);

    // Check if the OAuth2 authorization exists for the user
    final Oauth2Authorization oauth2Authorization = verifyAndGetUserOauth2Authorization(user);
    // Create an update request using the access token and update details
    final UpdateLiveBroadcastRequest updateLiveBroadcastRequest = UpdateLiveBroadcastRequest
      .of(oauth2Authorization.getAccessToken(),
          updateLiveBroadcastDto.getTitle(),
          updateLiveBroadcastDto.getDescription());

    // Update the live broadcast using YouTubeLiveBroadcastService
    final UpdateYouTubeLiveBroadcastResponse updateYouTubeLiveBroadcastResponse = youTubeLiveBroadcastService.updateLiveBroadcast(updateLiveBroadcastRequest);
    log.info("Updated broadcast: {}", updateYouTubeLiveBroadcastResponse);

    // Update the stream entity with new title, description, tags, and location
    stream.update(
        updateLiveBroadcastDto.getTitle(),
        updateLiveBroadcastDto.getDescription(),
        updateLiveBroadcastDto.getTags(),
        updateLiveBroadcastDto.getLocation());

    fleenStreamRepository.save(stream);
    return UpdateStreamResponse.of(streamId, toFleenStreamResponse(stream));
  }

  /**
   * Reschedules a live broadcast associated with the specified stream ID using the provided details.
   *
   * <p>This method retrieves the {@link FleenStream} entity from the repository based on the stream ID. It validates
   * the OAuth2 authorization for the user and constructs a {@link RescheduleLiveBroadcastRequest} using the provided
   * reschedule details and access token. The live broadcast is then rescheduled using
   * {@link YouTubeLiveBroadcastService#rescheduleLiveBroadcast(RescheduleLiveBroadcastRequest)}.</p>
   *
   * <p>After successfully rescheduling the YouTube live broadcast, the method updates the schedule of the corresponding
   * {@link FleenStream} entity with the new start and end times and timezone. The updated stream entity is saved back
   * to the repository, and a {@link RescheduleStreamResponse} is returned with the rescheduled stream ID and its
   * corresponding {@link FleenStreamResponse}.</p>
   *
   * @param streamId                  The ID of the FleenStream to reschedule.
   * @param rescheduleLiveBroadcastDto The DTO containing details for rescheduling the live broadcast.
   * @param user                      The authenticated user initiating the broadcast rescheduling.
   * @return A {@link RescheduleStreamResponse} object containing the details of the rescheduled stream.
   * @throws FleenStreamNotFoundException    If the FleenStream with the specified ID is not found.
   * @throws Oauth2InvalidAuthorizationException If the OAuth2 authorization for the user is invalid or missing.
   */
  @Override
  @Transactional
  public RescheduleStreamResponse rescheduleLiveBroadcast(final Long streamId, final RescheduleLiveBroadcastDto rescheduleLiveBroadcastDto, final FleenUser user) {
    // Retrieve the FleenStream entity from the repository based on the stream ID
    final FleenStream stream = findStream(streamId);

    // Check if the OAuth2 authorization exists for the user
    final Oauth2Authorization oauth2Authorization = verifyAndGetUserOauth2Authorization(user);
    // Create a request object to reschedule the live broadcast on YouTube
    final RescheduleLiveBroadcastRequest rescheduleLiveBroadcastRequest = RescheduleLiveBroadcastRequest
      .of(oauth2Authorization.getAccessToken(),
          rescheduleLiveBroadcastDto.getActualStartDateTime(),
          rescheduleLiveBroadcastDto.getActualEndDateTime(), null);

    // Reschedule the live broadcast using YouTubeLiveBroadcastService
    final RescheduleYouTubeLiveBroadcastResponse rescheduleYouTubeLiveBroadcastResponse = youTubeLiveBroadcastService.rescheduleLiveBroadcast(rescheduleLiveBroadcastRequest);
    log.info("Rescheduled broadcast: {}", rescheduleYouTubeLiveBroadcastResponse);

    // Update the schedule of the FleenStream entity with new start and end times and timezone
    stream.updateSchedule(
      rescheduleLiveBroadcastDto.getActualStartDateTime(),
      rescheduleLiveBroadcastDto.getActualEndDateTime(),
      rescheduleLiveBroadcastDto.getTimezone());

    fleenStreamRepository.save(stream);
    return RescheduleStreamResponse.of(streamId, toFleenStreamResponse(stream));
  }

  /**
   * Updates the attendance status of a user for a specific event to indicate they are not attending.
   *
   * @param eventId the ID of the event
   * @param user the user who is indicating they are not attending
   * @return a {@link NotAttendingStreamResponse} indicating the outcome of the operation
   */
  @Override
  @Transactional
  public NotAttendingStreamResponse notAttendingStream(final Long eventId, final FleenUser user) {
    // Find the stream by its ID
    final FleenStream stream = findStream(eventId);
    // Find the existing attendee record for the user and event
    streamAttendeeRepository.findByFleenStreamAndMember(stream, user.toMember())
      .ifPresent(streamAttendee -> {
        // If an attendee record exists, update their attendance status to false
        streamAttendee.setIsNotAttending();
        // Save the updated attendee record
        streamAttendeeRepository.save(streamAttendee);
      });
    // Build and return the response indicating the user is no longer attending
    return localizedResponse.of(NotAttendingStreamResponse.of());
  }

  @Override
  public ProcessAttendeeRequestToJoinStreamResponse processAttendeeRequestToJoinStream(Long streamId, ProcessAttendeeRequestToJoinEventOrStreamDto processAttendeeRequestToJoinEventOrStreamDto, FleenUser user) {
    return null;
  }

  @Override
  public DeletedStreamResponse deleteStream(Long streamId, FleenUser user) {
    return null;
  }

  @Override
  public SearchResultView getAttendeeRequestsToJoinStream(Long streamId, StreamAttendeeSearchRequest searchRequest, FleenUser user) {
    return null;
  }

  @Override
  public UpdateStreamVisibilityResponse updateStreamVisibility(Long streamId, UpdateEventOrStreamVisibilityDto updateEventOrStreamVisibilityDto, FleenUser user) {
    return null;
  }

  /**
   * Validates the expiry time of the access token for the specified OAuth2 service type and user,
   * or refreshes the token if necessary.
   *
   * @param oauth2ServiceType the type of OAuth2 service (e.g., Google, Facebook) to validate or refresh the token for
   * @param user the user whose access token is being validated or refreshed
   * @return an {@link Oauth2Authorization} object containing updated authorization details
   */
  public Oauth2Authorization validateAccessTokenExpiryTimeOrRefreshToken(final Oauth2ServiceType oauth2ServiceType, final FleenUser user) {
    return googleOauth2Service.validateAccessTokenExpiryTimeOrRefreshToken(oauth2ServiceType, user);
  }

  /**
   * Verifies and retrieves the OAuth2 authorization details for the specified FleenUser.
   *
   * <p>This method delegates to {@link #verifyAndGetUserOauth2Authorization(Member)}
   * using the Member representation of the provided FleenUser.</p>
   *
   * @param user The FleenUser whose OAuth2 authorization needs to be verified and retrieved.
   * @return The {@link Oauth2Authorization} entity associated with the specified FleenUser.
   * @throws Oauth2InvalidAuthorizationException If no valid OAuth2 authorization is found for the FleenUser.
   */
  public Oauth2Authorization verifyAndGetUserOauth2Authorization(final FleenUser user) {
    // Delegate to verifyAndGetUserOauth2Authorization(Member) method
    return verifyAndGetUserOauth2Authorization(user.toMember());
  }

  /**
   * Verifies and retrieves the OAuth2 authorization details for the specified member.
   *
   * <p>This method retrieves the {@link Oauth2Authorization} entity associated with the provided member
   * from the repository. If no authorization is found, an {@link Oauth2InvalidAuthorizationException} is thrown.
   * Otherwise, it returns the retrieved {@link Oauth2Authorization}.</p>
   *
   * @param member The member whose OAuth2 authorization needs to be verified and retrieved.
   * @return The {@link Oauth2Authorization} entity associated with the specified member.
   * @throws Oauth2InvalidAuthorizationException If no valid OAuth2 authorization is found for the member.
   */
  public Oauth2Authorization verifyAndGetUserOauth2Authorization(final Member member) {
    // Retrieve the OAuth2 authorization entity associated with the member
    final Optional<Oauth2Authorization> existingGoogleOauth2Authorization = oauth2AuthorizationRepository.findByMemberAndServiceType(member, Oauth2ServiceType.YOUTUBE);
    // Throw exception if no authorization is found
    checkIsTrue(existingGoogleOauth2Authorization.isEmpty(), Oauth2InvalidAuthorizationException::new);
    // Return the retrieved OAuth2 authorization
    return existingGoogleOauth2Authorization.get();
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
  protected FleenStream findStream(final Long eventId) {
    return fleenStreamRepository.findById(eventId)
      .orElseThrow(() -> new FleenStreamNotFoundException(eventId));
  }
}
