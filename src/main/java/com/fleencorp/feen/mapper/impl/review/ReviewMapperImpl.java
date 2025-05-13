package com.fleencorp.feen.mapper.impl.review;

import com.fleencorp.feen.mapper.impl.BaseMapper;
import com.fleencorp.feen.mapper.review.ReviewMapper;
import com.fleencorp.feen.model.domain.review.Review;
import com.fleencorp.feen.model.info.like.UserLikeInfo;
import com.fleencorp.feen.model.info.stream.rating.RatingInfo;
import com.fleencorp.feen.model.response.review.ReviewResponse;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.List;
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
public final class ReviewMapperImpl extends BaseMapper implements ReviewMapper {

  public ReviewMapperImpl(final MessageSource messageSource) {
    super(messageSource);
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
  public ReviewResponse toReviewResponsePublic(final Review entry) {
    if (nonNull(entry)) {
      final RatingInfo ratingInfo = RatingInfo.of(
        entry.getRating(),
        entry.getRatingNumber(),
        entry.getRatingName(),
        translate(entry.getRating().getMessageCode())
      );

      final ReviewResponse response = new ReviewResponse();
      response.setId(entry.getReviewId());
      response.setReview(entry.getReview());
      response.setReviewParentType(entry.getReviewParentType());
      response.setRatingInfo(ratingInfo);
      response.setReviewerName(entry.getReviewerName());
      response.setReviewerPhoto(entry.getReviewerPhoto());
      response.setMemberId(entry.getMemberId());
      response.setTotalLikeCount(entry.getLikeCount());

      response.setParentId(entry.getParentId());
      response.setParentTitle(entry.getParentTitle());

      response.setCreatedOn(entry.getCreatedOn());
      response.setUpdatedOn(entry.getUpdatedOn());

      final UserLikeInfo userLikeInfo = UserLikeInfo.of();
      response.setUserLikeInfo(userLikeInfo);

      return response;
    }
    return null;
  }

  /**
   * Converts a list of {@link Review} entities to a list of {@link ReviewResponse} DTOs.
   *
   * <p>This method checks if the provided list of {@link Review} entities is non-null.
   * It then filters out any null entries and maps each valid {@link Review} to a {@link ReviewResponse}
   * using the {@link ReviewMapperImpl#toReviewResponsePublic(Review)} method.</p>
   *
   * @param entries the list of {@link Review} entities to be converted
   * @return a list of {@link ReviewResponse} DTOs, or an empty list if the input is null
   */
  @Override
  public List<ReviewResponse> toReviewResponsesPublic(final List<Review> entries) {
    if (nonNull(entries)) {
      return entries.stream()
        .filter(Objects::nonNull)
        .map(this::toReviewResponsePublic)
        .toList();
    }
    return List.of();
  }

}

