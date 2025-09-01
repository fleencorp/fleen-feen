package com.fleencorp.feen.softask.controller.vote;

import com.fleencorp.feen.common.exception.FailedOperationException;
import com.fleencorp.feen.softask.exception.core.SoftAskNotFoundException;
import com.fleencorp.feen.softask.exception.core.SoftAskReplyNotFoundException;
import com.fleencorp.feen.softask.model.dto.vote.SoftAskVoteDto;
import com.fleencorp.feen.softask.model.response.vote.SoftAskVoteUpdateResponse;
import com.fleencorp.feen.softask.service.vote.SoftAskVoteService;
import com.fleencorp.feen.shared.security.RegisteredUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/softask/vote")
@PreAuthorize("hasAnyRole('USER', 'ADMINISTRATOR', 'SUPER_ADMINISTRATOR')")
public class SoftAskVoteController {

  private final SoftAskVoteService softAskVoteService;

  public SoftAskVoteController(final SoftAskVoteService softAskVoteService) {
    this.softAskVoteService = softAskVoteService;
  }

  @Operation(summary = "Vote on a soft ask",
    description = "Submits a vote for a specified soft ask. Requires user authentication.")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Successfully submitted the vote",
      content = @Content(schema = @Schema(implementation = SoftAskVoteUpdateResponse.class))),
    @ApiResponse(responseCode = "404", description = "Soft Ask not found",
      content = @Content(schema = @Schema(implementation = SoftAskNotFoundException.class))),
    @ApiResponse(responseCode = "404", description = "Soft Ask Reply not found",
      content = @Content(schema = @Schema(implementation = SoftAskReplyNotFoundException.class))),
    @ApiResponse(responseCode = "400", description = "Failed operation",
      content = @Content(schema = @Schema(implementation = FailedOperationException.class)))
  })
  @PutMapping(value = "/vote")
  public SoftAskVoteUpdateResponse vote(
    @Parameter(description = "Soft ask vote details", required = true)
      @Valid @RequestBody final SoftAskVoteDto softAskVoteDto,
    @Parameter(hidden = true)
      @AuthenticationPrincipal final RegisteredUser user) {
    return softAskVoteService.vote(softAskVoteDto, user);
  }
} 