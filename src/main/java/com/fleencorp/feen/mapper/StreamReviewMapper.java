package com.fleencorp.feen.mapper;

import com.fleencorp.feen.model.domain.stream.StreamReview;
import com.fleencorp.feen.model.response.stream.review.StreamReviewResponse;

import java.util.List;
import java.util.Objects;

import static java.util.Objects.nonNull;

/**
 * A utility class responsible for mapping between {@link StreamReview} entities and their corresponding DTOs,
 * such as {@link StreamReviewResponse}.
 *
 * <p>The {@code StreamReviewMapper} provides static methods to handle the conversion of {@link StreamReview} entities
 * to various response types, including basic and extended versions with additional details like stream titles.</p>
 *
 * <p>These mappings are typically used when transforming entities for external use, such as sending them as part of API responses.</p>
 *
 * @author Yusuf Alamu Musa
 * @version 1.0
 */
public class StreamReviewMapper {

  private StreamReviewMapper() {}

  /**
   * Converts a {@link StreamReview} object to a {@link StreamReviewResponse}.
   *
   * <p>This method maps the fields of a {@link StreamReview} object to a {@link StreamReviewResponse}
   * object, building the response with details such as the review's ID, review text, rating, rating name,
   * and timestamps for when the review was created and updated.</p>
   *
   * @param entry the {@link StreamReview} object to be converted
   * @return a {@link StreamReviewResponse} object containing the converted data, or {@code null} if the input is null
   */
  public static StreamReviewResponse toStreamReviewResponse(final StreamReview entry) {
    if (nonNull(entry)) {
      return StreamReviewResponse.builder()
        .id(entry.getStreamReviewId())
        .review(entry.getReview())
        .rating(entry.getRatingNumber())
        .ratingName(entry.getRatingName())
        .createdOn(entry.getCreatedOn())
        .updatedOn(entry.getUpdatedOn())
        .streamTitle(entry.getStreamTitle())
        .streamId(entry.getStreamId())
        .build();
    }
    return null;
  }

  /**
   * Converts a {@link StreamReview} entity to a {@link StreamReviewResponse} DTO and includes additional details.
   *
   * <p>This method first checks if the provided {@link StreamReview} entry is non-null.
   * It then calls {@link StreamReviewMapper#toStreamReviewResponse(StreamReview)} to create a
   * {@link StreamReviewResponse} and adds the stream title to the response, if applicable.</p>
   *
   * @param entry the {@link StreamReview} entity to be converted
   * @return a {@link StreamReviewResponse} DTO with the stream title included, or {@code null} if the input is null
   */
  public static StreamReviewResponse toStreamReviewResponseMore(final StreamReview entry) {
    if (nonNull(entry)) {
      final StreamReviewResponse streamReviewResponse = toStreamReviewResponse(entry);
      if (nonNull(streamReviewResponse)) {
        streamReviewResponse.setReviewerName(entry.getReviewerName());
        streamReviewResponse.setReviewerPhoto(entry.getReviewerPhoto());
      }
      return streamReviewResponse;
    }
    return null;
  }

  /**
   * Converts a list of {@link StreamReview} entities to a list of {@link StreamReviewResponse} DTOs.
   *
   * <p>This method checks if the provided list of {@link StreamReview} entities is non-null.
   * It then filters out any null entries and maps each valid {@link StreamReview} to a {@link StreamReviewResponse}
   * using the {@link StreamReviewMapper#toStreamReviewResponse(StreamReview)} method.</p>
   *
   * @param entries the list of {@link StreamReview} entities to be converted
   * @return a list of {@link StreamReviewResponse} DTOs, or an empty list if the input is null
   */
  public static List<StreamReviewResponse> toStreamReviewResponses(final List<StreamReview> entries) {
    if (nonNull(entries)) {
      return entries.stream()
        .filter(Objects::nonNull)
        .map(StreamReviewMapper::toStreamReviewResponse)
        .toList();
    }
    return List.of();
  }

  /**
   * Converts a list of {@link StreamReview} entities to a list of {@link StreamReviewResponse} DTOs,
   * including additional details for each response.
   *
   * <p>This method checks if the provided list of {@link StreamReview} entries is non-null, filters out any null entries,
   * and then maps each valid entry to a {@link StreamReviewResponse} using the {@link StreamReviewMapper#toStreamReviewResponseMore(StreamReview)} method.
   * The resulting list of responses will contain additional information, such as the stream title, for each review.</p>
   *
   * @param entries the list of {@link StreamReview} entities to be converted
   * @return a list of {@link StreamReviewResponse} DTOs, or an empty list if the input is null
   */
  public static List<StreamReviewResponse> toStreamReviewResponsesMore(final List<StreamReview> entries) {
    if (nonNull(entries)) {
      return entries.stream()
        .filter(Objects::nonNull)
        .map(StreamReviewMapper::toStreamReviewResponseMore)
        .toList();
    }
    return List.of();
  }
}
