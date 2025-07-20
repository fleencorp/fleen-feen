package com.fleencorp.feen.softask.controller.vote;

import com.fleencorp.base.resolver.SearchParam;
import com.fleencorp.feen.exception.base.FailedOperationException;
import com.fleencorp.feen.softask.model.request.SoftAskSearchRequest;
import com.fleencorp.feen.softask.model.search.SoftAskVoteSearchResult;
import com.fleencorp.feen.softask.service.vote.SoftAskVoteSearchService;
import com.fleencorp.feen.user.model.security.RegisteredUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/softask/vote")
public class SoftAskVoteSearchController {

  private final SoftAskVoteSearchService softAskVoteSearchService;

  public SoftAskVoteSearchController(final SoftAskVoteSearchService softAskVoteSearchService) {
    this.softAskVoteSearchService = softAskVoteSearchService;
  }

  @Operation(summary = "Search for user votes",
    description = "Searches for votes made by the authenticated user based on the provided search criteria.")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Successfully retrieved vote search results",
      content = @Content(schema = @Schema(implementation = SoftAskVoteSearchResult.class))),
    @ApiResponse(responseCode = "400", description = "Failed operation",
      content = @Content(schema = @Schema(implementation = FailedOperationException.class)))
  })
  @GetMapping(value = "/find-user-votes")
  public SoftAskVoteSearchResult findUserVotes(
    @Parameter(description = "Search criteria for user votes", required = true)
      @SearchParam final SoftAskSearchRequest searchRequest,
    @Parameter(hidden = true)
      @AuthenticationPrincipal final RegisteredUser user) {
    return softAskVoteSearchService.findUserVotes(searchRequest, user);
  }
} 