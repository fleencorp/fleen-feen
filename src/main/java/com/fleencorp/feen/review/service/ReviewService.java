package com.fleencorp.feen.review.service;

import com.fleencorp.base.model.request.search.SearchRequest;
import com.fleencorp.feen.review.constant.ReviewParentType;
import com.fleencorp.feen.review.model.dto.AddReviewDto;
import com.fleencorp.feen.review.model.dto.UpdateReviewDto;
import com.fleencorp.feen.review.model.request.ReviewSearchRequest;
import com.fleencorp.feen.review.model.response.AddReviewResponse;
import com.fleencorp.feen.review.model.response.DeleteReviewResponse;
import com.fleencorp.feen.review.model.response.ReviewResponse;
import com.fleencorp.feen.review.model.response.UpdateReviewResponse;
import com.fleencorp.feen.review.model.search.ReviewSearchResult;
import com.fleencorp.feen.user.model.security.RegisteredUser;

public interface ReviewService {

  ReviewSearchResult findReviews(ReviewSearchRequest searchRequest, RegisteredUser user);

  ReviewResponse findMostRecentReview(ReviewParentType reviewParentType, Long entryId, RegisteredUser user);

  ReviewSearchResult findMyReviews(SearchRequest searchRequest, RegisteredUser user);

  AddReviewResponse addReview(AddReviewDto addReviewDto, RegisteredUser user);

  UpdateReviewResponse updateReview(Long reviewId, UpdateReviewDto updateStreamReviewDto, RegisteredUser user);

  DeleteReviewResponse deleteReview(Long reviewId, RegisteredUser user);

  Long incrementLikeCount(Long reviewId);

  Long decrementLikeCount(Long reviewId);
}
