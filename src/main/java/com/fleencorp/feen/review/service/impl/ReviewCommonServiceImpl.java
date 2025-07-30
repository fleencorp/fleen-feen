package com.fleencorp.feen.review.service.impl;

import com.fleencorp.feen.like.service.LikeService;
import com.fleencorp.feen.review.model.holder.ReviewParentCountHolder;
import com.fleencorp.feen.review.model.projection.ReviewParentCount;
import com.fleencorp.feen.review.constant.ReviewParentType;
import com.fleencorp.feen.review.mapper.ReviewMapper;
import com.fleencorp.feen.review.model.domain.Review;
import com.fleencorp.feen.review.model.response.base.ReviewResponse;
import com.fleencorp.feen.review.repository.ReviewRepository;
import com.fleencorp.feen.review.service.ReviewCommonService;
import com.fleencorp.feen.user.model.domain.Member;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

import static com.fleencorp.feen.common.service.impl.misc.MiscServiceImpl.setEntityUpdatableByUser;
import static com.fleencorp.feen.common.util.CommonUtil.allNonNull;
import static java.util.Objects.nonNull;

@Service
public class ReviewCommonServiceImpl implements ReviewCommonService {

  private final LikeService likeService;
  private final ReviewRepository reviewRepository;
  private final ReviewMapper reviewMapper;

  /**
   * Constructs a new {@code ReviewServiceImpl}, responsible for managing reviews and related interactions
   * such as likes on reviews within streams.
   *
   * @param likeService the service used to handle like operations on reviews (injected lazily to avoid circular dependencies)
   * @param reviewRepository the repository for performing CRUD operations on review entities
   * @param reviewMapper the mapper for converting between review entities and their corresponding DTOs
   */
  public ReviewCommonServiceImpl(
    @Lazy final LikeService likeService,
    final ReviewRepository reviewRepository,
    final ReviewMapper reviewMapper) {
    this.likeService = likeService;
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
  public void processReviewsOtherDetails(final Collection<ReviewResponse> reviewResponses, final Member member) {
    if (allNonNull(reviewResponses, member)) {
      reviewResponses.stream()
        .filter(Objects::nonNull)
        .forEach(reviewResponse -> {
          // Set the review is-updatable check
          setEntityUpdatableByUser(reviewResponse, member.getMemberId());
        });

      // Set the like info by the user if any
      likeService.populateLikesForReviews(reviewResponses, member);
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

  /**
   * Increments the like count of the review identified by the given ID.
   *
   * <p>This method delegates to the repository to atomically
   * increment the stored count, and returns the updated value.</p>
   *
   * @param reviewId the ID of the review to increment the like count for
   * @return the updated like count as a {@code Long}
   */
  @Override
  @Transactional
  public Long incrementLikeCount(final Long reviewId) {
    final int total = reviewRepository.incrementAndGetLikeCount(reviewId);
    return (long) total;
  }

  /**
   * Decrements the like count of the review identified by the given ID.
   *
   * <p>This method delegates the operation to the underlying
   * repository, which atomically decrements the stored count
   * and returns the updated total.</p>
   *
   * @param reviewId the ID of the review to decrement the like count for
   * @return the updated like count as a {@code Long}
   */
  @Override
  @Transactional
  public Long decrementLikeCount(final Long reviewId) {
    final int total = reviewRepository.decrementAndGetLikeCount(reviewId);
    return (long) total;
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
