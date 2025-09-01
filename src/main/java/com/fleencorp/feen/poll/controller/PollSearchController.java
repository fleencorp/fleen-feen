package com.fleencorp.feen.poll.controller;

import com.fleencorp.base.resolver.SearchParam;
import com.fleencorp.feen.common.exception.FailedOperationException;
import com.fleencorp.feen.poll.exception.poll.PollNotFoundException;
import com.fleencorp.feen.poll.model.request.PollSearchRequest;
import com.fleencorp.feen.poll.model.response.PollRetrieveResponse;
import com.fleencorp.feen.poll.model.search.ChatSpacePollSearchResult;
import com.fleencorp.feen.poll.model.search.PollSearchResult;
import com.fleencorp.feen.poll.model.search.StreamPollSearchResult;
import com.fleencorp.feen.poll.service.PollSearchService;
import com.fleencorp.feen.shared.security.RegisteredUser;
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
@RequestMapping(value = "/api/poll")
public class PollSearchController {

  private final PollSearchService pollSearchService;

  public PollSearchController(final PollSearchService pollSearchService) {
    this.pollSearchService = pollSearchService;
  }

  @Operation(summary = "Retrieve a specific poll",
    description = "Fetches the details of a poll by its ID.")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Successfully retrieved the poll",
      content = @Content(schema = @Schema(implementation = PollRetrieveResponse.class))),
    @ApiResponse(responseCode = "404", description = "Poll not found",
      content = @Content(schema = @Schema(implementation = PollNotFoundException.class))),
    @ApiResponse(responseCode = "400", description = "Failed operation",
      content = @Content(schema = @Schema(implementation = FailedOperationException.class)))
  })
  @GetMapping(value = "/detail/{pollId}")
  public PollRetrieveResponse findPoll(
    @Parameter(description = "ID of the poll to retrieve", required = true)
      @PathVariable(name = "pollId") final Long pollId,
    @Parameter(hidden = true)
      @AuthenticationPrincipal final RegisteredUser user) {
    return pollSearchService.findPoll(pollId, user);
  }

  @Operation(summary = "Search for polls",
    description = "Searches for polls based on the provided search criteria.")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Successfully retrieved search results",
      content = @Content(schema = @Schema(implementation = PollSearchResult.class))),
    @ApiResponse(responseCode = "400", description = "Failed operation",
      content = @Content(schema = @Schema(implementation = FailedOperationException.class)))
  })
  @GetMapping(value = "/find-polls")
  public PollSearchResult findPolls(
    @Parameter(description = "Search criteria for polls", required = true)
      @SearchParam final PollSearchRequest searchRequest,
    @Parameter(hidden = true)
      @AuthenticationPrincipal final RegisteredUser user) {
    return pollSearchService.findPolls(searchRequest, user);
  }

  @Operation(summary = "Search for polls in a chat space",
    description = "Searches for polls associated with a chat space based on the provided search criteria.")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Successfully retrieved chat space poll search results",
      content = @Content(schema = @Schema(implementation = ChatSpacePollSearchResult.class))),
    @ApiResponse(responseCode = "400", description = "Failed operation",
      content = @Content(schema = @Schema(implementation = FailedOperationException.class)))
  })
  @GetMapping(value = "/find-chat-space-polls")
  public ChatSpacePollSearchResult findChatSpacePolls(
    @Parameter(description = "Search criteria for chat space polls", required = true)
      @SearchParam final PollSearchRequest searchRequest,
    @Parameter(hidden = true)
      @AuthenticationPrincipal final RegisteredUser user) {
    return pollSearchService.findChatSpacePolls(searchRequest, user);
  }

  @Operation(summary = "Search for polls in a stream",
    description = "Searches for polls associated with a stream based on the provided search criteria.")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Successfully retrieved stream poll search results",
      content = @Content(schema = @Schema(implementation = StreamPollSearchResult.class))),
    @ApiResponse(responseCode = "400", description = "Failed operation",
      content = @Content(schema = @Schema(implementation = FailedOperationException.class)))
  })
  @GetMapping(value = "/find-stream-polls")
  public StreamPollSearchResult findStreamPolls(
    @Parameter(description = "Search criteria for stream polls", required = true)
      @SearchParam final PollSearchRequest searchRequest,
    @Parameter(hidden = true)
      @AuthenticationPrincipal final RegisteredUser user) {
    return pollSearchService.findStreamPolls(searchRequest, user);
  }

  @Operation(summary = "Search for polls created by the user",
    description = "Searches for polls created by the authenticated user based on the provided search criteria.")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Successfully retrieved user-created poll search results",
      content = @Content(schema = @Schema(implementation = PollSearchResult.class))),
    @ApiResponse(responseCode = "400", description = "Failed operation",
      content = @Content(schema = @Schema(implementation = FailedOperationException.class)))
  })
  @GetMapping(value = "/find-my-polls")
  public PollSearchResult findMyPolls(
    @Parameter(description = "Search criteria for user-created polls", required = true)
      @SearchParam final PollSearchRequest searchRequest,
    @Parameter(hidden = true)
      @AuthenticationPrincipal final RegisteredUser user) {
    return pollSearchService.findMyPolls(searchRequest, user);
  }
}
