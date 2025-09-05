package com.fleencorp.feen.review.service.impl;

import com.fleencorp.base.model.request.search.SearchRequest;
import com.fleencorp.base.model.view.search.SearchResult;
import com.fleencorp.feen.review.constant.ReviewParentType;
import com.fleencorp.feen.review.exception.core.ReviewNotFoundException;
import com.fleencorp.feen.review.mapper.ReviewMapper;
import com.fleencorp.feen.review.model.domain.Review;
import com.fleencorp.feen.review.model.request.ReviewSearchRequest;
import com.fleencorp.feen.review.model.response.base.ReviewResponse;
import com.fleencorp.feen.review.model.search.ReviewSearchResult;
import com.fleencorp.feen.review.repository.ReviewRepository;
import com.fleencorp.feen.review.service.ReviewCommonService;
import com.fleencorp.feen.review.service.ReviewSearchService;
import com.fleencorp.feen.shared.security.RegisteredUser;
import com.fleencorp.feen.user.model.domain.Member;
import com.fleencorp.localizer.service.Localizer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.fleencorp.base.util.FleenUtil.toSearchResult;
import static java.util.Objects.nonNull;

@Service
public class ReviewSearchServiceImpl implements ReviewSearchService {

  private final ReviewCommonService reviewCommonService;
  private final ReviewRepository reviewRepository;
  private final ReviewMapper reviewMapper;
  private final Localizer localizer;

  /**
   * Constructs a new {@code ReviewServiceImpl}, responsible for managing reviews and related interactions
   * such as likes on reviews within streams.
   *
   * @param reviewCommonService the service used to handle common review operations
   * @param reviewRepository the repository for performing CRUD operations on review entities
   * @param reviewMapper the mapper for converting between review entities and their corresponding DTOs
   * @param localizer the utility for resolving localized text for responses and messages
   */
  public ReviewSearchServiceImpl(
    final ReviewCommonService reviewCommonService,
    final ReviewRepository reviewRepository,
    final ReviewMapper reviewMapper,
    final Localizer localizer) {
    this.reviewCommonService = reviewCommonService;
    this.reviewRepository = reviewRepository;
    this.reviewMapper = reviewMapper;
    this.localizer = localizer;
  }

  /**
   * Finds a {@link Review} by its ID.
   *
   * <p>The method queries the repository for the given review ID and returns the
   * corresponding {@link Review}. If the review does not exist, a
   * {@link ReviewNotFoundException} is thrown.</p>
   *
   * @param reviewId the ID of the review to find
   * @return the {@link Review} with the given ID
   * @throws ReviewNotFoundException if no review exists with the given ID
   */
  @Override
  public Review findReview(final Long reviewId) {
    return reviewRepository.findById(reviewId)
      .orElseThrow(ReviewNotFoundException.of(reviewId));
  }

  /**
   * Retrieves a paginated list of public reviews for the specified stream or event.
   *
   * <p>This method performs a search for reviews associated with the given stream ID, based on the provided
   * {@link SearchRequest}. It converts the retrieved reviews into a list of {@link ReviewResponse}, checks if any
   * of the reviews are updatable by the current user, and includes pagination details in the result.</p>
   *
   * <p>If the user has authored any of the reviews, those reviews will be marked as updatable.</p>
   *
   * @param searchRequest the pagination and sorting information for the review search
   * @param user the {@link RegisteredUser} to check ownership of reviews and mark them as updatable if applicable
   * @return a {@link ReviewSearchResult} containing the reviews and pagination details, or an empty result if no reviews are found
   */
  @Override
  public ReviewSearchResult findReviews(final ReviewSearchRequest searchRequest, final RegisteredUser user) {
    final Long parentId = searchRequest.getParentId();
    final Pageable pageable = searchRequest.getPage();
    Page<Review> page = Page.empty();

    // Check if a stream review search request
    if (searchRequest.isStreamReviewSearchRequest()) {
      page = reviewRepository.findByStreamId(parentId, pageable);
    }

    // Convert and process the reviews to responses
    return processAndReturnReviews(page, user.toMember());
  }

  /**
   * Retrieves the most recent review for the specified stream or event.
   *
   * <p>This method looks up the most recent review associated with the given stream ID. If a review is found,
   * it is mapped to a {@link ReviewResponse} and returned. If no review is found, {@code null} is returned.
   * Additionally, if the review was created by the current user, it will be marked as updatable.</p>
   *
   * @param reviewParentType the type of reviews to find
   * @param parentId the ID of the parent which review is a child of
   * @param user the {@link RegisteredUser} whose ownership of the review is being verified for updatability
   * @return the {@link ReviewResponse} for the most recent review, or {@code null} if no review is found
   */
  @Override
  public ReviewResponse findMostRecentReview(final ReviewParentType reviewParentType, final Long parentId, final RegisteredUser user) {
    final PageRequest pageRequest = PageRequest.of(0, 1);
    List<Review> reviews = new ArrayList<>();

    if (ReviewParentType.isStream(reviewParentType)) {
      reviews = reviewRepository.findMostRecentReviewByStream(parentId, pageRequest);
    }

    final List<ReviewResponse> reviewResponses = reviewCommonService.getMostRecentReviews(reviews);
    reviewCommonService.processReviewsOtherDetails(reviewResponses, user.toMember());

    return reviewResponses.isEmpty() ? null : reviewResponses.getFirst();
  }

  /**
   * Retrieves stream reviews submitted by a specific user.
   *
   * <p>This method fetches a paginated list of reviews associated with the user,
   * converting them into response views suitable for presentation.</p>
   *
   * @param searchRequest contains pagination and search parameters for retrieving the reviews
   * @param user the user whose reviews are being retrieved
   * @return a {@link ReviewSearchResult} containing the paginated list of reviews in response format
   */
  @Override
  public ReviewSearchResult findMyReviews(final SearchRequest searchRequest, final RegisteredUser user) {
    final Page<Review> page = reviewRepository.findByMember(user.toMember(), searchRequest.getPage());
    return processAndReturnReviews(page, user.toMember());
  }

  /**
   * Processes a page of reviews and returns a localized search result.
   *
   * <p>This method converts each {@link Review} in the page to a {@link ReviewResponse}, processes
   * additional review details, wraps the responses in a {@link SearchResult}, and then returns
   * a localized {@link ReviewSearchResult}.</p>
   *
   * @param page the paginated list of reviews
   * @param member the current member viewing the reviews
   * @return a localized {@link ReviewSearchResult}, or an empty result if input is null
   */
  protected ReviewSearchResult processAndReturnReviews(final Page<Review> page, final Member member) {
    if (nonNull(page)) {
      final Collection<ReviewResponse> reviewResponses = reviewMapper.toReviewResponsesPublic(page.getContent());


      final SearchResult searchResult = toSearchResult(reviewResponses, page);
      final ReviewSearchResult reviewSearchResult = ReviewSearchResult.of(searchResult);

      return localizer.of(reviewSearchResult);
    }

    return ReviewSearchResult.empty();
  }

}
