package com.fleencorp.feen.review.service.impl;

import com.fleencorp.feen.review.model.domain.Review;
import com.fleencorp.feen.review.service.ReviewCommonService;
import com.fleencorp.feen.review.service.ReviewOperationService;
import com.fleencorp.feen.review.service.ReviewSearchService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class ReviewOperationServiceImpl implements ReviewOperationService {

  private final ReviewCommonService reviewCommonService;
  private final ReviewSearchService reviewSearchService;

  public ReviewOperationServiceImpl(
    final ReviewCommonService reviewCommonService,
    final ReviewSearchService reviewSearchService) {
    this.reviewCommonService = reviewCommonService;
    this.reviewSearchService = reviewSearchService;
  }

  @Override
  public Review findReview(final Long reviewId) {
    return reviewSearchService.findReview(reviewId);
  }

  @Override
  @Transactional
  public Integer updateLikeCount(final Long reviewId, final boolean isLiked) {
    return reviewCommonService.updateLikeCount(reviewId, isLiked);
  }

  @Override
  @Transactional
  public Integer updateBookmarkCount(final Long reviewId, final boolean isBookmarked) {
    return reviewCommonService.updateBookmarkCount(reviewId, isBookmarked);
  }
}
