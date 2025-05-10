package com.fleencorp.feen.service.review;

import com.fleencorp.base.model.request.search.SearchRequest;
import com.fleencorp.feen.constant.review.ReviewParentType;
import com.fleencorp.feen.model.dto.review.AddReviewDto;
import com.fleencorp.feen.model.dto.review.UpdateReviewDto;
import com.fleencorp.feen.model.request.search.review.ReviewSearchRequest;
import com.fleencorp.feen.model.response.review.AddReviewResponse;
import com.fleencorp.feen.model.response.review.DeleteReviewResponse;
import com.fleencorp.feen.model.response.review.ReviewResponse;
import com.fleencorp.feen.model.response.review.UpdateReviewResponse;
import com.fleencorp.feen.model.search.review.ReviewSearchResult;
import com.fleencorp.feen.model.security.FleenUser;

public interface ReviewService {

  ReviewSearchResult findReviews(ReviewSearchRequest searchRequest, FleenUser user);

  ReviewResponse findMostRecentReview(ReviewParentType reviewParentType, Long entryId, FleenUser user);

  ReviewSearchResult findMyReviews(SearchRequest searchRequest, FleenUser user);

  AddReviewResponse addReview(AddReviewDto addReviewDto, FleenUser user);

  UpdateReviewResponse updateReview(Long reviewId, UpdateReviewDto updateStreamReviewDto, FleenUser user);

  DeleteReviewResponse deleteReview(Long reviewId, FleenUser user);

  Long incrementLikeCount(Long reviewId);

  Long decrementLikeCount(Long reviewId);
}
