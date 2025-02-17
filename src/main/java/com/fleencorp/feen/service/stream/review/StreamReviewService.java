package com.fleencorp.feen.service.stream.review;

import com.fleencorp.base.model.request.search.SearchRequest;
import com.fleencorp.feen.model.dto.stream.review.AddStreamReviewDto;
import com.fleencorp.feen.model.response.stream.review.AddStreamReviewResponse;
import com.fleencorp.feen.model.response.stream.review.DeleteStreamReviewResponse;
import com.fleencorp.feen.model.search.stream.review.StreamReviewSearchResult;
import com.fleencorp.feen.model.security.FleenUser;

public interface StreamReviewService {

  StreamReviewSearchResult findReviews(Long streamId, SearchRequest searchRequest);

  StreamReviewSearchResult findReviews(SearchRequest searchRequest, FleenUser user);

  AddStreamReviewResponse addReview(Long streamId, AddStreamReviewDto addStreamReviewDto, FleenUser user);

  DeleteStreamReviewResponse deleteReview(Long reviewId, FleenUser user);
}
