package com.fleencorp.feen.review.service;

import com.fleencorp.base.model.request.search.SearchRequest;
import com.fleencorp.feen.review.constant.ReviewParentType;
import com.fleencorp.feen.review.model.request.ReviewSearchRequest;
import com.fleencorp.feen.review.model.response.base.ReviewResponse;
import com.fleencorp.feen.review.model.search.ReviewSearchResult;
import com.fleencorp.feen.user.model.security.RegisteredUser;

public interface ReviewSearchService {

  ReviewSearchResult findReviews(ReviewSearchRequest searchRequest, RegisteredUser user);

  ReviewResponse findMostRecentReview(ReviewParentType reviewParentType, Long entryId, RegisteredUser user);

  ReviewSearchResult findMyReviews(SearchRequest searchRequest, RegisteredUser user);
}
