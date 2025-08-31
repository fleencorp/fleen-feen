package com.fleencorp.feen.review.service;

import com.fleencorp.feen.review.model.domain.Review;

public interface ReviewOperationService {

  Review findReview(Long reviewId);

  Integer updateLikeCount(Long reviewId, boolean isLiked);

  Integer updateBookmarkCount(Long reviewId, boolean isBookmarked);
}
