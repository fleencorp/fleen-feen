package com.fleencorp.feen.poll.controller;

import com.fleencorp.base.resolver.SearchParam;
import com.fleencorp.feen.common.exception.FailedOperationException;
import com.fleencorp.feen.poll.exception.option.PollOptionNotFoundException;
import com.fleencorp.feen.poll.exception.poll.PollNotFoundException;
import com.fleencorp.feen.poll.exception.vote.PollVotingNoMultipleChoiceException;
import com.fleencorp.feen.poll.exception.vote.PollVotingNotAllowedPollDeletedException;
import com.fleencorp.feen.poll.exception.vote.PollVotingNotAllowedPollEndedException;
import com.fleencorp.feen.poll.exception.vote.PollVotingNotAllowedPollNoOptionException;
import com.fleencorp.feen.poll.model.dto.VotePollDto;
import com.fleencorp.feen.poll.model.request.PollVoteSearchRequest;
import com.fleencorp.feen.poll.model.response.core.PollVoteResponse;
import com.fleencorp.feen.poll.model.search.PollVoteSearchResult;
import com.fleencorp.feen.poll.service.PollVoteService;
import com.fleencorp.feen.shared.security.RegisteredUser;
import com.fleencorp.feen.user.exception.member.MemberNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/poll")
public class PollVoteController {

  private final PollVoteService pollVoteService;

  public PollVoteController(final PollVoteService pollVoteService) {
    this.pollVoteService = pollVoteService;
  }

  @Operation(summary = "Search for poll votes",
    description = "Searches for poll votes based on the provided search criteria. Requires user authentication.")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Successfully retrieved search results",
      content = @Content(schema = @Schema(implementation = PollVoteSearchResult.class))),
    @ApiResponse(responseCode = "400", description = "Failed operation",
      content = @Content(schema = @Schema(implementation = FailedOperationException.class)))
  })
  @GetMapping(value = "/find-votes/{pollId}")
  public PollVoteSearchResult findVotes(
    @Parameter(description = "ID of the poll to retrieve votes for", required = true)
      @PathVariable(name = "pollId") final Long pollId,
    @Parameter(description = "Search criteria for polls", required = true)
      @SearchParam final PollVoteSearchRequest searchRequest) {
    return pollVoteService.findVotes(pollId, searchRequest);
  }

  @Operation(summary = "Vote on a poll",
    description = "Submits a vote for a poll. Requires user authentication and valid poll conditions.")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Successfully voted on the poll",
      content = @Content(schema = @Schema(implementation = PollVoteResponse.class))),
    @ApiResponse(responseCode = "404", description = "Member not found",
      content = @Content(schema = @Schema(implementation = MemberNotFoundException.class))),
    @ApiResponse(responseCode = "404", description = "Poll not found",
      content = @Content(schema = @Schema(implementation = PollNotFoundException.class))),
    @ApiResponse(responseCode = "404", description = "Poll option not found",
      content = @Content(schema = @Schema(implementation = PollOptionNotFoundException.class))),
    @ApiResponse(responseCode = "400", description = "Voting not allowed because the poll is deleted",
      content = @Content(schema = @Schema(implementation = PollVotingNotAllowedPollDeletedException.class))),
    @ApiResponse(responseCode = "400", description = "Voting not allowed because the poll has ended",
      content = @Content(schema = @Schema(implementation = PollVotingNotAllowedPollEndedException.class))),
    @ApiResponse(responseCode = "400", description = "Voting not allowed because the poll has no options",
      content = @Content(schema = @Schema(implementation = PollVotingNotAllowedPollNoOptionException.class))),
    @ApiResponse(responseCode = "400", description = "Voting not allowed because the poll does not support multiple-choice voting",
      content = @Content(schema = @Schema(implementation = PollVotingNoMultipleChoiceException.class)))
  })
  @PostMapping(value = "/vote/{pollId}")
  public PollVoteResponse votePoll(
      @Parameter(description = "ID of the poll to vote on", required = true)
        @PathVariable(name = "pollId") final Long pollId,
      @Parameter(description = "Vote details", required = true)
        @Valid @RequestBody final VotePollDto votePollDto,
      @Parameter(hidden = true)
        @AuthenticationPrincipal final RegisteredUser user) {
    return pollVoteService.votePoll(pollId, votePollDto, user);
  }
}
