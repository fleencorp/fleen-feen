package com.fleencorp.feen.review.service;

import com.fleencorp.base.model.request.search.SearchRequest;
import com.fleencorp.feen.review.constant.ReviewParentType;
import com.fleencorp.feen.review.model.domain.Review;
import com.fleencorp.feen.review.model.request.ReviewSearchRequest;
import com.fleencorp.feen.review.model.response.base.ReviewResponse;
import com.fleencorp.feen.review.model.search.ReviewSearchResult;
import com.fleencorp.feen.shared.security.RegisteredUser;

public interface ReviewSearchService {

  Review findReview(Long reviewId);

  ReviewSearchResult findReviews(ReviewSearchRequest searchRequest, RegisteredUser user);

  ReviewResponse findMostRecentReview(ReviewParentType reviewParentType, Long entryId, RegisteredUser user);

  ReviewSearchResult findMyReviews(SearchRequest searchRequest, RegisteredUser user);
}
