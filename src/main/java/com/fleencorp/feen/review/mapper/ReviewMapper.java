package com.fleencorp.feen.review.mapper;

import com.fleencorp.feen.review.model.domain.Review;
import com.fleencorp.feen.review.model.response.base.ReviewResponse;

import java.util.List;

public interface ReviewMapper {

  ReviewResponse toReviewResponsePublic(Review entry);

  List<ReviewResponse> toReviewResponsesPublic(List<Review> entries);
}