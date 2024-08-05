package com.fleencorp.feen.service.external.google.youtube;

import com.fleencorp.base.exception.externalsystem.ExternalSystemException;
import com.fleencorp.feen.aspect.MeasureExecutionTime;
import com.fleencorp.feen.constant.external.ExternalSystemType;
import com.fleencorp.feen.model.response.external.google.youtube.category.YouTubeCategoriesResponse;
import com.fleencorp.feen.model.response.external.google.youtube.category.YouTubeCategoryResponse;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.VideoCategory;
import com.google.api.services.youtube.model.VideoCategoryListResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.fleencorp.feen.constant.base.SimpleConstant.COMMA;
import static com.fleencorp.feen.constant.external.google.youtube.YouTubeVideoPart.ID;
import static com.fleencorp.feen.constant.external.google.youtube.YouTubeVideoPart.SNIPPET;
import static com.fleencorp.feen.constant.external.google.youtube.base.YouTubeParameter.US;
import static com.fleencorp.feen.service.external.google.oauth2.GoogleOauth2Service.getJsonFactory;
import static com.fleencorp.feen.service.external.google.oauth2.GoogleOauth2Service.getTransport;
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
public class YouTubeChannelService {

  private final String applicationName;
  private final String serviceApiKey;

  /**
   * Constructs a YouTubeChannelService instance with the specified application name and API key.
   *
   * @param applicationName The name of the application using this service.
   * @param serviceApiKey   The API key for accessing YouTube Data API.
   */
  public YouTubeChannelService(
      @Value("${application.name}") final String applicationName,
      @Value("${youtube.data.api-key}") final String serviceApiKey) {
    this.applicationName = applicationName;
    this.serviceApiKey = serviceApiKey;
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
  public YouTubeCategoriesResponse listCategories() {
    // Initialize a list to store YouTubeCategoryResponse objects
    final List<YouTubeCategoryResponse> youTubeCategoryResponses = new ArrayList<>();
    try {
      // Create a request to list categories using the 'snippet' and 'id' part
      final YouTube.VideoCategories.List categoryRequest = getYouTube().videoCategories()
          .list(String.join(COMMA,
              ID.getValue(),
              SNIPPET.getValue()));
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
      categories.forEach(category -> {
        final YouTubeCategoryResponse youTubeCategoryResponse = new YouTubeCategoryResponse();
        youTubeCategoryResponse.setId(category.getId());
        youTubeCategoryResponse.setKind(category.getKind());
        youTubeCategoryResponse.setName(category.getSnippet().getTitle());
        categoriesResponses.add(youTubeCategoryResponse);
      });
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
        httpRequest -> {})
        .setApplicationName(applicationName)
        .build();
  }
}
