package com.fleencorp.feen.mapper.impl.stream.review;

import com.fleencorp.feen.mapper.stream.review.StreamReviewMapper;
import com.fleencorp.feen.model.domain.stream.StreamReview;
import com.fleencorp.feen.model.info.stream.rating.StreamRatingInfo;
import com.fleencorp.feen.model.response.stream.review.StreamReviewResponse;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Locale;
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
@Component
public final class StreamReviewMapperImpl implements StreamReviewMapper {

  private final MessageSource messageSource;

  public StreamReviewMapperImpl(final MessageSource messageSource) {
    this.messageSource = messageSource;
  }

  /**
   * Translates a message code into a localized message based on the current locale.
   *
   * <p>This method retrieves a message from the {@link MessageSource} using the provided
   * message code and the locale obtained from {@link LocaleContextHolder}.</p>
   *
   * @param messageCode the code of the message to be translated; must not be {@code null}.
   * @return the localized message corresponding to the given message code.
   */
  private String translate(final String messageCode) {
    final Locale locale = LocaleContextHolder.getLocale();
    return messageSource.getMessage(messageCode, null, locale);
  }

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
  @Override
  public StreamReviewResponse toStreamReviewResponse(final StreamReview entry) {
    if (nonNull(entry)) {
      final StreamRatingInfo ratingInfo = StreamRatingInfo.of(entry.getRating(), entry.getRatingNumber(), entry.getRatingName(), translate(entry.getRating().getMessageCode()));

      final StreamReviewResponse response = new StreamReviewResponse();
      response.setId(entry.getReviewId());
      response.setReview(entry.getReview());
      response.setRatingInfo(ratingInfo);
      response.setCreatedOn(entry.getCreatedOn());
      response.setUpdatedOn(entry.getUpdatedOn());
      response.setStreamTitle(entry.getStreamTitle());
      response.setStreamId(entry.getStreamId());

      return response;
    }
    return null;
  }

  /**
   * Converts a {@link StreamReview} entity to a {@link StreamReviewResponse} DTO and includes additional details.
   *
   * <p>This method first checks if the provided {@link StreamReview} entry is non-null.
   * It then calls {@link StreamReviewMapperImpl#toStreamReviewResponse(StreamReview)} to create a
   * {@link StreamReviewResponse} and adds the stream title to the response, if applicable.</p>
   *
   * @param entry the {@link StreamReview} entity to be converted
   * @return a {@link StreamReviewResponse} DTO with the stream title included, or {@code null} if the input is null
   */
  public StreamReviewResponse toStreamReviewResponseMore(final StreamReview entry) {
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
   * using the {@link StreamReviewMapperImpl#toStreamReviewResponse(StreamReview)} method.</p>
   *
   * @param entries the list of {@link StreamReview} entities to be converted
   * @return a list of {@link StreamReviewResponse} DTOs, or an empty list if the input is null
   */
  @Override
  public List<StreamReviewResponse> toStreamReviewResponses(final List<StreamReview> entries) {
    if (nonNull(entries)) {
      return entries.stream()
        .filter(Objects::nonNull)
        .map(this::toStreamReviewResponse)
        .toList();
    }
    return List.of();
  }

  /**
   * Converts a list of {@link StreamReview} entities to a list of {@link StreamReviewResponse} DTOs,
   * including additional details for each response.
   *
   * <p>This method checks if the provided list of {@link StreamReview} entries is non-null, filters out any null entries,
   * and then maps each valid entry to a {@link StreamReviewResponse} using the {@link StreamReviewMapperImpl#toStreamReviewResponseMore(StreamReview)} method.
   * The resulting list of responses will contain additional information, such as the stream title, for each review.</p>
   *
   * @param entries the list of {@link StreamReview} entities to be converted
   * @return a list of {@link StreamReviewResponse} DTOs, or an empty list if the input is null
   */
  @Override
  public List<StreamReviewResponse> toStreamReviewResponsesMore(final List<StreamReview> entries) {
    if (nonNull(entries)) {
      return entries.stream()
        .filter(Objects::nonNull)
        .map(this::toStreamReviewResponseMore)
        .toList();
    }
    return List.of();
  }
}
