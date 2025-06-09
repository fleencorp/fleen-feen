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
import com.fleencorp.feen.user.security.RegisteredUser;

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
