package com.fleencorp.feen.service.impl.stream;

import com.fleencorp.base.model.view.search.SearchResultView;
import com.fleencorp.feen.constant.external.google.oauth2.Oauth2ServiceType;
import com.fleencorp.feen.exception.google.oauth2.Oauth2InvalidAuthorizationException;
import com.fleencorp.feen.exception.stream.FleenStreamNotFoundException;
import com.fleencorp.feen.model.domain.auth.Oauth2Authorization;
import com.fleencorp.feen.model.domain.stream.FleenStream;
import com.fleencorp.feen.model.domain.user.Member;
import com.fleencorp.feen.model.dto.livebroadcast.CreateLiveBroadcastDto;
import com.fleencorp.feen.model.dto.livebroadcast.RescheduleLiveBroadcastDto;
import com.fleencorp.feen.model.dto.livebroadcast.UpdateLiveBroadcastDto;
import com.fleencorp.feen.model.request.search.youtube.LiveBroadcastSearchRequest;
import com.fleencorp.feen.model.request.youtube.broadcast.CreateLiveBroadcastRequest;
import com.fleencorp.feen.model.request.youtube.broadcast.RescheduleLiveBroadcastRequest;
import com.fleencorp.feen.model.request.youtube.broadcast.UpdateLiveBroadcastRequest;
import com.fleencorp.feen.model.response.broadcast.CreateStreamResponse;
import com.fleencorp.feen.model.response.broadcast.RescheduleStreamResponse;
import com.fleencorp.feen.model.response.broadcast.UpdateStreamResponse;
import com.fleencorp.feen.model.response.external.google.youtube.CreateYouTubeLiveBroadcastResponse;
import com.fleencorp.feen.model.response.external.google.youtube.RescheduleYouTubeLiveBroadcastResponse;
import com.fleencorp.feen.model.response.external.google.youtube.UpdateYouTubeLiveBroadcastResponse;
import com.fleencorp.feen.model.response.stream.FleenStreamResponse;
import com.fleencorp.feen.model.security.FleenUser;
import com.fleencorp.feen.repository.oauth2.Oauth2AuthorizationRepository;
import com.fleencorp.feen.repository.stream.FleenStreamRepository;
import com.fleencorp.feen.service.impl.external.google.youtube.YouTubeLiveBroadcastService;
import com.fleencorp.feen.service.stream.LiveBroadcastService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static com.fleencorp.base.util.FleenUtil.areNotEmpty;
import static com.fleencorp.base.util.FleenUtil.toSearchResult;
import static com.fleencorp.feen.mapper.FleenStreamMapper.toFleenStreamResponse;
import static com.fleencorp.feen.mapper.FleenStreamMapper.toFleenStreams;
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

  private final YouTubeLiveBroadcastService youTubeLiveBroadcastService;
  private final FleenStreamRepository fleenStreamRepository;
  private final Oauth2AuthorizationRepository oauth2AuthorizationRepository;

  /**
   * Constructs a new instance of LiveBroadcastServiceImpl.
   *
   * <p>This constructor initializes the necessary services and repositories for handling live broadcasts,
   * including the YouTubeLiveBroadcastService for creating broadcasts on YouTube, the FleenStreamRepository
   * for managing broadcast data, and the GoogleOauth2AuthorizationRepository for OAuth2 authorization.</p>
   *
   * @param youTubeLiveBroadcastService the service to handle YouTube live broadcasts
   * @param fleenStreamRepository the repository to manage FleenStream data
   * @param oauth2AuthorizationRepository the repository to handle OAuth2 authorization
   */
  public LiveBroadcastServiceImpl(
      final YouTubeLiveBroadcastService youTubeLiveBroadcastService,
      final FleenStreamRepository fleenStreamRepository,
      final Oauth2AuthorizationRepository oauth2AuthorizationRepository) {
    this.youTubeLiveBroadcastService = youTubeLiveBroadcastService;
    this.fleenStreamRepository = fleenStreamRepository;
    this.oauth2AuthorizationRepository = oauth2AuthorizationRepository;
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
      page = fleenStreamRepository.findByDateBetween(searchRequest.getStartDate().atStartOfDay(), searchRequest.getEndDate().atStartOfDay(), searchRequest.getPage());
    } else if (nonNull(searchRequest.getTitle())) {
      page = fleenStreamRepository.findByTitle(searchRequest.getTitle(), searchRequest.getPage());
    } else {
      page = fleenStreamRepository.findMany(searchRequest.getPage());
    }

    final List<FleenStreamResponse> views = toFleenStreams(page.getContent());
    return toSearchResult(views, page);
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
  public CreateStreamResponse createLiveBroadcast(final CreateLiveBroadcastDto createLiveBroadcastDto, final FleenUser user) {
    // Check if there is a valid OAuth2 authorization for the user
    final Optional<Oauth2Authorization> existingGoogleOauth2Authorization = oauth2AuthorizationRepository.findByMemberAndServiceType(user.toMember(), Oauth2ServiceType.YOUTUBE);
    if (existingGoogleOauth2Authorization.isEmpty()) {
      throw new Oauth2InvalidAuthorizationException();
    }

    // Create a request object to create the live broadcast on YouTube
    final CreateLiveBroadcastRequest createLiveBroadcastRequest = CreateLiveBroadcastRequest.by(createLiveBroadcastDto);
    final Oauth2Authorization oauth2Authorization = existingGoogleOauth2Authorization.get();

    createLiveBroadcastRequest.setAccessTokenForHttpRequest(oauth2Authorization.getAccessToken());
    // Create the live broadcast using YouTubeLiveBroadcastService
    final CreateYouTubeLiveBroadcastResponse createYouTubeLiveBroadcastResponse = youTubeLiveBroadcastService.createBroadcast(createLiveBroadcastRequest);

    // Create a new FleenStream entity based on the DTO and set YouTube response details
    final FleenStream stream = createLiveBroadcastDto.toFleenStream(user.toMember());
    stream.setStreamLink(createYouTubeLiveBroadcastResponse.getLiveStreamLink());
    stream.setExternalId(createYouTubeLiveBroadcastResponse.getLiveBroadcastId());

    fleenStreamRepository.save(stream);
    return CreateStreamResponse.of(stream.getFleenStreamId(), toFleenStreamResponse(stream));
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
  public UpdateStreamResponse updateLiveBroadcast(final Long streamId, final UpdateLiveBroadcastDto updateLiveBroadcastDto, final FleenUser user) {
    final FleenStream stream = fleenStreamRepository.findById(streamId)
        .orElseThrow(() -> new FleenStreamNotFoundException(streamId));

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
  public RescheduleStreamResponse rescheduleLiveBroadcast(final Long streamId, final RescheduleLiveBroadcastDto rescheduleLiveBroadcastDto, final FleenUser user) {
    // Retrieve the FleenStream entity from the repository based on the stream ID
    final FleenStream stream = fleenStreamRepository.findById(streamId)
        .orElseThrow(() -> new FleenStreamNotFoundException(streamId));

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
    if (existingGoogleOauth2Authorization.isEmpty()) {
      throw new Oauth2InvalidAuthorizationException();
    }

    // Return the retrieved OAuth2 authorization
    return existingGoogleOauth2Authorization.get();
  }
}
