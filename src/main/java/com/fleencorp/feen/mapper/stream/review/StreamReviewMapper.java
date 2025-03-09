package com.fleencorp.feen.mapper.stream.review;

import com.fleencorp.feen.model.domain.review.Review;
import com.fleencorp.feen.model.response.review.ReviewResponse;

import java.util.List;

public interface StreamReviewMapper {

  ReviewResponse toStreamReviewResponsePublic(Review entry);

  List<ReviewResponse> toStreamReviewResponsesPublic(List<Review> entries);

  List<ReviewResponse> toStreamReviewResponsesPrivate(List<Review> entries);
}
