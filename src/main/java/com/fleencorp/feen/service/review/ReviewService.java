package com.fleencorp.feen.service.review;

import com.fleencorp.base.model.request.search.SearchRequest;
import com.fleencorp.feen.model.dto.stream.review.AddReviewDto;
import com.fleencorp.feen.model.dto.stream.review.UpdateReviewDto;
import com.fleencorp.feen.model.response.review.AddReviewResponse;
import com.fleencorp.feen.model.response.review.DeleteReviewResponse;
import com.fleencorp.feen.model.response.review.ReviewResponse;
import com.fleencorp.feen.model.response.review.UpdateReviewResponse;
import com.fleencorp.feen.model.search.review.ReviewSearchResult;
import com.fleencorp.feen.model.security.FleenUser;

public interface ReviewService {

  ReviewSearchResult findReviewsPublic(Long streamId, SearchRequest searchRequest);

  ReviewResponse findMostRecentReview(Long streamId);

  ReviewSearchResult findReviewsPrivate(SearchRequest searchRequest, FleenUser user);

  AddReviewResponse addReview(Long streamId, AddReviewDto addReviewDto, FleenUser user);

  UpdateReviewResponse updateReview(Long streamId, Long reviewId, UpdateReviewDto updateStreamReviewDto, FleenUser user);

  DeleteReviewResponse deleteReview(Long reviewId, FleenUser user);
}
