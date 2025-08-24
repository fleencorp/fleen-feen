package com.fleencorp.feen.softask.controller.reply;

import com.fleencorp.base.resolver.SearchParam;
import com.fleencorp.feen.common.exception.FailedOperationException;
import com.fleencorp.feen.softask.exception.core.SoftAskReplyNotFoundException;
import com.fleencorp.feen.softask.model.request.SoftAskSearchRequest;
import com.fleencorp.feen.softask.model.response.reply.SoftAskReplyRetrieveResponse;
import com.fleencorp.feen.softask.model.search.SoftAskReplySearchResult;
import com.fleencorp.feen.softask.service.reply.SoftAskReplySearchService;
import com.fleencorp.feen.user.model.security.RegisteredUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/soft-ask/reply")
public class SoftAskReplySearchController {

  private final SoftAskReplySearchService softAskReplySearchService;

  public SoftAskReplySearchController(final SoftAskReplySearchService softAskReplySearchService) {
    this.softAskReplySearchService = softAskReplySearchService;
  }

  @Operation(summary = "Retrieve a specific soft ask reply",
    description = "Fetches the details of a soft ask reply by its ID.")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Successfully retrieved the reply",
      content = @Content(schema = @Schema(implementation = SoftAskReplyRetrieveResponse.class))),
    @ApiResponse(responseCode = "404", description = "Reply not found",
      content = @Content(schema = @Schema(implementation = SoftAskReplyNotFoundException.class))),
    @ApiResponse(responseCode = "400", description = "Failed operation",
      content = @Content(schema = @Schema(implementation = FailedOperationException.class)))
  })
  @GetMapping(value = "/detail/{softAskId}/{replyId}")
  public SoftAskReplyRetrieveResponse findSoftAskReply(
    @Parameter(description = "ID of the soft ask parent", required = true)
      @PathVariable(name = "softAskId") final Long softAskId,
    @Parameter(description = "ID of the reply to retrieve", required = true)
      @PathVariable(name = "replyId") final Long replyId) {
    return softAskReplySearchService.retrieveSoftAskReply(softAskId, replyId);
  }

  @Operation(summary = "Search for soft ask replies",
    description = "Searches for soft ask replies based on the provided search criteria.")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Successfully retrieved search results",
      content = @Content(schema = @Schema(implementation = SoftAskReplySearchResult.class))),
    @ApiResponse(responseCode = "400", description = "Failed operation",
      content = @Content(schema = @Schema(implementation = FailedOperationException.class)))
  })
  @GetMapping(value = "/find-replies")
  public SoftAskReplySearchResult findSoftAskReplies(
    @Parameter(description = "Search criteria for replies", required = true)
      @SearchParam final SoftAskSearchRequest searchRequest,
    @Parameter(hidden = true)
      @AuthenticationPrincipal final RegisteredUser user) {
    return softAskReplySearchService.findSoftAskReplies(searchRequest, user);
  }
} 