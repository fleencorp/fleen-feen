package com.fleencorp.feen.service.external.google.youtube;

import com.fleencorp.feen.constant.external.ExternalSystemType;
import com.fleencorp.feen.exception.base.ExternalSystemException;
import com.fleencorp.feen.exception.stream.UnableToCompleteOperationException;
import com.fleencorp.feen.mapper.external.YouTubeLiveBroadcastMapper;
import com.fleencorp.feen.model.request.youtube.broadcast.CreateLiveBroadcastRequest;
import com.fleencorp.feen.model.request.youtube.broadcast.RescheduleLiveBroadcastRequest;
import com.fleencorp.feen.model.request.youtube.broadcast.UpdateLiveBroadcastRequest;
import com.fleencorp.feen.model.response.external.google.youtube.CreateYouTubeLiveBroadcastResponse;
import com.fleencorp.feen.model.response.external.google.youtube.RescheduleYouTubeLiveBroadcastResponse;
import com.fleencorp.feen.model.response.external.google.youtube.UpdateYouTubeLiveBroadcastResponse;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.json.JsonObjectParser;
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

import static com.fleencorp.feen.constant.base.SimpleConstant.COMMA;
import static com.fleencorp.feen.constant.external.google.youtube.YouTubeVideoPart.*;
import static com.fleencorp.feen.service.external.google.oauth2.GoogleOauth2Service.getJsonFactory;
import static com.fleencorp.feen.service.external.google.oauth2.GoogleOauth2Service.getTransport;
import static com.fleencorp.feen.util.external.google.GoogleApiUtil.getYouTubeLiveStreamingByBroadcastId;
import static com.fleencorp.feen.util.external.google.GoogleApiUtil.toDateTime;
import static com.google.auth.http.AuthHttpConstants.AUTHORIZATION;
import static com.google.auth.http.AuthHttpConstants.BEARER;
import static java.util.Collections.emptyList;
import static java.util.Objects.nonNull;

/**
 * Service class for managing YouTube Live Broadcasts.
 *
 * <p> This class provides functionalities to interact with YouTube Live Broadcasts,
 * enabling operations such as creating, updating, and managing live broadcast events
 * on the YouTube platform. It utilizes the YouTube Data API to perform these operations.</p>
 *
 * <p> The methods in this service class will facilitate integration with YouTube Live,
 * allowing applications to handle live streaming events programmatically.</p>
 *
 * <p> Note: This class requires proper authentication and authorization setup to interact
 * with the YouTube Data API.</p>
 *
 * <p> Dependencies and setup for this service should be properly configured to ensure
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

  private final String serviceApiKey;

  /**
   * Constructs a YouTubeLiveBroadcastService with the specified API key.
   *
   * <p> This constructor initializes the YouTubeLiveBroadcastService with the provided
   * YouTube Data API key. The API key is injected using the {@link Value} annotation,
   * which retrieves the value from the application properties configured under the key
   * "youtube.data.api-key". This API key is essential for authenticating requests
   * to the YouTube Data API.</p>
   *
   * <p> The API key should be properly configured in the application properties to ensure
   * that the service can successfully interact with the YouTube Data API.</p>
   *
   * @param serviceApiKey The YouTube Data API key used for authenticating API requests.
   */
  public YouTubeLiveBroadcastService(
      @Value("${youtube.data.api-key}") String serviceApiKey) {
    this.serviceApiKey = serviceApiKey;
  }

  /**
   * Creates a live broadcast on YouTube for the specified channel.
   *
   * <p> This method initializes a {@link YouTube} request object using the provided access token
   * from the {@link CreateLiveBroadcastRequest} object. It then retrieves the YouTube channel
   * associated with the authenticated user using the {@link #getChannel(YouTube)} method.</p>
   *
   * <p> If the channel is successfully retrieved, the method sets the channel ID in the
   * {@link CreateLiveBroadcastRequest} object and proceeds to create the live broadcast by calling
   * the {@link #createLiveBroadcast(CreateLiveBroadcastRequest, YouTube)} method.</p>
   *
   * <p> Note: This method throws an {@link IOException} if there is an issue with the YouTube API request.</p>
   *
   * @param createLiveBroadcastRequest The request object containing details for creating the live broadcast.
   * @return {@link CreateYouTubeLiveBroadcastResponse} containing the broadcast and live-streaming details
   * @throws UnableToCompleteOperationException if the operation cannot be completed
   */
  public CreateYouTubeLiveBroadcastResponse createBroadcast(CreateLiveBroadcastRequest createLiveBroadcastRequest) {
    YouTube youTube = createRequest(createLiveBroadcastRequest.getAccessTokenForHttpRequest());
    Channel channel = getChannel(youTube);

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
   * <p> This method constructs a {@link LiveBroadcast} object using the details provided
   * in the {@link CreateLiveBroadcastRequest} object. It sets the snippet, content details,
   * status, and thumbnail of the live broadcast.</p>
   *
   * <p> The method initializes and configures the necessary components of the live broadcast,
   * including the title, description, scheduled start and end times, channel ID, thumbnail URL,
   * closed captions type, and privacy status. It then inserts the live broadcast using the
   * YouTube Data API.</p>
   *
   * <p> Upon successful creation, a {@link CreateYouTubeLiveBroadcastResponse} is built with the
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
  public CreateYouTubeLiveBroadcastResponse createLiveBroadcast(CreateLiveBroadcastRequest createLiveBroadcastRequest, YouTube youTube) {
    try {
      LiveBroadcast liveBroadcast = new LiveBroadcast();

      // Set the snippet details for the broadcast
      LiveBroadcastSnippet snippet = new LiveBroadcastSnippet();
      snippet.setTitle(createLiveBroadcastRequest.getTitle());
      snippet.setDescription(createLiveBroadcastRequest.getDescription());
      snippet.setScheduledStartTime(toDateTime(createLiveBroadcastRequest.getScheduledStartDateTime()));
      snippet.setScheduledEndTime(toDateTime(createLiveBroadcastRequest.getScheduledEndDateTime()));
      snippet.setChannelId(createLiveBroadcastRequest.getChannelId());
      liveBroadcast.setSnippet(snippet);

      // Set the thumbnail details for the broadcast
      ThumbnailDetails thumbnailDetails = new ThumbnailDetails();
      Thumbnail thumbnail = new Thumbnail();
      thumbnail.setUrl(createLiveBroadcastRequest.getThumbnailUrl()); // Set the thumbnail URL
      thumbnailDetails.setDefault(thumbnail);
      snippet.setThumbnails(thumbnailDetails);

      // Set the content details for the broadcast
      LiveBroadcastContentDetails liveBroadcastContentDetails = new LiveBroadcastContentDetails();
      liveBroadcastContentDetails.setClosedCaptionsType(createLiveBroadcastRequest.getClosedCaptionsType().getValue());
      liveBroadcast.setContentDetails(liveBroadcastContentDetails);

      // Set the status for the broadcast
      LiveBroadcastStatus liveBroadcastStatus = new LiveBroadcastStatus();
      liveBroadcastStatus.setPrivacyStatus(createLiveBroadcastRequest.getPrivacyStatus().getValue());
      liveBroadcastStatus.setMadeForKids(createLiveBroadcastRequest.getMadeForKids());
      liveBroadcast.setStatus(liveBroadcastStatus);

      // Insert the broadcast using the YouTube Data API
      LiveBroadcast createdLiveBroadcast = youTube
              .liveBroadcasts()
              .insert(getPartsForCreatingLiveBroadcast(), liveBroadcast)
              .execute();

      Video video = new Video();
      video.setId(createdLiveBroadcast.getId());

      // Add a category to the live broadcast and stream
      VideoSnippet videoSnippet = new VideoSnippet();
      videoSnippet.setTitle(createLiveBroadcastRequest.getTitle());
      videoSnippet.setDescription(createLiveBroadcastRequest.getDescription());
      videoSnippet.setCategoryId(createLiveBroadcastRequest.getCategoryId());
      video.setSnippet(videoSnippet);

      // Update the broadcast and live stream with a category
      youTube.videos()
          .update(createdLiveBroadcast.getId(), video)
          .execute();

      // Build and return the response with the created broadcast details
      return CreateYouTubeLiveBroadcastResponse.builder()
              .liveBroadcastId(createdLiveBroadcast.getId())
              .liveStreamLink(getYouTubeLiveStreamingByBroadcastId(createdLiveBroadcast.getId()))
              .liveBroadcast(YouTubeLiveBroadcastMapper.mapToLiveBroadcastResponse(createdLiveBroadcast))
              .build();
    } catch (IOException ex) {
      log.error(ex.getMessage(), ex);
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
  public UpdateYouTubeLiveBroadcastResponse updateLiveBroadcast(UpdateLiveBroadcastRequest updateLiveBroadcastRequest) {
    try {
      // Create a YouTube request using the provided access token
      YouTube youTube = createRequest(updateLiveBroadcastRequest.getAccessTokenForHttpRequest());
      Channel channel = getChannel(youTube);

      // List live broadcasts based on the broadcast ID
      YouTube.LiveBroadcasts.List liveBroadcasts = youTube.liveBroadcasts()
          .list(SNIPPET.getValue())
          .setId(updateLiveBroadcastRequest.getBroadcastId());

      // Check if the channel and live broadcasts are found and not empty
      if (nonNull(channel) && nonNull(liveBroadcasts) && !liveBroadcasts.isEmpty()) {
        LiveBroadcast liveBroadcast = liveBroadcasts.execute().getItems().get(0);
        LiveBroadcastSnippet snippet = liveBroadcast.getSnippet();

        snippet.setTitle(updateLiveBroadcastRequest.getTitle());
        snippet.setDescription(updateLiveBroadcastRequest.getDescription());

        YouTube.LiveBroadcasts.Update updateRequest = createUpdateRequest(channel.getId(), liveBroadcast, youTube);
        LiveBroadcast updatedLiveBroadcast = updateRequest.execute();

        return UpdateYouTubeLiveBroadcastResponse.builder()
            .liveBroadcastId(liveBroadcast.getId())
            .liveStreamLink(getYouTubeLiveStreamingByBroadcastId(updatedLiveBroadcast.getId()))
            .liveBroadcast(YouTubeLiveBroadcastMapper.mapToLiveBroadcastResponse(updatedLiveBroadcast))
            .build();
      }
      log.error("Cannot update live broadcast. Channel or broadcast cannot be found");
    } catch (IOException ex) {
      log.error(ex.getMessage(), ex);
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
  public RescheduleYouTubeLiveBroadcastResponse rescheduleLiveBroadcast(RescheduleLiveBroadcastRequest rescheduleLiveBroadcastRequest) {
    try {
      // Create a YouTube request using the provided access token
      YouTube youTube = createRequest(rescheduleLiveBroadcastRequest.getAccessTokenForHttpRequest());
      Channel channel = getChannel(youTube);

      // List live broadcasts based on the broadcast ID
      YouTube.LiveBroadcasts.List liveBroadcasts = youTube.liveBroadcasts()
          .list(SNIPPET.getValue())
          .setId(rescheduleLiveBroadcastRequest.getBroadcastId());

      // Check if the channel and live broadcasts are found and not empty
      if (nonNull(channel) && nonNull(liveBroadcasts) && !liveBroadcasts.isEmpty()) {
        // Execute the live broadcasts request and get the first item
        LiveBroadcast liveBroadcast = liveBroadcasts.execute().getItems().get(0);
        LiveBroadcastSnippet snippet = liveBroadcast.getSnippet();

        // Set the new scheduled start and end times
        snippet.setScheduledStartTime(toDateTime(rescheduleLiveBroadcastRequest.getScheduledStartDateTime()));
        snippet.setScheduledEndTime(toDateTime(rescheduleLiveBroadcastRequest.getScheduledEndDateTime()));

        // Create the update request for the broadcast
        YouTube.LiveBroadcasts.Update updateRequest = createUpdateRequest(channel.getId(), liveBroadcast, youTube);
        LiveBroadcast updatedLiveBroadcast = updateRequest.execute();

        // Build and return the response with updated live broadcast details
        return RescheduleYouTubeLiveBroadcastResponse.builder()
            .liveBroadcastId(updatedLiveBroadcast.getId())
            .liveStreamLink(getYouTubeLiveStreamingByBroadcastId(updatedLiveBroadcast.getId()))
            .liveBroadcast(YouTubeLiveBroadcastMapper.mapToLiveBroadcastResponse(updatedLiveBroadcast))
            .build();
      }
      log.error("Cannot reschedule live broadcast. Channel or broadcast cannot be found");
    } catch (IOException ex) {
      log.error(ex.getMessage(), ex);
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
  public YouTube.LiveBroadcasts.Update createUpdateRequest(String channelId, LiveBroadcast liveBroadcast, YouTube youTube) {
    try {
      // Get the snippet from the live broadcast
      LiveBroadcastSnippet snippet = liveBroadcast.getSnippet();
      snippet.setChannelId(channelId);

      // Create a new live broadcast object for the update
      LiveBroadcast updateBroadcast = new LiveBroadcast();
      // Set the ID and snippet for the update broadcast
      updateBroadcast.setSnippet(snippet);

      // Return update request
      return youTube.liveBroadcasts()
          .update(SNIPPET.getValue(), updateBroadcast);
    } catch (IOException ex) {
      log.error(ex.getMessage(), ex);
    }
    throw new UnableToCompleteOperationException();
  }

  /**
   * Retrieves the YouTube channel associated with the authenticated user.
   *
   * <p> This method creates and executes a request to list channels using the 'snippet' part,
   * and sets the request to retrieve channels associated with the authenticated user. It uses
   * the provided {@link YouTube} object to make the request and includes the API key for
   * authentication.</p>
   *
   * <p> The method handles the response by checking if the list of channels is not empty,
   * and returns the first channel in the list. If no channels are found or if an error
   * occurs during the API request, it logs the error and throws an {@link ExternalSystemException}
   * with a message indicating the failure.</p>
   *
   * <p> Note: This method assumes that the {@link YouTube} object is properly authenticated
   * and configured with the necessary API credentials.</p>
   *
   * @param youTube The {@link YouTube} object used to make the API request.
   * @return The {@link Channel} object associated with the authenticated user, or null if no channels are found.
   *
   * @see <a href="https://www.getphyllo.com/post/how-to-get-youtube-api-key">
   *   How to Get YouTube API Key: A Detailed Guide for Developers</a>
   */
  public Channel getChannel(YouTube youTube) {
    try {
      // Create a request to list channels using the 'snippet' part
      Channels.List channelRequest = youTube
              .channels()
              .list(SNIPPET.getValue());
      channelRequest.setMine(true); // Set to retrieve channels associated with the authenticated user
      channelRequest.setKey(serviceApiKey);

      // Execute the request and obtain the response
      ChannelListResponse channelListResponse = channelRequest.execute();
      List<Channel> channels = Optional.of(channelListResponse.getItems()).orElse(emptyList());

      if (!channels.isEmpty()) {
        return channels.get(0);
      }
    } catch (Exception ex) {
      // Log the error and throw an exception if accessing YouTube API fails
      log.error(ex.getMessage());
      throw new ExternalSystemException(ExternalSystemType.YOUTUBE.getValue());
    }

    // Return the list of YouTubeChannelResponse objects
    return null;
  }

  /**
   * Constructs and returns a comma-separated string of parts required for creating a live broadcast.
   *
   * <p> This method creates a list of parts needed for creating a YouTube live broadcast,
   * including "snippet", "status", and "contentDetails". It then joins these parts into a
   * single comma-separated string using {@link String#join(CharSequence, CharSequence...)}.</p>
   *
   * <p> The resulting string can be used in API requests that require specifying the parts
   * of a YouTube resource to be included in the response.</p>
   *
   * @return A comma-separated string of parts for creating a live broadcast.
   */
  private String getPartsForCreatingLiveBroadcast() {
    List<String> parts = List.of(SNIPPET.getValue(), STATUS.getValue(), CONTENT_DETAILS.getValue());
    return String.join(COMMA, parts);
  }

  /**
   * Creates and returns a YouTube request object configured with the provided access token.
   *
   * <p> This method constructs an instance of {@link YouTube} using the provided access token.
   * It uses a {@link Builder} to initialize the YouTube object with the necessary
   * transport, JSON factory, and HTTP request initializer. The HTTP request initializer is
   * obtained using the {@link #getHttpRequestInitializer(String)} method, which configures
   * the HTTP request with the provided access token.</p>
   *
   * <p> The resulting YouTube object can be used to interact with the YouTube Data API,
   * enabling operations such as creating, updating, and managing YouTube resources.</p>
   *
   * @param accessToken The access token to be used for authenticating YouTube API requests.
   * @return A {@link YouTube} object configured with the provided access token.
   *
   * @see <a href="https://developers.google.com/youtube/registering_an_application">
   *   Obtaining authorization credentials</a>
   * @see <a href="https://kingbbode.tistory.com/8">
   *   Integrating Google API in Spring Boot (1) Setting up Oauth authentication</a>
   * @see <a href="https://velog.io/@ssongji/%EC%9C%A0%ED%8A%9C%EB%B8%8C-%EB%8D%B0%EC%9D%B4%ED%84%B0-%ED%81%AC%EB%A1%A4%EB%A7%81-%EB%B0%8F-%EC%8B%9C%EA%B0%81%ED%99%94-%ED%94%84%EB%A1%9C%EC%A0%9D%ED%8A%B8-1.-YOUTUBE-API-%EC%82%AC%EC%9A%A9-%ED%99%98%EA%B2%BD-%EC%84%A4%EC%A0%95">
   *  YouTube API usage environment settings</a>
   */
  private YouTube createRequest(String accessToken) {
    return new YouTube.Builder(getTransport(), getJsonFactory(), getHttpRequestInitializer(accessToken))
            .build();
  }

  /**
   * Creates and returns an {@link HttpRequestInitializer} with the provided access token.
   *
   * <p> This method constructs an {@link HttpRequestInitializer} that configures an
   * HTTP request with a JSON parser, sets the headers using the provided access token,
   * and ensures exceptions are thrown on execution errors. </p>
   *
   *
   * @param accessToken The access token to be included in the HTTP request headers.
   * @return An {@link HttpRequestInitializer} configured with the provided access token.
   */
  private HttpRequestInitializer getHttpRequestInitializer(String accessToken) {
    return httpRequest -> {
      httpRequest.setParser(new JsonObjectParser(getJsonFactory()));
      httpRequest.setHeaders(getHeaders(accessToken));
      httpRequest.setThrowExceptionOnExecuteError(true);
      httpRequest.setConnectTimeout(5000);
      httpRequest.setReadTimeout(5000);

      httpRequest.executeAsync();
    };
  }

  /**
   * Creates and returns HTTP headers with the provided access token.
   *
   * <p> This method constructs an instance of {@link HttpHeaders} and sets the
   * Authorization header with the bearer token generated from the provided access token.
   * The bearer token is created using the {@link #getBearerToken(String)} method.</p>
   *
   * <p> The resulting HttpHeaders object can be used to authenticate HTTP requests
   * that require OAuth 2.0 bearer token authentication.</p>
   *
   * @param accessToken The access token to be included in the Authorization header.
   * @return An {@link HttpHeaders} object with the Authorization header set.
   *
   * @see <a href="https://developers.google.com/youtube/v3/docs/">
   *   YouTube API Reference - Calling the API</a>
   * @see <a href="https://stackoverflow.com/a/31169962/10152132">
   *   Set Bearer Token for Google YouTube API Request</a>
   */
  private HttpHeaders getHeaders(String accessToken) {
    HttpHeaders headers = new HttpHeaders();
    headers.set(AUTHORIZATION, getBearerToken(accessToken));
    return headers;
  }

  /**
   * Constructs a bearer token from the provided access token.
   *
   * <p> This method takes an access token as input and constructs a bearer token
   * by concatenating the "Bearer" prefix with the access token. The resulting bearer
   * token is used for authorizing API requests that require OAuth 2.0 authentication.</p>
   *
   * <p> The "Bearer" prefix is a standard convention for OAuth 2.0 token usage, ensuring
   * that the access token is correctly formatted for authorization headers in HTTP requests.</p>
   *
   * @param accessToken The access token to be converted into a bearer token.
   * @return A bearer token constructed from the provided access token.
   */
  private String getBearerToken(String accessToken) {
    return BEARER.concat(" ").concat(accessToken);
  }
}