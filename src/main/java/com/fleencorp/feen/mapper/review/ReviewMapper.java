package com.fleencorp.feen.mapper.review;

import com.fleencorp.feen.review.model.domain.Review;
import com.fleencorp.feen.review.model.response.ReviewResponse;

import java.util.List;

public interface ReviewMapper {

  ReviewResponse toReviewResponsePublic(Review entry);

  List<ReviewResponse> toReviewResponsesPublic(List<Review> entries);
}