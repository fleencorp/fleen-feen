package com.fleencorp.feen.mapper.review;

import com.fleencorp.feen.model.domain.review.Review;
import com.fleencorp.feen.model.response.review.ReviewResponse;

import java.util.List;

public interface ReviewMapper {

  ReviewResponse toReviewResponsePublic(Review entry);

  List<ReviewResponse> toReviewResponsesPublic(List<Review> entries);

  List<ReviewResponse> toReviewResponsesPrivate(List<Review> entries);
}