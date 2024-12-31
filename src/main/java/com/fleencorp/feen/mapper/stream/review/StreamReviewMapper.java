package com.fleencorp.feen.mapper.stream.review;

import com.fleencorp.feen.model.domain.stream.StreamReview;
import com.fleencorp.feen.model.response.stream.review.StreamReviewResponse;

import java.util.List;

public interface StreamReviewMapper {

  StreamReviewResponse toStreamReviewResponse(StreamReview entry);

  List<StreamReviewResponse> toStreamReviewResponses(List<StreamReview> entries);

  List<StreamReviewResponse> toStreamReviewResponsesMore(List<StreamReview> entries);
}
