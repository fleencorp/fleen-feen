package com.fleencorp.feen.review.service;

import com.fleencorp.feen.review.constant.ReviewParentType;
import com.fleencorp.feen.review.model.domain.Review;
import com.fleencorp.feen.review.model.holder.ReviewParentCountHolder;
import com.fleencorp.feen.review.model.response.base.ReviewResponse;
import com.fleencorp.feen.shared.member.contract.IsAMember;

import java.util.Collection;
import java.util.List;

public interface ReviewCommonService {

  void processReviewsOtherDetails(Collection<ReviewResponse> reviewResponses, IsAMember member);

  List<ReviewResponse> getMostRecentReviews(List<Review> reviews);

  Integer updateLikeCount(Long reviewId, boolean isLiked);

  Integer updateBookmarkCount(Long reviewId, boolean bookmarked);

  ReviewParentCountHolder getTotalReviewsByParent(ReviewParentType parentType, Collection<Long> parentIds);
}
