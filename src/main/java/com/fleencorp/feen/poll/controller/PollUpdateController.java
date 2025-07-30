package com.fleencorp.feen.poll.controller;

import com.fleencorp.feen.common.exception.FailedOperationException;
import com.fleencorp.feen.chat.space.exception.core.NotAnAdminOfChatSpaceException;
import com.fleencorp.feen.poll.exception.option.PollUpdateCantChangeOptionsException;
import com.fleencorp.feen.poll.exception.poll.*;
import com.fleencorp.feen.poll.model.dto.UpdatePollDto;
import com.fleencorp.feen.poll.model.response.PollUpdateResponse;
import com.fleencorp.feen.poll.service.PollUpdateService;
import com.fleencorp.feen.user.model.security.RegisteredUser;
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
public class PollUpdateController {

  private final PollUpdateService pollUpdateService;

  public PollUpdateController(final PollUpdateService pollUpdateService) {
    this.pollUpdateService = pollUpdateService;
  }

  @Operation(summary = "Update an existing poll",
    description = "Updates the details of an existing poll. Requires user authentication and appropriate permissions.")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Successfully updated the poll",
      content = @Content(schema = @Schema(implementation = PollUpdateResponse.class))),
    @ApiResponse(responseCode = "404", description = "Poll not found",
      content = @Content(schema = @Schema(implementation = PollNotFoundException.class))),
    @ApiResponse(responseCode = "403", description = "Cannot change question after votes is casted",
      content = @Content(schema = @Schema(implementation = PollUpdateCantChangeQuestionException.class))),
    @ApiResponse(responseCode = "403", description = "Cannot change multiple choice option after votes is casted",
      content = @Content(schema = @Schema(implementation = PollUpdateCantChangeMultipleChoiceException.class))),
    @ApiResponse(responseCode = "403", description = "Cannot change poll options texts after votes is casted",
      content = @Content(schema = @Schema(implementation = PollUpdateCantChangeOptionsException.class))),
    @ApiResponse(responseCode = "403", description = "Cannot change visibility after votes is casted",
      content = @Content(schema = @Schema(implementation = PollUpdateCantChangeVisibilityException.class))),
    @ApiResponse(responseCode = "403", description = "Cannot change anonymity of poll",
      content = @Content(schema = @Schema(implementation = PollUpdateCantChangeAnonymityException.class))),
    @ApiResponse(responseCode = "403", description = "Unauthorized to update poll",
      content = @Content(schema = @Schema(implementation = PollUpdateUnauthorizedException.class))),
    @ApiResponse(responseCode = "403", description = "Not an admin of chat space",
      content = @Content(schema = @Schema(implementation = NotAnAdminOfChatSpaceException.class))),
    @ApiResponse(responseCode = "400", description = "Failed operation",
      content = @Content(schema = @Schema(implementation = FailedOperationException.class)))
  })
  @PutMapping(value = "/update/{pollId}")
  public PollUpdateResponse updatePoll(
    @Parameter(description = "ID of the poll to update", required = true)
      @PathVariable(name = "pollId") final Long pollId,
    @Parameter(description = "Updated poll details", required = true)
      @Valid @RequestBody final UpdatePollDto updatePollDto,
    @Parameter(hidden = true)
      @AuthenticationPrincipal final RegisteredUser user) {
    return pollUpdateService.updatePoll(pollId, updatePollDto, user);
  }
}
