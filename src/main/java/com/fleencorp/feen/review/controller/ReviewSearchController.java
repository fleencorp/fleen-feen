package com.fleencorp.feen.review.controller;

import com.fleencorp.base.model.request.search.SearchRequest;
import com.fleencorp.base.resolver.SearchParam;
import com.fleencorp.feen.common.exception.FailedOperationException;
import com.fleencorp.feen.review.model.request.ReviewSearchRequest;
import com.fleencorp.feen.review.model.search.ReviewSearchResult;
import com.fleencorp.feen.review.service.ReviewSearchService;
import com.fleencorp.feen.user.model.security.RegisteredUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/review")
public class ReviewSearchController {

  private final ReviewSearchService reviewSearchService;

  public ReviewSearchController(final ReviewSearchService reviewSearchService) {
    this.reviewSearchService = reviewSearchService;
  }

  @Operation(summary = "Retrieve reviews created by the authenticated user",
    description = "Fetches a paginated list of reviews authored by the currently authenticated user.")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Successfully retrieved user's reviews",
      content = @Content(schema = @Schema(implementation = ReviewSearchResult.class))),
    @ApiResponse(responseCode = "400", description = "Failed operation",
      content = @Content(schema = @Schema(implementation = FailedOperationException.class)))
  })
  @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'SUPER_ADMINISTRATOR', 'USER')")
  @GetMapping(value = "/mine")
  public ReviewSearchResult findMyReviews(
    @SearchParam final SearchRequest searchRequest,
      @AuthenticationPrincipal final RegisteredUser user) {
    return reviewSearchService.findMyReviews(searchRequest, user);
  }

  @Operation(summary = "Retrieve reviews for various entries (public access)",
    description = "Fetches a paginated list of reviews for different entry types, accessible publicly.")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Successfully retrieved reviews",
      content = @Content(schema = @Schema(implementation = ReviewSearchResult.class))),
    @ApiResponse(responseCode = "400", description = "Failed operation",
      content = @Content(schema = @Schema(implementation = FailedOperationException.class)))
  })
  @PostMapping(value = "/entries")
  public ReviewSearchResult findReviewsPublic(
    @SearchParam final ReviewSearchRequest searchRequest,
      @AuthenticationPrincipal final RegisteredUser user) {
    return reviewSearchService.findReviews(searchRequest, user);
  }
}
