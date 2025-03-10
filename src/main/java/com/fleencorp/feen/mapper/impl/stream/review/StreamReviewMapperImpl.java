package com.fleencorp.feen.mapper.impl.stream.review;

import com.fleencorp.feen.mapper.stream.review.StreamReviewMapper;
import com.fleencorp.feen.model.domain.review.Review;
import com.fleencorp.feen.model.info.stream.rating.StreamRatingInfo;
import com.fleencorp.feen.model.response.review.ReviewResponse;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

import static java.util.Objects.nonNull;

/**
 * A utility class responsible for mapping between {@link Review} entities and their corresponding DTOs,
 * such as {@link ReviewResponse}.
 *
 * <p>The {@code StreamReviewMapper} provides static methods to handle the conversion of {@link Review} entities
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
   * Converts a {@link Review} object to a {@link ReviewResponse}.
   *
   * <p>This method maps the fields of a {@link Review} object to a {@link ReviewResponse}
   * object, building the response with details such as the review's ID, review text, rating, rating name,
   * and timestamps for when the review was created and updated.</p>
   *
   * @param entry the {@link Review} object to be converted
   * @return a {@link ReviewResponse} object containing the converted data, or {@code null} if the input is null
   */
  @Override
  public ReviewResponse toStreamReviewResponsePublic(final Review entry) {
    if (nonNull(entry)) {
      final StreamRatingInfo ratingInfo = StreamRatingInfo.of(
        entry.getRating(),
        entry.getRatingNumber(),
        entry.getRatingName(),
        translate(entry.getRating().getMessageCode())
      );

      final ReviewResponse response = new ReviewResponse();
      response.setId(entry.getReviewId());
      response.setReview(entry.getReview());
      response.setRatingInfo(ratingInfo);
      response.setReviewerName(entry.getReviewerName());
      response.setReviewerPhoto(entry.getReviewerPhoto());

      response.setCreatedOn(entry.getCreatedOn());
      response.setUpdatedOn(entry.getUpdatedOn());

      return response;
    }
    return null;
  }

  /**
   * Converts a {@link Review} entity to a private {@link ReviewResponse} object.
   *
   * <p>This method extends the functionality of {@link #toStreamReviewResponsePublic(Review)} by adding
   * additional private stream-related details to the response, such as the stream ID and stream title.</p>
   *
   * @param entry the {@link Review} entity to be converted to a private {@link ReviewResponse}
   * @return the private {@link ReviewResponse} containing both public and private review details,
   *         or {@code null} if the input {@link Review} is {@code null}
   */
  public ReviewResponse toStreamReviewResponsePrivate(final Review entry) {
    final ReviewResponse response = toStreamReviewResponsePublic(entry);

    if (nonNull(response)) {
      response.setStreamId(entry.getStreamId());
      response.setStreamTitle(entry.getStreamTitle());
    }

    return response;
  }

  /**
   * Converts a list of {@link Review} entities to a list of {@link ReviewResponse} DTOs.
   *
   * <p>This method checks if the provided list of {@link Review} entities is non-null.
   * It then filters out any null entries and maps each valid {@link Review} to a {@link ReviewResponse}
   * using the {@link StreamReviewMapperImpl#toStreamReviewResponsePublic(Review)} method.</p>
   *
   * @param entries the list of {@link Review} entities to be converted
   * @return a list of {@link ReviewResponse} DTOs, or an empty list if the input is null
   */
  @Override
  public List<ReviewResponse> toStreamReviewResponsesPublic(final List<Review> entries) {
    if (nonNull(entries)) {
      return entries.stream()
        .filter(Objects::nonNull)
        .map(this::toStreamReviewResponsePublic)
        .toList();
    }
    return List.of();
  }

  /**
   * Converts a list of {@link Review} entities to a list of private {@link ReviewResponse} objects.
   *
   * <p>This method processes each {@link Review} in the provided list and converts it to a private
   * {@link ReviewResponse} using {@link #toStreamReviewResponsePrivate(Review)}. It filters out any
   * {@code null} values from the input list.</p>
   *
   * @param entries the list of {@link Review} entities to be converted to private {@link ReviewResponse} objects
   * @return a list of {@link ReviewResponse} containing both public and private review details,
   *         or an empty list if the input list is {@code null} or contains no valid entries
   */
  @Override
  public List<ReviewResponse> toStreamReviewResponsesPrivate(final List<Review> entries) {
    if (nonNull(entries)) {
      return entries.stream()
        .filter(Objects::nonNull)
        .map(this::toStreamReviewResponsePrivate)
        .toList();
    }
    return List.of();
  }

}

