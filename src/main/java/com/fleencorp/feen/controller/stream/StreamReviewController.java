package com.fleencorp.feen.controller.stream;

import com.fleencorp.base.model.request.search.SearchRequest;
import com.fleencorp.base.resolver.SearchParam;
import com.fleencorp.feen.model.dto.stream.review.AddStreamReviewDto;
import com.fleencorp.feen.model.response.stream.review.AddStreamReviewResponse;
import com.fleencorp.feen.model.response.stream.review.DeleteStreamReviewResponse;
import com.fleencorp.feen.model.search.stream.review.StreamReviewSearchResult;
import com.fleencorp.feen.model.security.FleenUser;
import com.fleencorp.feen.service.stream.review.StreamReviewService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/stream/review")
public class StreamReviewController {

  private final StreamReviewService streamReviewService;

  public StreamReviewController(final StreamReviewService streamReviewService) {
    this.streamReviewService = streamReviewService;
  }

  @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'SUPER_ADMINISTRATOR', 'USER')")
  @GetMapping(value = "/mine")
  public StreamReviewSearchResult findReviews(
      @SearchParam final SearchRequest searchRequest,
      @AuthenticationPrincipal final FleenUser user) {
    return streamReviewService.findReviews(searchRequest, user);
  }

  @GetMapping(value = "/entries/{streamId}")
  public StreamReviewSearchResult findReviews(
      @PathVariable(name = "streamId") final Long streamId,
      @SearchParam final SearchRequest searchRequest) {
    return streamReviewService.findReviews(streamId, searchRequest);
  }

  @PreAuthorize("isFullyAuthenticated()")
  @PostMapping(value = "/add/{streamId}")
  public AddStreamReviewResponse addReview(
      @PathVariable(name = "streamId") final Long streamId,
      @Valid @RequestBody final AddStreamReviewDto addStreamReviewDto,
      @AuthenticationPrincipal final FleenUser user) {
    return streamReviewService.addReview(streamId, addStreamReviewDto, user);
  }

  @PreAuthorize("isFullyAuthenticated()")
  @DeleteMapping(value = "/delete/{reviewId}")
  public DeleteStreamReviewResponse deleteReview(
      @PathVariable(name = "reviewId") final Long reviewId,
      @AuthenticationPrincipal final FleenUser user) {
    return streamReviewService.deleteReview(reviewId, user);
  }
}
