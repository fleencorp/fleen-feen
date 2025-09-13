package com.fleencorp.feen.stream.service.impl.external;

import com.fleencorp.base.exception.externalsystem.ExternalSystemException;
import com.fleencorp.feen.common.aspect.MeasureExecutionTime;
import com.fleencorp.feen.common.constant.external.ExternalSystemType;
import com.fleencorp.feen.common.constant.external.google.youtube.YouTubeVideoPart;
import com.fleencorp.feen.common.service.report.ReporterService;
import com.fleencorp.feen.model.response.external.google.youtube.category.YouTubeCategoriesResponse;
import com.fleencorp.feen.model.response.external.google.youtube.category.YouTubeCategoryResponse;
import com.fleencorp.feen.stream.service.external.YouTubeChannelService;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Channel;
import com.google.api.services.youtube.model.ChannelListResponse;
import com.google.api.services.youtube.model.VideoCategory;
import com.google.api.services.youtube.model.VideoCategoryListResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.fleencorp.feen.common.constant.base.ReportMessageType.YOUTUBE;
import static com.fleencorp.feen.common.constant.base.SimpleConstant.COMMA;
import static com.fleencorp.feen.common.constant.external.google.youtube.base.YouTubeParameter.US;
import static com.fleencorp.feen.oauth2.service.external.impl.external.GoogleOauth2ServiceImpl.getJsonFactory;
import static com.fleencorp.feen.oauth2.service.external.impl.external.GoogleOauth2ServiceImpl.getTransport;
import static java.util.Collections.emptyList;
import static java.util.Objects.nonNull;

/**
 * Service class for interacting with YouTube channels.
 *
 * @author Yusuf Alamu Musa
 * @version 1.0
 */
@Slf4j
@Component
public class YouTubeChannelServiceImpl implements YouTubeChannelService {

  private final String applicationName;
  private final String serviceApiKey;
  private final ReporterService reporterService;

  /**
   * Constructs a YouTubeChannelService instance with the specified application name and API key.
   *
   * @param applicationName The name of the application using this service.
   * @param serviceApiKey   The API key for accessing YouTube Data API.
   */
  public YouTubeChannelServiceImpl(
      @Value("${application.name}") final String applicationName,
      @Value("${youtube.data.api-key}") final String serviceApiKey,
      final ReporterService reporterService) {
    this.applicationName = applicationName;
    this.serviceApiKey = serviceApiKey;
    this.reporterService = reporterService;
  }

  /**
   * Retrieves a list of YouTube categories that can be assigned to a stream.
   *
   * <p>This method first calls {@code listCategories()} to obtain all available YouTube categories.
   * If categories are found, it filters the list to include only those that are assignable.
   * The filtered list is then set back to the {@code YouTubeCategoriesResponse} object.</p>
   *
   * @return a {@code YouTubeCategoriesResponse} containing only the categories that are assignable.
   *         If no categories are available, an empty or unchanged response is returned.
   */
  @Override
  public YouTubeCategoriesResponse listAssignableCategories() {
    // Retrieve all available YouTube categories.
    final YouTubeCategoriesResponse youTubeCategoriesResponse = listCategories();

    // Check if the response is not null and contains categories.
    if (nonNull(youTubeCategoriesResponse) && youTubeCategoriesResponse.hasCategories()) {
      // Filter the categories to include only those that are assignable.
      final List<YouTubeCategoryResponse> assignedCategory = youTubeCategoriesResponse.getCategories()
        .stream()
        .filter(YouTubeCategoryResponse::isAssignable)
        .toList();

      // Set the filtered list of categories back to the response object.
      youTubeCategoriesResponse.setCategories(assignedCategory);
    }

    // Return the response with the filtered assignable categories.
    return youTubeCategoriesResponse;
  }

  /**
   * Retrieves and lists YouTube categories based on the specified region code.
   *
   * <p>This method initializes a list to store {@link YouTubeCategoryResponse} objects.
   * It creates a request to list categories using the 'snippet' and 'id' parts, specific
   * to the US region. It executes the request, retrieves the response, and converts
   * {@link VideoCategory} objects to {@link YouTubeCategoryResponse} objects using
   * {@link #addYouTubeCategoriesResponse(List, List)}.</p>
   *
   * <p>If an {@link IOException} occurs during the API call, it logs the error and
   * throws an {@link ExternalSystemException} for YouTube.</p>
   *
   * @return A {@link YouTubeCategoriesResponse} containing the list of YouTube category responses.
   * @throws ExternalSystemException If an error occurs while accessing the YouTube API.
   */
  @MeasureExecutionTime
  protected YouTubeCategoriesResponse listCategories() {
    // Initialize a list to store YouTubeCategoryResponse objects
    final List<YouTubeCategoryResponse> youTubeCategoryResponses = new ArrayList<>();
    try {
      // Create a request to list categories using the 'snippet' and 'id' part
      final YouTube.VideoCategories.List categoryRequest = getYouTube().videoCategories()
          .list(String.join(COMMA,
              YouTubeVideoPart.id(),
              YouTubeVideoPart.snippet()));
      categoryRequest.setRegionCode(US.getValue()); // Set to retrieve categories associated with the specific region
      categoryRequest.setKey(serviceApiKey);

      // Execute the request and obtain the response
      final VideoCategoryListResponse categoryListResponse = categoryRequest.execute();
      final List<VideoCategory> categories = Optional.of(categoryListResponse.getItems()).orElse(emptyList());
      addYouTubeCategoriesResponse(categories, youTubeCategoryResponses);

    } catch (final IOException ex) {
      // Log the error and throw an exception if accessing YouTube API fails
      log.error(ex.getMessage());
      throw new ExternalSystemException(ExternalSystemType.YOUTUBE.getValue());
    }

    return YouTubeCategoriesResponse.of(youTubeCategoryResponses);
  }

  /**
   * Adds YouTube category responses based on the provided categories.
   *
   * <p>This method iterates through the {@code categories} list and creates
   * {@link YouTubeCategoryResponse} objects for each category. These responses
   * include the category ID, kind, and name retrieved from the category's snippet.</p>
   *
   * @param categories         The list of {@link VideoCategory} objects to convert to responses.
   * @param categoriesResponses The list to which {@link YouTubeCategoryResponse} objects will be added.
   */
  public void addYouTubeCategoriesResponse(final List<VideoCategory> categories, final List<YouTubeCategoryResponse> categoriesResponses) {
    if (nonNull(categories) && nonNull(categoriesResponses)) {
      categories.stream()
        .filter(Objects::nonNull)
        .forEach(category -> {
        final YouTubeCategoryResponse youTubeCategoryResponse = YouTubeCategoryResponse.of();
        youTubeCategoryResponse.setId(category.getId());
        youTubeCategoryResponse.setKind(category.getKind());
        youTubeCategoryResponse.setName(category.getSnippet().getTitle());
        youTubeCategoryResponse.setAssignable(category.getSnippet().getAssignable());
        categoriesResponses.add(youTubeCategoryResponse);
      });
    }
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
  @Override
  public Channel getChannel(final YouTube youTube) {
    try {
      // Execute the request and obtain the response
      final ChannelListResponse channelListResponse = getChannels(youTube);
      final List<Channel> channels = Optional.of(channelListResponse.getItems()).orElse(emptyList());

      if (!channels.isEmpty()) {
        // Return the first channel in the list if the channel list is not empty
        return channels.getFirst();
      }
    } catch (final RuntimeException ex) {
      // Log the error and throw an exception if accessing YouTube API fails
      final String errorMessage = String.format("Error occurred while retrieving YouTube channel. Reason: %s", ex.getMessage());
      reporterService.sendMessage(errorMessage, YOUTUBE);

      throw new ExternalSystemException(ExternalSystemType.youTube());
    }
    // Return null if no channel was found
    return null;
  }

  /**
   * Retrieves the list of YouTube channels associated with the authenticated user.
   *
   * @param youTube the YouTube service object used to access the YouTube Data API
   * @return a {@code ChannelListResponse} containing the list of channels
   * @throws ExternalSystemException if there is an error accessing the YouTube API
   */
  @Override
  public ChannelListResponse getChannels(final YouTube youTube) {
    try {
      // Create a request to list channels using the 'snippet' part
      final YouTube.Channels.List channelRequest = youTube
        .channels()
        .list(YouTubeVideoPart.snippet());
      channelRequest.setMine(true); // Set to retrieve channels associated with the authenticated user
      channelRequest.setKey(serviceApiKey); // Set the API key for authentication

      return channelRequest.execute(); // Execute the request and return the response
    } catch (final IOException ex) {
      final String errorMessage = String.format("Error occurred while retrieving YouTube channels. Reason: %s", ex.getMessage());
      reporterService.sendMessage(errorMessage, YOUTUBE);

      throw new ExternalSystemException(ExternalSystemType.YOUTUBE.getValue());
    }
  }


  /**
   * Retrieves an instance of YouTube client for making API requests.
   *
   * <p>This method constructs and returns a new instance of {@link YouTube} client
   * using the configured transport, JSON factory, and application name.</p>
   *
   * @return An instance of {@link YouTube} client configured with the necessary components.
   * @throws IOException if an error occurs while building the YouTube client.
   */
  public YouTube getYouTube() throws IOException {
    return new YouTube.Builder(
        getTransport(),
        getJsonFactory(),
        _ -> {})
        .setApplicationName(applicationName)
        .build();
  }
}
