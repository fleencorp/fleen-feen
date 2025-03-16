package com.fleencorp.feen.controller.review;

import com.fleencorp.base.model.request.search.SearchRequest;
import com.fleencorp.base.resolver.SearchParam;
import com.fleencorp.feen.model.dto.stream.review.AddReviewDto;
import com.fleencorp.feen.model.dto.stream.review.UpdateReviewDto;
import com.fleencorp.feen.model.request.search.review.ReviewSearchRequest;
import com.fleencorp.feen.model.response.review.AddReviewResponse;
import com.fleencorp.feen.model.response.review.DeleteReviewResponse;
import com.fleencorp.feen.model.response.review.UpdateReviewResponse;
import com.fleencorp.feen.model.search.review.ReviewSearchResult;
import com.fleencorp.feen.model.security.FleenUser;
import com.fleencorp.feen.service.review.ReviewService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/review")
public class ReviewController {

  private final ReviewService reviewService;

  public ReviewController(final ReviewService reviewService) {
    this.reviewService = reviewService;
  }

  @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'SUPER_ADMINISTRATOR', 'USER')")
  @GetMapping(value = "/mine")
  public ReviewSearchResult findReviewsPrivate(
      @SearchParam final SearchRequest searchRequest,
      @AuthenticationPrincipal final FleenUser user) {
    return reviewService.findReviewsPrivate(searchRequest, user);
  }

  @PostMapping(value = "/entries")
  public ReviewSearchResult findReviewsPublic(
      @SearchParam final ReviewSearchRequest searchRequest,
      @AuthenticationPrincipal final FleenUser user) {
    return reviewService.findReviewsPublic(searchRequest, user);
  }

  @PreAuthorize("isFullyAuthenticated()")
  @PostMapping(value = "/add")
  public AddReviewResponse addReview(
      @Valid @RequestBody final AddReviewDto addReviewDto,
      @AuthenticationPrincipal final FleenUser user) {
    return reviewService.addReview(addReviewDto, user);
  }

  @PreAuthorize("isFullyAuthenticated()")
  @PutMapping(value = "/update/{reviewId}")
  public UpdateReviewResponse updateReview(
      @PathVariable(name = "reviewId") final Long reviewId,
      @Valid @RequestBody final UpdateReviewDto updateStreamReviewDto,
      @AuthenticationPrincipal final FleenUser user) {
    return reviewService.updateReview(reviewId, updateStreamReviewDto, user);
  }

  @PreAuthorize("isFullyAuthenticated()")
  @DeleteMapping(value = "/delete/{reviewId}")
  public DeleteReviewResponse deleteReview(
      @PathVariable(name = "reviewId") final Long reviewId,
      @AuthenticationPrincipal final FleenUser user) {
    return reviewService.deleteReview(reviewId, user);
  }
}
