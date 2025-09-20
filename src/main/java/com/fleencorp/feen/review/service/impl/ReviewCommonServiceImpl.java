package com.fleencorp.feen.review.service.impl;

import com.fleencorp.feen.bookmark.service.BookmarkOperationService;
import com.fleencorp.feen.like.service.LikeOperationService;
import com.fleencorp.feen.review.constant.ReviewParentType;
import com.fleencorp.feen.review.mapper.ReviewMapper;
import com.fleencorp.feen.review.model.domain.Review;
import com.fleencorp.feen.review.model.holder.ReviewParentCountHolder;
import com.fleencorp.feen.review.model.projection.ReviewParentCount;
import com.fleencorp.feen.review.model.response.base.ReviewResponse;
import com.fleencorp.feen.review.repository.ReviewRepository;
import com.fleencorp.feen.review.service.ReviewCommonService;
import com.fleencorp.feen.shared.member.contract.IsAMember;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

import static com.fleencorp.feen.common.service.impl.misc.MiscServiceImpl.setEntityUpdatableByUser;
import static com.fleencorp.feen.common.util.common.CommonUtil.allNonNull;
import static java.util.Objects.nonNull;

@Service
public class ReviewCommonServiceImpl implements ReviewCommonService {

  private final BookmarkOperationService bookmarkOperationService;
  private final LikeOperationService likeOperationService;
  private final ReviewRepository reviewRepository;
  private final ReviewMapper reviewMapper;

  public ReviewCommonServiceImpl(
    final BookmarkOperationService bookmarkOperationService,
    final LikeOperationService likeOperationService,
    final ReviewRepository reviewRepository,
    final ReviewMapper reviewMapper) {
    this.bookmarkOperationService = bookmarkOperationService;
    this.likeOperationService = likeOperationService;
    this.reviewRepository = reviewRepository;
    this.reviewMapper = reviewMapper;
  }

  /**
   * Populates additional metadata for each review response such as update eligibility and like status.
   *
   * <p>Each {@link ReviewResponse} is checked to determine if it can be updated by the current member,
   * and also enriched with the member’s like information if applicable.</p>
   *
   * @param reviewResponses the list of review responses to process
   * @param member the current member interacting with the reviews
   */
  @Override
  public void processReviewsOtherDetails(final Collection<ReviewResponse> reviewResponses, final IsAMember member) {
    if (allNonNull(reviewResponses, member)) {
      reviewResponses.stream()
        .filter(Objects::nonNull)
        .forEach(reviewResponse -> setEntityUpdatableByUser(reviewResponse, member.getMemberId()));

      bookmarkOperationService.populateBookmarkForReviews(reviewResponses, member);
      likeOperationService.populateLikesForReviews(reviewResponses, member);
    }
  }

  /**
   * Returns the most recent review from the provided list of reviews, if available,
   * by mapping it to a {@link ReviewResponse} using the {@code reviewMapper}.
   *
   * <p>If the input list is {@code null} or empty, an empty list is returned.</p>
   *
   * @param reviews the list of {@link Review} objects to process
   * @return a list containing a single {@link ReviewResponse} representing the most recent review,
   *         or an empty list if the input is {@code null} or contains no elements
   */
  @Override
  public List<ReviewResponse> getMostRecentReviews(final List<Review> reviews) {
    if (nonNull(reviews)) {
      return reviews.stream()
        .findFirst()
        .map(reviewMapper::toReviewResponsePublic)
        .stream()
        .toList();
    }

    return List.of();
  }

  private Integer decrementLikeCount(final Long reviewId) {
    reviewRepository.decrementAndGetLikeCount(reviewId);
    return reviewRepository.getLikeCount(reviewId);
  }

  private Integer incrementLikeCount(final Long reviewId) {
    reviewRepository.incrementAndGetLikeCount(reviewId);
    return reviewRepository.getLikeCount(reviewId);
  }

  @Override
  @Transactional
  public Integer updateLikeCount(final Long reviewId, final boolean isLiked) {
    return isLiked ? incrementLikeCount(reviewId) : decrementLikeCount(reviewId);
  }

  private Integer decrementBookmarkCount(final Long reviewId) {
    reviewRepository.decrementAndGetBookmarkCount(reviewId);
    return reviewRepository.getBookmarkCount(reviewId);
  }

  private Integer incrementBookmarkCount(final Long reviewId) {
    reviewRepository.incrementAndBookmarkCount(reviewId);
    return reviewRepository.getBookmarkCount(reviewId);
  }

  @Override
  @Transactional
  public Integer updateBookmarkCount(final Long reviewId, final boolean bookmarked) {
    return bookmarked
      ? incrementBookmarkCount(reviewId)
      : decrementBookmarkCount(reviewId);
  }

  /**
   * Retrieves the total number of reviews grouped by each parent ID for a given parent type.
   *
   * <p>This method queries the repository to count reviews associated with the specified
   * parent type and collection of parent IDs. It returns a {@link ReviewParentCountHolder}
   * containing the counts mapped to each corresponding parent ID.</p>
   *
   * @param parentType the type of the review parent (e.g., PRODUCT, SERVICE), must not be null
   * @param parentIds the collection of parent IDs to retrieve review counts for, must not be empty
   * @return a {@code ReviewParentCountHolder} containing review counts grouped by parent ID
   */
  @Override
  public ReviewParentCountHolder getTotalReviewsByParent(final ReviewParentType parentType, final Collection<Long> parentIds) {
    final List<ReviewParentCount> reviewParentCounts = reviewRepository.countReviewsGroupedByParentId(parentType, parentIds);
    return ReviewParentCountHolder.of(reviewParentCounts);
  }
}
