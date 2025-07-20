package com.fleencorp.feen.review.service;

import com.fleencorp.feen.review.model.holder.ReviewParentCountHolder;
import com.fleencorp.feen.review.constant.ReviewParentType;
import com.fleencorp.feen.review.model.domain.Review;
import com.fleencorp.feen.review.model.response.base.ReviewResponse;
import com.fleencorp.feen.user.model.domain.Member;

import java.util.Collection;
import java.util.List;

public interface ReviewCommonService {

  void processReviewsOtherDetails(Collection<ReviewResponse> reviewResponses, Member member);

  List<ReviewResponse> getMostRecentReviews(List<Review> reviews);

  Long incrementLikeCount(Long reviewId);

  Long decrementLikeCount(Long reviewId);

  ReviewParentCountHolder getTotalReviewsByParent(ReviewParentType parentType, Collection<Long> parentIds);
}
