package com.fleencorp.feen.like.controller;

import com.fleencorp.base.resolver.SearchParam;
import com.fleencorp.feen.chat.space.exception.core.ChatSpaceNotFoundException;
import com.fleencorp.feen.common.exception.FailedOperationException;
import com.fleencorp.feen.like.model.dto.LikeDto;
import com.fleencorp.feen.like.model.request.search.LikeSearchRequest;
import com.fleencorp.feen.like.model.response.LikeCreateResponse;
import com.fleencorp.feen.like.model.search.LikeSearchResult;
import com.fleencorp.feen.like.service.LikeSearchService;
import com.fleencorp.feen.like.service.LikeService;
import com.fleencorp.feen.softask.exception.core.SoftAskNotFoundException;
import com.fleencorp.feen.softask.exception.core.SoftAskReplyNotFoundException;
import com.fleencorp.feen.stream.exception.core.StreamNotFoundException;
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
@RequestMapping(value = "/api/like")
public class LikeController {

  private final LikeSearchService likeSearchService;
  private final LikeService likeService;

  public LikeController(
      final LikeSearchService likeSearchService,
      final LikeService likeService) {
    this.likeSearchService = likeSearchService;
    this.likeService = likeService;
  }

  @Operation(summary = "Like a entity",
    description = "Creates a like for a specific entity. Requires full user authentication.")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Successfully likeed the chat",
      content = @Content(schema = @Schema(implementation = LikeCreateResponse.class))),
    @ApiResponse(responseCode = "404", description = "Chat space not found",
      content = @Content(schema = @Schema(implementation = ChatSpaceNotFoundException.class))),
    @ApiResponse(responseCode = "404", description = "Soft Ask not found",
      content = @Content(schema = @Schema(implementation = SoftAskNotFoundException.class))),
    @ApiResponse(responseCode = "404", description = "Soft Ask Reply not found",
      content = @Content(schema = @Schema(implementation = SoftAskReplyNotFoundException.class))),
    @ApiResponse(responseCode = "404", description = "Stream not found",
      content = @Content(schema = @Schema(implementation = StreamNotFoundException.class))),
    @ApiResponse(responseCode = "400", description = "Failed operation",
      content = @Content(schema = @Schema(implementation = FailedOperationException.class)))
  })
  @PreAuthorize("isFullyAuthenticated()")
  @PostMapping(value = "")
  public LikeCreateResponse like(
      @Valid @RequestBody final LikeDto likeDto,
      @AuthenticationPrincipal final RegisteredUser user) {
    return likeService.like(likeDto, user);
  }

  @Operation(summary = "Find likes",
    description = "Searches for and retrieves a list of likes based on specified search criteria.")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Successfully found likes",
      content = @Content(schema = @Schema(implementation = LikeSearchResult.class)))
  })
  @GetMapping(value = "/entries")
  public LikeSearchResult findLikes(
    @Parameter(description = "Search criteria for likes")
    @SearchParam final LikeSearchRequest searchRequest,
    @Parameter(hidden = true)
    @AuthenticationPrincipal final RegisteredUser user) {
    return likeSearchService.findLikes(searchRequest, user);
  }
}
