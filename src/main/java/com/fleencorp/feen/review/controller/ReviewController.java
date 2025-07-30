package com.fleencorp.feen.review.controller;

import com.fleencorp.feen.common.exception.FailedOperationException;
import com.fleencorp.feen.stream.exception.core.StreamNotFoundException;
import com.fleencorp.feen.review.exception.core.CannotAddReviewIfStreamHasNotStartedException;
import com.fleencorp.feen.review.exception.core.ReviewNotFoundException;
import com.fleencorp.feen.review.model.dto.AddReviewDto;
import com.fleencorp.feen.review.model.dto.UpdateReviewDto;
import com.fleencorp.feen.review.model.response.ReviewAddResponse;
import com.fleencorp.feen.review.model.response.ReviewDeleteResponse;
import com.fleencorp.feen.review.model.response.ReviewUpdateResponse;
import com.fleencorp.feen.review.service.ReviewService;
import com.fleencorp.feen.user.model.security.RegisteredUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

  @Operation(summary = "Add a new review",
    description = "Creates a new review with the provided details. Requires full user authentication.")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Successfully added the review",
      content = @Content(schema = @Schema(implementation = ReviewAddResponse.class))),
    @ApiResponse(responseCode = "404", description = "Stream not found",
      content = @Content(schema = @Schema(implementation = StreamNotFoundException.class))),
    @ApiResponse(responseCode = "400", description = "Cannot add review if stream has not started",
      content = @Content(schema = @Schema(implementation = CannotAddReviewIfStreamHasNotStartedException.class))),
    @ApiResponse(responseCode = "400", description = "Failed operation",
      content = @Content(schema = @Schema(implementation = FailedOperationException.class)))
  })
  @PreAuthorize("isFullyAuthenticated()")
  @PostMapping(value = "/add")
  public ReviewAddResponse addReview(
    @Parameter(description = "Review details for creation", required = true)
      @Valid @RequestBody final AddReviewDto addReviewDto,
    @Parameter(hidden = true)
      @AuthenticationPrincipal final RegisteredUser user) {
    return reviewService.addReview(addReviewDto, user);
  }

  @Operation(summary = "Update an existing review",
    description = "Updates an existing review identified by its ID. Requires full user authentication and ownership of the review.")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Successfully updated the review",
      content = @Content(schema = @Schema(implementation = ReviewUpdateResponse.class))),
    @ApiResponse(responseCode = "404", description = "Review not found",
      content = @Content(schema = @Schema(implementation = ReviewNotFoundException.class))),
    @ApiResponse(responseCode = "404", description = "Stream not found",
      content = @Content(schema = @Schema(implementation = StreamNotFoundException.class))),
    @ApiResponse(responseCode = "400", description = "Cannot update review if stream has not started",
      content = @Content(schema = @Schema(implementation = CannotAddReviewIfStreamHasNotStartedException.class))),
    @ApiResponse(responseCode = "400", description = "Failed operation",
      content = @Content(schema = @Schema(implementation = FailedOperationException.class)))
  })
  @PreAuthorize("isFullyAuthenticated()")
  @PutMapping(value = "/update/{reviewId}")
  public ReviewUpdateResponse updateReview(
    @Parameter(description = "ID of the review to update", required = true)
      @PathVariable(name = "reviewId") final Long reviewId,
    @Parameter(description = "Updated review details", required = true)
      @Valid @RequestBody final UpdateReviewDto updateStreamReviewDto,
    @Parameter(hidden = true)
      @AuthenticationPrincipal final RegisteredUser user) {
    return reviewService.updateReview(reviewId, updateStreamReviewDto, user);
  }

  @Operation(summary = "Delete an existing review",
    description = "Deletes an existing review identified by its ID. Requires full user authentication and ownership of the review.")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Successfully deleted the review",
      content = @Content(schema = @Schema(implementation = ReviewDeleteResponse.class))),
    @ApiResponse(responseCode = "404", description = "Review not found",
      content = @Content(schema = @Schema(implementation = ReviewNotFoundException.class))),
    @ApiResponse(responseCode = "400", description = "Failed operation",
      content = @Content(schema = @Schema(implementation = FailedOperationException.class)))
  })
  @PreAuthorize("isFullyAuthenticated()")
  @DeleteMapping(value = "/delete/{reviewId}")
  public ReviewDeleteResponse deleteReview(
    @Parameter(description = "ID of the review to delete", required = true)
      @PathVariable(name = "reviewId") final Long reviewId,
    @Parameter(hidden = true)
      @AuthenticationPrincipal final RegisteredUser user) {
    return reviewService.deleteReview(reviewId, user);
  }
}
