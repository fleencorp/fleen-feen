package com.fleencorp.feen.stream.service.impl.common;

import com.fleencorp.feen.model.response.external.google.youtube.category.YouTubeCategoriesResponse;
import com.fleencorp.feen.oauth2.constant.Oauth2ServiceType;
import com.fleencorp.feen.oauth2.exception.core.Oauth2InvalidAuthorizationException;
import com.fleencorp.feen.oauth2.model.domain.Oauth2Authorization;
import com.fleencorp.feen.oauth2.service.external.BaseOauth2Service;
import com.fleencorp.feen.shared.member.contract.IsAMember;
import com.fleencorp.feen.shared.security.RegisteredUser;
import com.fleencorp.feen.stream.mapper.StreamUnifiedMapper;
import com.fleencorp.feen.stream.model.domain.FleenStream;
import com.fleencorp.feen.stream.model.dto.livebroadcast.CreateLiveBroadcastDto;
import com.fleencorp.feen.stream.model.info.core.StreamTypeInfo;
import com.fleencorp.feen.stream.model.request.external.ExternalStreamRequest;
import com.fleencorp.feen.stream.model.request.external.broadcast.CreateLiveBroadcastRequest;
import com.fleencorp.feen.stream.model.response.StreamResponse;
import com.fleencorp.feen.stream.model.response.base.CreateStreamResponse;
import com.fleencorp.feen.stream.model.response.common.live.broadcast.DataForCreateLiveBroadcastResponse;
import com.fleencorp.feen.stream.service.common.LiveBroadcastService;
import com.fleencorp.feen.stream.service.common.StreamOperationsService;
import com.fleencorp.feen.stream.service.core.StreamRequestService;
import com.fleencorp.feen.stream.service.external.YouTubeChannelService;
import com.fleencorp.feen.stream.service.impl.update.LiveBroadcastUpdateService;
import com.fleencorp.localizer.service.Localizer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

import static com.fleencorp.feen.common.validator.impl.TimezoneValidValidator.getAvailableTimezones;

@Slf4j
@Service
public class LiveBroadcastServiceImpl implements LiveBroadcastService, StreamRequestService {

  private final BaseOauth2Service baseOauth2Service;
  private final StreamOperationsService streamOperationsService;
  private final LiveBroadcastUpdateService liveBroadcastUpdateService;
  private final YouTubeChannelService youTubeChannelService;
  private final StreamUnifiedMapper streamUnifiedMapper;
  private final Localizer localizer;

  public LiveBroadcastServiceImpl(
      final BaseOauth2Service baseOauth2Service,
      final StreamOperationsService streamOperationsService,
      @Lazy final LiveBroadcastUpdateService liveBroadcastUpdateService,
      final YouTubeChannelService youTubeChannelService,
      final Localizer localizer,
      final StreamUnifiedMapper streamUnifiedMapper) {
    this.baseOauth2Service = baseOauth2Service;
    this.streamOperationsService = streamOperationsService;
    this.liveBroadcastUpdateService = liveBroadcastUpdateService;
    this.youTubeChannelService = youTubeChannelService;
    this.streamUnifiedMapper = streamUnifiedMapper;
    this.localizer = localizer;
  }

  /**
   * Retrieves the necessary data for creating a live broadcast, including available timezones
   * and YouTube categories.
   *
   * <p>This method fetches the list of available timezones and assigns YouTube categories that can
   * be used during the live broadcast creation process. It then returns this data in the form of a
   * {@link DataForCreateLiveBroadcastResponse} containing the timezones and categories.</p>
   *
   * @return a {@link DataForCreateLiveBroadcastResponse} containing available timezones and YouTube categories
   */
  @Override
  public DataForCreateLiveBroadcastResponse getDataForCreateLiveBroadcast() {
    // Retrieve the available timezones from the system
    final Set<String> timezones = getAvailableTimezones();
    // Retrieve the list of YouTube categories
    final YouTubeCategoriesResponse categoriesResponse = youTubeChannelService.listAssignableCategories();
    // Return the response containing the details to create the live broadcast
    return DataForCreateLiveBroadcastResponse.of(timezones, categoriesResponse.getCategories());
  }

  /**
   * Creates a live broadcast stream on an external platform (e.g., YouTube Live Stream).
   *
   * <p>This method first checks if the user has a valid OAuth2 authorization. If the authorization is
   * valid, it creates a new {@link FleenStream} object based on the provided DTO, updates the stream's
   * details with the user's information, and increments the attendee count. The user is then registered
   * as an attendee for the live broadcast.</p>
   *
   * <p>Next, the method builds a request to create the live broadcast, calls an external service to
   * create the live broadcast on a platform like YouTube, and retrieves the response. Finally, the
   * method returns a localized response containing details about the created live broadcast stream,
   * including the stream ID, stream type info, and other relevant details.</p>
   *
   * @param createLiveBroadcastDto the DTO containing details for creating the live broadcast
   * @param user the user who is organizing the live broadcast
   * @return a {@link CreateStreamResponse} containing the details of the created live broadcast stream
   * @throws Oauth2InvalidAuthorizationException if the OAuth2 authorization is invalid or expired
   */
  @Override
  @Transactional
  public CreateStreamResponse createLiveBroadcast(final CreateLiveBroadcastDto createLiveBroadcastDto, final RegisteredUser user) throws Oauth2InvalidAuthorizationException {
    final IsAMember member = user.toMember();
    final Oauth2Authorization oauth2Authorization = validateAccessTokenExpiryTimeOrRefreshToken(createLiveBroadcastDto.getOauth2ServiceType(), user);
    final String organizerAliasOrDisplayName = createLiveBroadcastDto.getOrganizerAlias(user.getFullName());
    final FleenStream stream = createLiveBroadcastDto.toFleenStream(member);
    stream.update(
      organizerAliasOrDisplayName,
      member.getEmailAddress(),
      member.getPhoneNumber()
    );

    streamOperationsService.increaseTotalAttendeesOrGuests(stream);
    streamOperationsService.registerAndApproveOrganizerOfStreamAsAnAttendee(stream, member);

    final ExternalStreamRequest createStreamRequest = ExternalStreamRequest.ofCreateLiveBroadcast(oauth2Authorization, stream, stream.getStreamType(), createLiveBroadcastDto);
    createLiveBroadcastExternally(createStreamRequest);

    final StreamResponse streamResponse = streamUnifiedMapper.toStreamResponseByAdminUpdate(stream);
    final StreamTypeInfo streamTypeInfo = streamUnifiedMapper.toStreamTypeInfo(stream.getStreamType());

    return localizer.of(CreateStreamResponse.of(stream.getStreamId(), streamTypeInfo, streamResponse));
  }

  /**
   * Creates a live broadcast in an external service, such as the YouTube Live Stream API.
   *
   * <p>This method checks if the provided stream request is a live broadcast. If it is, it creates a
   * {@link CreateLiveBroadcastRequest} from the provided DTO, updates it with the necessary access token,
   * and then sends the request to create the live broadcast externally using an external service like
   * the YouTube Live Stream API.</p>
   *
   * @param createStreamRequest the request containing details about the stream and the live broadcast
   *                            to be created, including access token and broadcast information
   */
  protected void createLiveBroadcastExternally(final ExternalStreamRequest createStreamRequest) {
    if (createStreamRequest.isABroadcast() && createStreamRequest.isCreateLiveBroadcastRequest()) {
      // Create a request object for the live broadcast based on the dto
      final CreateLiveBroadcastRequest createLiveBroadcastRequest = CreateLiveBroadcastRequest.by(createStreamRequest.getCreateLiveBroadcastDto());
      // Update access token needed to perform request
      createLiveBroadcastRequest.updateToken(createStreamRequest.accessToken());
      // Create and add live broadcast or stream in external service for example YouTube Live Stream API
      liveBroadcastUpdateService.createLiveBroadcastAndStream(createStreamRequest.getStream(), createLiveBroadcastRequest);
    }
  }

  /**
   * Validates the expiry time of the access token for the specified OAuth2 service type and user,
   * or refreshes the token if necessary.
   *
   * @param oauth2ServiceType the type of OAuth2 service (e.g., Google, Facebook) to validate or refresh the token for
   * @param user the user whose access token is being validated or refreshed
   * @return an {@link Oauth2Authorization} object containing updated authorization details
   */
  public Oauth2Authorization validateAccessTokenExpiryTimeOrRefreshToken(final Oauth2ServiceType oauth2ServiceType, final RegisteredUser user) {
    return baseOauth2Service.validateAccessTokenExpiryTimeOrRefreshToken(oauth2ServiceType, user.getId());
  }

}
