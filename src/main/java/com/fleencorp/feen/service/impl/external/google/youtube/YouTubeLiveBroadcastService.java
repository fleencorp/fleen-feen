package com.fleencorp.feen.service.impl.external.google.youtube;

import com.fleencorp.base.exception.externalsystem.ExternalSystemException;
import com.fleencorp.feen.aspect.MeasureExecutionTime;
import com.fleencorp.feen.constant.external.ExternalSystemType;
import com.fleencorp.feen.constant.external.google.youtube.YouTubeVideoPart;
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
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.fleencorp.feen.constant.base.ReportMessageType.YOUTUBE;
import static com.fleencorp.feen.constant.base.SimpleConstant.COMMA;
import static com.fleencorp.feen.constant.external.google.youtube.YouTubeVideoPart.CDN;
import static com.fleencorp.feen.constant.external.google.youtube.YouTubeVideoPart.STATUS;
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
      final LiveBroadcastSnippet snippet = setLiveBroadcastSnippet(createLiveBroadcastRequest);
      liveBroadcast.setSnippet(snippet);
      setBroadcastDetails(createLiveBroadcastRequest, liveBroadcast, snippet);

      // Insert the broadcast using the YouTube Data API
      final LiveBroadcast createdLiveBroadcast = youTube
              .liveBroadcasts()
              .insert(getPartsForCreatingLiveBroadcast(), liveBroadcast)
              .execute();
      log.info("Created Broadcast {}", createdLiveBroadcast);

      // Create and bind the associated live stream to the created broadcast
      createAssociatedLiveBroadcastStream(createLiveBroadcastRequest, youTube, createdLiveBroadcast);
      // Update the broadcast with category and other details
      setAndUpdateLiveBroadcastCategoryAndOtherDetails(createLiveBroadcastRequest, youTube, createdLiveBroadcast);

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
   * Creates and sets up a {@link LiveBroadcastSnippet} using the details from the given {@link CreateLiveBroadcastRequest}.
   *
   * @param createLiveBroadcastRequest the request object containing details for creating the live broadcast snippet
   * @return a {@link LiveBroadcastSnippet} populated with the title, description, scheduled start and end times,
   *         and channel ID from the {@code createLiveBroadcastRequest}, or {@code null} if the request is {@code null}
   */
  private LiveBroadcastSnippet setLiveBroadcastSnippet(CreateLiveBroadcastRequest createLiveBroadcastRequest) {
    if (nonNull(createLiveBroadcastRequest)) {
      final LiveBroadcastSnippet snippet = new LiveBroadcastSnippet();

      snippet.setTitle(createLiveBroadcastRequest.getTitle());
      snippet.setDescription(createLiveBroadcastRequest.getDescription());
      snippet.setChannelId(createLiveBroadcastRequest.getChannelId());
      // Set the new scheduled start and end times
      setLiveBroadcastScheduleDetails(snippet, createLiveBroadcastRequest.getScheduledStartDateTime(), createLiveBroadcastRequest.getScheduledEndDateTime());
      return snippet;
    }
    return null;
  }

  /**
   * Sets the details of a live broadcast, including the thumbnail, content details, and status,
   * using the provided {@link CreateLiveBroadcastRequest}, {@link LiveBroadcast}, and {@link LiveBroadcastSnippet}.
   *
   * @param createLiveBroadcastRequest the request containing the details for the live broadcast
   * @param liveBroadcast the {@link LiveBroadcast} object to be updated with content details and status
   * @param snippet the {@link LiveBroadcastSnippet} object to be updated with thumbnail details
   */
  private void setBroadcastDetails(CreateLiveBroadcastRequest createLiveBroadcastRequest, LiveBroadcast liveBroadcast, LiveBroadcastSnippet snippet) {
    if (nonNull(liveBroadcast)) {
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

      // Set the status for the broadcast
      final LiveBroadcastStatus liveBroadcastStatus = new LiveBroadcastStatus();
      liveBroadcastStatus.setPrivacyStatus(createLiveBroadcastRequest.getPrivacyStatus().getValue());
      liveBroadcastStatus.setSelfDeclaredMadeForKids(createLiveBroadcastRequest.getMadeForKids());
      liveBroadcast.setStatus(liveBroadcastStatus);
    }
  }

  /**
   * Creates a {@link LiveStream} using the details from the given {@link CreateLiveBroadcastRequest} and
   * the provided {@link LiveStreamSnippet}.
   *
   * @param createLiveBroadcastRequest the request containing the configuration for the live stream
   * @param liveStreamSnippet the snippet providing metadata for the live stream, such as title and description
   * @return a {@link LiveStream} configured with the provided snippet, CDN settings, and kind,
   *         or {@code null} if either the request or snippet is {@code null}
   */
  private LiveStream createLiveStream(CreateLiveBroadcastRequest createLiveBroadcastRequest, LiveStreamSnippet liveStreamSnippet) {
    if (nonNull(createLiveBroadcastRequest) && nonNull(liveStreamSnippet)) {
      // Create and configure CDN settings for the live stream
      CdnSettings cdnSettings = new CdnSettings();
      cdnSettings.setFormat(createLiveBroadcastRequest.getLiveStreamFormat());
      cdnSettings.setIngestionType(createLiveBroadcastRequest.getIngestionType());
      cdnSettings.setResolution(createLiveBroadcastRequest.getLiveStreamResolution());
      cdnSettings.setFrameRate(createLiveBroadcastRequest.getLiveStreamFrameRate());

      // Create the live stream and set its snippet and CDN settings
      LiveStream stream = new LiveStream();
      stream.setSnippet(liveStreamSnippet);
      stream.setCdn(cdnSettings);
      stream.setKind(createLiveBroadcastRequest.getStreamKind());
      return stream;
    }
    // Return null if the request or snippet is null
    return null;
  }

  /**
   * Creates an associated {@link LiveBroadcast} stream on YouTube using the details from the given {@link CreateLiveBroadcastRequest}.
   * This method creates a {@link LiveStream} and binds it to the provided {@link LiveBroadcast}.
   *
   * @param createLiveBroadcastRequest the request containing the configuration for creating the live broadcast
   * @param youTube the YouTube service instance used to interact with the YouTube API
   * @param createdLiveBroadcast the existing {@link LiveBroadcast} to which the new live stream will be associated
   * @return a {@link LiveStream} the newly created live stream binding to the Broadcast, or {@code null} if the request is {@code null}
   *         or the live stream could not be created
   * @throws IOException if an I/O error occurs when interacting with the YouTube API
   */
  private LiveStream createAssociatedLiveBroadcastStream(CreateLiveBroadcastRequest createLiveBroadcastRequest, YouTube youTube, LiveBroadcast createdLiveBroadcast) throws IOException {
    if (nonNull(createLiveBroadcastRequest)) {
      // Create a snippet for the live stream using the title from the request
      final LiveStreamSnippet liveStreamSnippet = new LiveStreamSnippet();
      liveStreamSnippet.setTitle(createLiveBroadcastRequest.getTitle());

      // Create the live stream using the snippet and the request details
      LiveStream stream = createLiveStream(createLiveBroadcastRequest, liveStreamSnippet);
      if (nonNull(stream)) {
        // Insert the live stream into YouTube and execute the request
        LiveStream createdStream = youTube.liveStreams()
          .insert(getPartsForCreatingLiveStream(), stream)
          .execute();
        log.info("Created Stream {}", createdStream);

        // Bind the created live stream to the provided live broadcast and execute the binding
        youTube.liveBroadcasts()
          .bind(createdLiveBroadcast.getId(), getPartsForBindingLiveStreamWithBroadcast())
          .setStreamId(createdStream.getId())
          .execute();
      }
    }
    // Return null if the request is null or if the stream could not be created
    return null;
  }

  /**
   * Sets and updates the category and other details for the provided {@link LiveBroadcast} on YouTube.
   * This method creates a {@link Video} resource associated with the live broadcast, assigns a category,
   * and updates the live broadcast on YouTube.
   *
   * @param createLiveBroadcastRequest the request containing the configuration for creating the live broadcast, including the category ID
   * @param youTube the YouTube service instance used to interact with the YouTube API
   * @param createdLiveBroadcast the existing {@link LiveBroadcast} that will be updated with new details
   * @throws IOException if an I/O error occurs when interacting with the YouTube API
   */
  private void setAndUpdateLiveBroadcastCategoryAndOtherDetails(CreateLiveBroadcastRequest createLiveBroadcastRequest, YouTube youTube, LiveBroadcast createdLiveBroadcast) throws IOException {
    // Create a Video object and set its ID to match the live broadcast's ID
    Video video = new Video();
    video.setId(createdLiveBroadcast.getId());

    // Create a VideoSnippet object and set the category ID from the request
    final VideoSnippet videoSnippet = new VideoSnippet();
    videoSnippet.setTitle(createLiveBroadcastRequest.getTitle());
    videoSnippet.setDescription(createLiveBroadcastRequest.getDescription());
    videoSnippet.setCategoryId(createLiveBroadcastRequest.getCategoryId());
    video.setSnippet(videoSnippet);

    // Update the live broadcast on YouTube with the new category and details
    video = youTube.videos()
      .update(getPartsForUpdatingBroadcastSnippet(), video)
      .execute();
    log.info("Created Video {}", video);
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
          .list(YouTubeVideoPart.getSnippet())
          .setId(updateLiveBroadcastRequest.getBroadcastId());

      // Check if the channel and live broadcasts are found and not empty
      if (nonNull(channel) && nonNull(liveBroadcasts) && !liveBroadcasts.isEmpty()) {
        final LiveBroadcast liveBroadcast = liveBroadcasts.execute().getItems().getFirst();
        final LiveBroadcastSnippet snippet = liveBroadcast.getSnippet();

        snippet.setTitle(updateLiveBroadcastRequest.getTitle());
        snippet.setDescription(updateLiveBroadcastRequest.getDescription());

        final LiveBroadcast updatedLiveBroadcast = createUpdateRequest(channel.getId(), liveBroadcast, youTube)
          .execute();

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
          .list(YouTubeVideoPart.getSnippet())
          .setId(rescheduleLiveBroadcastRequest.getBroadcastId());

      // Check if the channel and live broadcasts are found and not empty
      if (nonNull(channel) && nonNull(liveBroadcasts) && !liveBroadcasts.isEmpty()) {
        // Execute the live broadcasts request and get the first item
        final LiveBroadcast liveBroadcast = liveBroadcasts.execute().getItems().getFirst();
        final LiveBroadcastSnippet snippet = liveBroadcast.getSnippet();

        // Set the new scheduled start and end times
        setLiveBroadcastScheduleDetails(snippet, rescheduleLiveBroadcastRequest.getScheduledStartDateTime(), rescheduleLiveBroadcastRequest.getScheduledEndDateTime());
        // Create the update request for the broadcast
        final LiveBroadcast updatedLiveBroadcast = createUpdateRequest(channel.getId(), liveBroadcast, youTube)
          .execute();

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
          .update(YouTubeVideoPart.getSnippet(), updateBroadcast);
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
      // Execute the request and obtain the response
      final ChannelListResponse channelListResponse = getChannels(youTube);
      final List<Channel> channels = Optional.of(channelListResponse.getItems()).orElse(emptyList());

      if (!channels.isEmpty()) {
        // Return the first channel in the list if the channel list is not empty
        return channels.getFirst();
      }
    } catch (final Exception ex) {
      // Log the error and throw an exception if accessing YouTube API fails
      final String errorMessage = String.format("Error occurred while retrieving YouTube channel. Reason: %s", ex.getMessage());
      reporterService.sendMessage(errorMessage, YOUTUBE);

      throw new ExternalSystemException(ExternalSystemType.YOUTUBE.getValue());
    }
    // Return null if no channel was found
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
    final List<String> parts = List.of(YouTubeVideoPart.getSnippet(), STATUS.getValue(), YouTubeVideoPart.getContentDetails());
    return joinParts(parts);
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
    final List<String> parts = List.of(YouTubeVideoPart.getSnippet(), CDN.getValue());
    return joinParts(parts);
  }

  /**
   * Constructs a comma-separated string of parts required for binding a live stream with a broadcast.
   *
   * @return a comma-separated string of parts, including the ID and content details
   */
  private String getPartsForBindingLiveStreamWithBroadcast() {
    final List<String> parts = List.of(YouTubeVideoPart.getId(), YouTubeVideoPart.getContentDetails());
    return joinParts(parts);
  }

  /**
   * Constructs a comma-separated string of parts required for updating a broadcast snippet.
   *
   * @return a comma-separated string containing the snippet part
   */
  private String getPartsForUpdatingBroadcastSnippet() {
    final List<String> parts = List.of(YouTubeVideoPart.getSnippet());
    return joinParts(parts);
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

  /**
   * Joins the elements of the provided list into a single string, using a comma separator.
   *
   * @param parts the list of string elements to be joined
   * @return a single string with all elements of the list joined by commas
   */
  protected String joinParts(List<String> parts) {
    // Joins the list elements using a comma as the separator
    return String.join(COMMA, parts);
  }

  /**
   * Retrieves the list of YouTube channels associated with the authenticated user.
   *
   * @param youTube the YouTube service object used to access the YouTube Data API
   * @return a {@code ChannelListResponse} containing the list of channels
   * @throws ExternalSystemException if there is an error accessing the YouTube API
   */
  protected ChannelListResponse getChannels(final YouTube youTube) {
    try {
      // Create a request to list channels using the 'snippet' part
      final Channels.List channelRequest = youTube
        .channels()
        .list(YouTubeVideoPart.getSnippet());
      channelRequest.setMine(true); // Set to retrieve channels associated with the authenticated user
      channelRequest.setKey(serviceApiKey); // Set the API key for authentication

      return channelRequest.execute(); // Execute the request and return the response
    } catch (final Exception ex) {
      final String errorMessage = String.format("Error occurred while retrieving YouTube channels. Reason: %s", ex.getMessage());
      reporterService.sendMessage(errorMessage, YOUTUBE);

      throw new ExternalSystemException(ExternalSystemType.YOUTUBE.getValue());
    }
  }

  /**
   * Sets the scheduled start and end times for the provided {@link LiveBroadcastSnippet}.
   *
   * @param snippet the {@link LiveBroadcastSnippet} object to be updated with schedule details
   * @param startTime the scheduled start time for the live broadcast
   * @param endTime the scheduled end time for the live broadcast
   */
  protected void setLiveBroadcastScheduleDetails(final LiveBroadcastSnippet snippet, final LocalDateTime startTime, final LocalDateTime endTime) {
    if (nonNull(snippet)) {
      // Convert and set the scheduled start time for the snippet
      snippet.setScheduledStartTime(toDateTime(startTime));
      // Convert and set the scheduled end time for the snippet
      snippet.setScheduledEndTime(toDateTime(endTime));
    }
  }
}
