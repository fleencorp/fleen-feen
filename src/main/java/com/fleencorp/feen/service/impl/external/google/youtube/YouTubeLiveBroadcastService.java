package com.fleencorp.feen.service.impl.external.google.youtube;

import com.fleencorp.base.exception.externalsystem.ExternalSystemException;
import com.fleencorp.feen.aspect.MeasureExecutionTime;
import com.fleencorp.feen.constant.external.ExternalSystemType;
import com.fleencorp.feen.exception.stream.UnableToCompleteOperationException;
import com.fleencorp.feen.model.request.youtube.broadcast.CreateLiveBroadcastRequest;
import com.fleencorp.feen.model.request.youtube.broadcast.RescheduleLiveBroadcastRequest;
import com.fleencorp.feen.model.request.youtube.broadcast.UpdateLiveBroadcastRequest;
import com.fleencorp.feen.model.response.external.google.youtube.CreateYouTubeLiveBroadcastResponse;
import com.fleencorp.feen.model.response.external.google.youtube.RescheduleYouTubeLiveBroadcastResponse;
import com.fleencorp.feen.model.response.external.google.youtube.UpdateYouTubeLiveBroadcastResponse;
import com.fleencorp.feen.service.impl.external.google.oauth2.GoogleOauth2Service;
import com.fleencorp.feen.service.report.ReporterService;
import com.fleencorp.feen.util.external.google.GoogleApiUtil;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.YouTube.Builder;
import com.google.api.services.youtube.YouTube.Channels;
import com.google.api.services.youtube.model.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static com.fleencorp.feen.constant.base.ReportMessageType.YOUTUBE;
import static com.fleencorp.feen.constant.base.SimpleConstant.COMMA;
import static com.fleencorp.feen.constant.external.google.youtube.YouTubeVideoPart.*;
import static com.fleencorp.feen.mapper.external.YouTubeLiveBroadcastMapper.mapToLiveBroadcastResponse;
import static com.fleencorp.feen.util.external.google.GoogleApiUtil.*;
import static java.util.Collections.emptyList;
import static java.util.Objects.nonNull;

/**
 * Service class for managing YouTube Live Broadcasts.
 *
 * <p>This class provides functionalities to interact with YouTube Live Broadcasts,
 * enabling operations such as creating, updating, and managing live broadcast events
 * on the YouTube platform. It utilizes the YouTube Data API to perform these operations.</p>
 *
 * <p>The methods in this service class will facilitate integration with YouTube Live,
 * allowing applications to handle live streaming events programmatically.</p>
 *
 * <p>Note: This class requires proper authentication and authorization setup to interact
 * with the YouTube Data API.</p>
 *
 * <p>Dependencies and setup for this service should be properly configured to ensure
 * seamless integration with the YouTube platform.</p>
 *
 * @author Yusuf Alamu Musa
 * @version 1.0
 *
 * @see <a href="https://developers.google.com/youtube/v3/live/docs/liveBroadcasts">
 *   LiveBroadcasts</a>
 * @see <a href="https://developers.google.com/youtube/v3/guides/authentication">
 *   Implementing OAuth 2.0 Authorization</a>
 * @see <a href="https://developers.google.com/youtube/v3/guides/auth/server-side-web-apps">
 *   Using OAuth 2.0 for Web Server Applications</a>
 */
@Service
@Slf4j
public class YouTubeLiveBroadcastService {

  private final String applicationName;
  private final String serviceApiKey;
  private final ReporterService reporterService;

  /**
   * Constructs a YouTubeLiveBroadcastService with the specified API key.
   *
   * <p>This constructor initializes the YouTubeLiveBroadcastService with the provided
   * YouTube Data API key. The API key is injected using the {@link Value} annotation,
   * which retrieves the value from the application properties configured under the key
   * "youtube.data.api-key". This API key is essential for authenticating requests
   * to the YouTube Data API.</p>
   *
   * <p>The API key should be properly configured in the application properties to ensure
   * that the service can successfully interact with the YouTube Data API.</p>
   *
   * @param applicationName the application name to be used in the YouTube service
   * @param serviceApiKey The YouTube Data API key used for authenticating API requests.
   * @param reporterService The service used for reporting events.
   */
  public YouTubeLiveBroadcastService(
      @Value("${application.name}") final String applicationName,
      @Value("${youtube.data.api-key}") final String serviceApiKey,
      final ReporterService reporterService) {
    this.applicationName = applicationName;
    this.serviceApiKey = serviceApiKey;
    this.reporterService = reporterService;
  }

  /**
   * Creates a live broadcast on YouTube for the specified channel.
   *
   * <p>This method initializes a {@link YouTube} request object using the provided access token
   * from the {@link CreateLiveBroadcastRequest} object. It then retrieves the YouTube channel
   * associated with the authenticated user using the {@link #getChannel(YouTube)} method.</p>
   *
   * <p>If the channel is successfully retrieved, the method sets the channel ID in the
   * {@link CreateLiveBroadcastRequest} object and proceeds to create the live broadcast by calling
   * the {@link #createLiveBroadcast(CreateLiveBroadcastRequest, YouTube)} method.</p>
   *
   * <p>Note: This method throws an {@link IOException} if there is an issue with the YouTube API request.</p>
   *
   * @param createLiveBroadcastRequest The request object containing details for creating the live broadcast.
   * @return {@link CreateYouTubeLiveBroadcastResponse} containing the broadcast and live-streaming details
   * @throws UnableToCompleteOperationException if the operation cannot be completed
   */
  @MeasureExecutionTime
  public CreateYouTubeLiveBroadcastResponse createBroadcast(final CreateLiveBroadcastRequest createLiveBroadcastRequest) {
    final YouTube youTube = createRequest(createLiveBroadcastRequest.getAccessTokenForHttpRequest());
    final Channel channel = getChannel(youTube);

    if (nonNull(channel)) {
      createLiveBroadcastRequest.setChannelId(channel.getId());
      return createLiveBroadcast(createLiveBroadcastRequest, youTube);
    }
    log.error("No channel found associated with user");
    throw new UnableToCompleteOperationException();
  }

  /**
   * Creates a live broadcast on YouTube using the specified request details.
   *
   * <p>This method constructs a {@link LiveBroadcast} object using the details provided
   * in the {@link CreateLiveBroadcastRequest} object. It sets the snippet, content details,
   * status, and thumbnail of the live broadcast.</p>
   *
   * <p>The method initializes and configures the necessary components of the live broadcast,
   * including the title, description, scheduled start and end times, channel ID, thumbnail URL,
   * closed captions type, and privacy status. It then inserts the live broadcast using the
   * YouTube Data API.</p>
   *
   * <p>Upon successful creation, a {@link CreateYouTubeLiveBroadcastResponse} is built with the
   * live broadcast ID and mapped response details. If an IOException occurs during the API request,
   * the error is logged, and the method returns null.</p>
   *
   * @param createLiveBroadcastRequest The request object containing details for creating the live broadcast.
   * @param youTube The {@link YouTube} object used to make the API request.
   * @return A {@link CreateYouTubeLiveBroadcastResponse} object containing the details of the created live broadcast,
   * or null if an error occurs.
   * @throws UnableToCompleteOperationException if the operation cannot be completed
   *
   * @see <a href="https://developers.google.com/youtube/v3/live/docs/liveBroadcasts/insert">
   *   LiveBroadcasts: insert</a>
   */
  @MeasureExecutionTime
  public CreateYouTubeLiveBroadcastResponse createLiveBroadcast(final CreateLiveBroadcastRequest createLiveBroadcastRequest, final YouTube youTube) {
    try {
      final LiveBroadcast liveBroadcast = new LiveBroadcast();

      // Set the snippet details for the broadcast
      final LiveBroadcastSnippet snippet = new LiveBroadcastSnippet();
      snippet.setTitle(createLiveBroadcastRequest.getTitle());
      snippet.setDescription(createLiveBroadcastRequest.getDescription());
      snippet.setScheduledStartTime(toDateTime(createLiveBroadcastRequest.getScheduledStartDateTime()));
      snippet.setScheduledEndTime(toDateTime(createLiveBroadcastRequest.getScheduledEndDateTime()));
      snippet.setChannelId(createLiveBroadcastRequest.getChannelId());
      snippet.set("broadcastSource", "BROADCAST_SOURCE_WEBCAM");
//      snippet.set("broadcastType", "BROADCAST_SCHEDULED");
      snippet.set("enableMonitorStream", false);
      snippet.set("useMasks", true);
      liveBroadcast.setSnippet(snippet);
      liveBroadcast.set("broadcastSource", "BROADCAST_SOURCE_WEBCAM");
//      liveBroadcast.set("broadcastType", "BROADCAST_SCHEDULED");
      liveBroadcast.set("enableMonitorStream", false);
      liveBroadcast.set("useMasks", true);



      // Set the thumbnail details for the broadcast
      final ThumbnailDetails thumbnailDetails = new ThumbnailDetails();
      final Thumbnail thumbnail = new Thumbnail();
      thumbnail.setUrl(createLiveBroadcastRequest.getThumbnailUrl()); // Set the thumbnail URL
      thumbnailDetails.setDefault(thumbnail);
      snippet.setThumbnails(thumbnailDetails);

      // Set the content details for the broadcast
      final LiveBroadcastContentDetails liveBroadcastContentDetails = new LiveBroadcastContentDetails();
      liveBroadcastContentDetails.setClosedCaptionsType(createLiveBroadcastRequest.getClosedCaptionsType().getValue());
      liveBroadcast.setContentDetails(liveBroadcastContentDetails);
      liveBroadcastContentDetails.setMonitorStream(null);
      liveBroadcastContentDetails.set("broadcastSource", "BROADCAST_SOURCE_WEBCAM");
//      liveBroadcastContentDetails.set("broadcastType", "BROADCAST_SCHEDULED");
      liveBroadcastContentDetails.set("enableMonitorStream", false);
      liveBroadcastContentDetails.set("useMasks", true);

      // Set the status for the broadcast
      final LiveBroadcastStatus liveBroadcastStatus = new LiveBroadcastStatus();
      liveBroadcastStatus.setPrivacyStatus(createLiveBroadcastRequest.getPrivacyStatus().getValue());
      liveBroadcastStatus.setSelfDeclaredMadeForKids(createLiveBroadcastRequest.getMadeForKids());
      liveBroadcast.setStatus(liveBroadcastStatus);

      // Insert the broadcast using the YouTube Data API
      final LiveBroadcast createdLiveBroadcast = youTube
              .liveBroadcasts()
              .insert(getPartsForCreatingLiveBroadcast(), liveBroadcast)
              .execute();

      log.info("Created Broadcast {}", createdLiveBroadcast);

      final LiveStreamSnippet liveStreamSnippet = new LiveStreamSnippet();
      liveStreamSnippet.setTitle(createLiveBroadcastRequest.getTitle());

      CdnSettings cdnSettings = new CdnSettings();
      cdnSettings.setFormat(createLiveBroadcastRequest.getLiveStreamFormat());
      cdnSettings.setIngestionType(createLiveBroadcastRequest.getIngestionType());
      cdnSettings.setResolution(createLiveBroadcastRequest.getLiveStreamResolution());
      cdnSettings.setFrameRate(createLiveBroadcastRequest.getLiveStreamFrameRate());

      LiveStream stream = new LiveStream();
      stream.setSnippet(liveStreamSnippet);
      stream.setCdn(cdnSettings);
      stream.setKind(createLiveBroadcastRequest.getStreamKind());

      LiveStream createdStream = youTube.liveStreams()
        .insert(getPartsForCreatingLiveStream(), stream)
        .execute();

      LiveBroadcast updatedBroadcast = youTube.liveBroadcasts()
        .bind(createdLiveBroadcast.getId(), getPartsForBindingLiveStreamWithBroadcast())
        .setStreamId(createdStream.getId())
        .execute();

      log.info("Updated broadcast {}", updatedBroadcast);

      final Video video = new Video();
      video.setId(createdLiveBroadcast.getId());

      // Add a category to the live broadcast and stream
      final VideoSnippet videoSnippet = new VideoSnippet();
      videoSnippet.setTitle(createLiveBroadcastRequest.getTitle());
      videoSnippet.setDescription(createLiveBroadcastRequest.getDescription());
      videoSnippet.setCategoryId(createLiveBroadcastRequest.getCategoryId());
      video.setSnippet(videoSnippet);

      // Update the broadcast and live stream with a category
      youTube.videos()
          .update(createdLiveBroadcast.getId(), video)
          .execute();

      // Build and return the response with the created broadcast details
      return CreateYouTubeLiveBroadcastResponse
          .of(createdLiveBroadcast.getId(),
              getYouTubeLiveStreamingLinkByBroadcastId(createdLiveBroadcast.getId()),
              mapToLiveBroadcastResponse(createdLiveBroadcast));
    } catch (final IOException ex) {
      final String errorMessage = String.format("Error occurred while creating live broadcast. Reason: %s", ex.getMessage());
      reporterService.sendMessage(errorMessage, YOUTUBE);
    }
    throw new UnableToCompleteOperationException();
  }

  /**
   * Updates a live broadcast on YouTube with the specified details.
   *
   * <p>This method retrieves the YouTube {@link Channel} and {@link LiveBroadcast} using
   * the access token and broadcaster ID provided in the {@link UpdateLiveBroadcastRequest}.
   * It updates the broadcast's {@link LiveBroadcastSnippet} with the new title and description.</p>
   *
   * <p>If the channel and live broadcast are found and successfully updated, the updated broadcast
   * details are returned encapsulated in a {@link UpdateYouTubeLiveBroadcastResponse} object,
   * including the broadcast ID, live stream link, and mapped response details.</p>
   *
   * <p>If any IOException occurs during the update operation, it is logged, and an
   * {@link UnableToCompleteOperationException} is thrown.</p>
   *
   * @param updateLiveBroadcastRequest The request object containing details for updating the live broadcast.
   * @return An {@link UpdateYouTubeLiveBroadcastResponse} object containing the updated live broadcast details.
   * @throws UnableToCompleteOperationException if the operation cannot be completed
   */
  @MeasureExecutionTime
  public UpdateYouTubeLiveBroadcastResponse updateLiveBroadcast(final UpdateLiveBroadcastRequest updateLiveBroadcastRequest) {
    try {
      // Create a YouTube request using the provided access token
      final YouTube youTube = createRequest(updateLiveBroadcastRequest.getAccessTokenForHttpRequest());
      final Channel channel = getChannel(youTube);

      // List live broadcasts based on the broadcast ID
      final YouTube.LiveBroadcasts.List liveBroadcasts = youTube.liveBroadcasts()
          .list(SNIPPET.getValue())
          .setId(updateLiveBroadcastRequest.getBroadcastId());

      // Check if the channel and live broadcasts are found and not empty
      if (nonNull(channel) && nonNull(liveBroadcasts) && !liveBroadcasts.isEmpty()) {
        final LiveBroadcast liveBroadcast = liveBroadcasts.execute().getItems().getFirst();
        final LiveBroadcastSnippet snippet = liveBroadcast.getSnippet();

        snippet.setTitle(updateLiveBroadcastRequest.getTitle());
        snippet.setDescription(updateLiveBroadcastRequest.getDescription());

        final YouTube.LiveBroadcasts.Update updateRequest = createUpdateRequest(channel.getId(), liveBroadcast, youTube);
        final LiveBroadcast updatedLiveBroadcast = updateRequest.execute();

        return UpdateYouTubeLiveBroadcastResponse
          .of(liveBroadcast.getId(),
            getYouTubeLiveStreamingLinkByBroadcastId(liveBroadcast.getId()),
            mapToLiveBroadcastResponse(updatedLiveBroadcast));
      }
      log.error("Cannot update live broadcast. Channel or broadcast cannot be found");
    } catch (final IOException ex) {
      final String errorMessage = String.format("Error occurred while updating live broadcast. Reason: %s", ex.getMessage());
      reporterService.sendMessage(errorMessage, YOUTUBE);
    }
    throw new UnableToCompleteOperationException();
  }

  /**
   * Reschedules a live broadcast on YouTube using the specified request details.
   *
   * <p>This method creates a YouTube request with the provided access token, retrieves
   * the channel, and lists live broadcasts based on the broadcast ID. If the channel and
   * live broadcasts are found, it updates the scheduled start and end times of the broadcast
   * and executes the update request.</p>
   *
   * <p>Upon successful rescheduling, a {@link RescheduleYouTubeLiveBroadcastResponse} is built with the
   * live broadcast ID, live stream link, and mapped response details. If an IOException occurs
   * during the API request, the error is logged, and the method throws an {@link UnableToCompleteOperationException}.</p>
   *
   * @param rescheduleLiveBroadcastRequest The request object containing details for rescheduling the live broadcast.
   * @return A {@link RescheduleYouTubeLiveBroadcastResponse} object containing the details of the rescheduled live broadcast.
   * @throws UnableToCompleteOperationException if the operation cannot be completed.
   */
  @MeasureExecutionTime
  public RescheduleYouTubeLiveBroadcastResponse rescheduleLiveBroadcast(final RescheduleLiveBroadcastRequest rescheduleLiveBroadcastRequest) {
    try {
      // Create a YouTube request using the provided access token
      final YouTube youTube = createRequest(rescheduleLiveBroadcastRequest.getAccessTokenForHttpRequest());
      final Channel channel = getChannel(youTube);

      // List live broadcasts based on the broadcast ID
      final YouTube.LiveBroadcasts.List liveBroadcasts = youTube.liveBroadcasts()
          .list(SNIPPET.getValue())
          .setId(rescheduleLiveBroadcastRequest.getBroadcastId());

      // Check if the channel and live broadcasts are found and not empty
      if (nonNull(channel) && nonNull(liveBroadcasts) && !liveBroadcasts.isEmpty()) {
        // Execute the live broadcasts request and get the first item
        final LiveBroadcast liveBroadcast = liveBroadcasts.execute().getItems().getFirst();
        final LiveBroadcastSnippet snippet = liveBroadcast.getSnippet();

        // Set the new scheduled start and end times
        snippet.setScheduledStartTime(toDateTime(rescheduleLiveBroadcastRequest.getScheduledStartDateTime()));
        snippet.setScheduledEndTime(toDateTime(rescheduleLiveBroadcastRequest.getScheduledEndDateTime()));

        // Create the update request for the broadcast
        final YouTube.LiveBroadcasts.Update updateRequest = createUpdateRequest(channel.getId(), liveBroadcast, youTube);
        final LiveBroadcast updatedLiveBroadcast = updateRequest.execute();

        // Build and return the response with updated live broadcast details
        return RescheduleYouTubeLiveBroadcastResponse
          .of(updatedLiveBroadcast.getId(),
            getYouTubeLiveStreamingLinkByBroadcastId(updatedLiveBroadcast.getId()),
            mapToLiveBroadcastResponse(updatedLiveBroadcast));
      }
      log.error("Cannot reschedule live broadcast. Channel or broadcast cannot be found");
    } catch (final IOException ex) {
      final String errorMessage = String.format("Error occurred while rescheduling live broadcast. Reason: %s", ex.getMessage());
      reporterService.sendMessage(errorMessage, YOUTUBE);
    }
    throw new UnableToCompleteOperationException();
  }

  /**
   * Creates an update request for a YouTube live broadcast.
   *
   * <p>This method sets the channel ID on the provided {@link LiveBroadcast} object's
   * {@link LiveBroadcastSnippet}, constructs a new {@link LiveBroadcast} object with
   * the updated snippet, and returns an update request using the YouTube Data API.</p>
   *
   * <p>If an {@link IOException} occurs during the creation of the update request,
   * it is logged, and an {@link UnableToCompleteOperationException} is thrown.</p>
   *
   * @param channelId The ID of the YouTube channel.
   * @param liveBroadcast The {@link LiveBroadcast} object containing the broadcast details to be updated.
   * @param youTube The {@link YouTube} object used to make the API request.
   * @return A {@link YouTube.LiveBroadcasts.Update} object representing the update request.
   * @throws UnableToCompleteOperationException if the operation cannot be completed
   */
  public YouTube.LiveBroadcasts.Update createUpdateRequest(final String channelId, final LiveBroadcast liveBroadcast, final YouTube youTube) {
    try {
      // Get the snippet from the live broadcast
      final LiveBroadcastSnippet snippet = liveBroadcast.getSnippet();
      snippet.setChannelId(channelId);

      // Create a new live broadcast object for the update
      final LiveBroadcast updateBroadcast = new LiveBroadcast();
      // Set the ID and snippet for the update broadcast
      updateBroadcast.setSnippet(snippet);

      // Return update request
      return youTube.liveBroadcasts()
          .update(SNIPPET.getValue(), updateBroadcast);
    } catch (final IOException ex) {
      final String errorMessage = String.format("Error occurred while live broadcast update request. Reason: %s", ex.getMessage());
      reporterService.sendMessage(errorMessage, YOUTUBE);
    }
    throw new UnableToCompleteOperationException();
  }

  /**
   * Retrieves the YouTube channel associated with the authenticated user.
   *
   * <p>This method creates and executes a request to list channels using the 'snippet' part,
   * and sets the request to retrieve channels associated with the authenticated user. It uses
   * the provided {@link YouTube} object to make the request and includes the API key for
   * authentication.</p>
   *
   * <p>The method handles the response by checking if the list of channels is not empty,
   * and returns the first channel in the list. If no channels are found or if an error
   * occurs during the API request, it logs the error and throws an {@link ExternalSystemException}
   * with a message indicating the failure.</p>
   *
   * <p>Note: This method assumes that the {@link YouTube} object is properly authenticated
   * and configured with the necessary API credentials.</p>
   *
   * @param youTube The {@link YouTube} object used to make the API request.
   * @return The {@link Channel} object associated with the authenticated user, or null if no channels are found.
   *
   * @see <a href="https://www.getphyllo.com/post/how-to-get-youtube-api-key">
   *   How to Get YouTube API Key: A Detailed Guide for Developers</a>
   */
  @MeasureExecutionTime
  public Channel getChannel(final YouTube youTube) {
    try {
      // Create a request to list channels using the 'snippet' part
      final Channels.List channelRequest = youTube
              .channels()
              .list(SNIPPET.getValue());
      channelRequest.setMine(true); // Set to retrieve channels associated with the authenticated user
      channelRequest.setKey(serviceApiKey);

      // Execute the request and obtain the response
      final ChannelListResponse channelListResponse = channelRequest.execute();
      final List<Channel> channels = Optional.of(channelListResponse.getItems()).orElse(emptyList());

      if (!channels.isEmpty()) {
        return channels.getFirst();
      }
    } catch (final Exception ex) {
      // Log the error and throw an exception if accessing YouTube API fails
      final String errorMessage = String.format("Error occurred while retrieving YouTube channel. Reason: %s", ex.getMessage());
      reporterService.sendMessage(errorMessage, YOUTUBE);

      throw new ExternalSystemException(ExternalSystemType.YOUTUBE.getValue());
    }

    // Return the list of YouTubeChannelResponse objects
    return null;
  }

  /**
   * Constructs and returns a comma-separated string of parts required for creating a live broadcast.
   *
   * <p>This method creates a list of parts needed for creating a YouTube live broadcast,
   * including "snippet", "status", and "contentDetails". It then joins these parts into a
   * single comma-separated string using {@link String#join(CharSequence, CharSequence...)}.</p>
   *
   * <p>The resulting string can be used in API requests that require specifying the parts
   * of a YouTube resource to be included in the response.</p>
   *
   * @return A comma-separated string of parts for creating a live broadcast.
   */
  private String getPartsForCreatingLiveBroadcast() {
    final List<String> parts = List.of(SNIPPET.getValue(), STATUS.getValue(), CONTENT_DETAILS.getValue());
    return String.join(COMMA, parts);
  }

  /**
   * Constructs and returns a comma-separated string of parts required for creating a live stream.
   *
   * <p>This method creates a list of parts needed for creating a YouTube live broadcast,
   * including "snippet" and "cdn". It then joins these parts into a
   * single comma-separated string using {@link String#join(CharSequence, CharSequence...)}.</p>
   *
   * <p>The resulting string can be used in API requests that require specifying the parts
   * of a YouTube resource to be included in the response.</p>
   *
   * @return A comma-separated string of parts for creating a live broadcast.
   */
  private String getPartsForCreatingLiveStream() {
    final List<String> parts = List.of(SNIPPET.getValue(), CDN.getValue());
    return String.join(COMMA, parts);
  }

  /**
   * Constructs a comma-separated string of parts required for binding a live stream with a broadcast.
   *
   * @return a comma-separated string of parts, including the ID and content details
   */
  private String getPartsForBindingLiveStreamWithBroadcast() {
    final List<String> parts = List.of(ID.getValue(), CONTENT_DETAILS.getValue());
    return String.join(COMMA, parts);
  }


  /**
   * Creates and returns a YouTube request object configured with the provided access token.
   *
   * <p>This method constructs an instance of {@link YouTube} using the provided access token.
   * It uses a {@link Builder} to initialize the YouTube object with the necessary
   * transport, JSON factory, and HTTP request initializer. The HTTP request initializer is
   * obtained using the {@link GoogleApiUtil#getHttpRequestInitializer(String)} method, which configures
   * the HTTP request with the provided access token.</p>
   *
   * <p>The resulting YouTube object can be used to interact with the YouTube Data API,
   * enabling operations such as creating, updating, and managing YouTube resources.</p>
   *
   * @param accessToken The access token to be used for authenticating YouTube API requests.
   * @return A {@link YouTube} object configured with the provided access token.
   *
   * @see <a href="https://developers.google.com/youtube/registering_an_application">
   *   Obtaining authorization credentials</a>
   */
  private YouTube createRequest(final String accessToken) {
    return new YouTube.Builder(GoogleOauth2Service.getTransport(), GoogleOauth2Service.getJsonFactory(), getHttpRequestInitializer(accessToken))
            .setApplicationName(applicationName)
            .build();
  }
}
