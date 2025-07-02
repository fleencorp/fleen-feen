package com.fleencorp.feen.poll.controller;

import com.fleencorp.feen.exception.base.FailedOperationException;
import com.fleencorp.feen.exception.chat.space.ChatSpaceNotFoundException;
import com.fleencorp.feen.exception.chat.space.core.NotAnAdminOfChatSpaceException;
import com.fleencorp.feen.exception.stream.StreamNotFoundException;
import com.fleencorp.feen.exception.stream.core.StreamNotCreatedByUserException;
import com.fleencorp.feen.poll.exception.poll.PollNotFoundException;
import com.fleencorp.feen.poll.model.dto.AddPollDto;
import com.fleencorp.feen.poll.model.dto.DeletePollDto;
import com.fleencorp.feen.poll.model.response.GetDataRequiredToCreatePoll;
import com.fleencorp.feen.poll.model.response.PollCreateResponse;
import com.fleencorp.feen.poll.model.response.PollDeleteResponse;
import com.fleencorp.feen.poll.service.PollSearchService;
import com.fleencorp.feen.poll.service.PollService;
import com.fleencorp.feen.user.exception.member.MemberNotFoundException;
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
@RequestMapping(value = "/api/poll")
@PreAuthorize("hasAnyRole('USER', 'ADMINISTRATOR', 'SUPER_ADMINISTRATOR')")
public class PollController {

  private final PollSearchService pollSearchService;
  private final PollService pollService;

  public PollController(
      final PollSearchService pollSearchService,
      final PollService pollService) {
    this.pollSearchService = pollSearchService;
    this.pollService = pollService;
  }

  @Operation(summary = "Retrieve data to use in UI to create poll",
    description = "Fetches the details to use to create poll.")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Successfully retrieved data to be able to create poll",
      content = @Content(schema = @Schema(implementation = GetDataRequiredToCreatePoll.class))),
    @ApiResponse(responseCode = "400", description = "Failed operation",
      content = @Content(schema = @Schema(implementation = FailedOperationException.class)))
  })
  @GetMapping(value = "/data-required-create")
  public GetDataRequiredToCreatePoll getDataRequiredToCreatePoll() {
    return pollSearchService.getDataRequiredToCreatePoll();
  }

  @Operation(summary = "Create a new poll",
    description = "Creates a new poll with the provided details. Requires user authentication.")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Successfully created the poll",
      content = @Content(schema = @Schema(implementation = PollCreateResponse.class))),
    @ApiResponse(responseCode = "404", description = "Member not found",
      content = @Content(schema = @Schema(implementation = MemberNotFoundException.class))),
    @ApiResponse(responseCode = "404", description = "Chat Space not found",
      content = @Content(schema = @Schema(implementation = ChatSpaceNotFoundException.class))),
    @ApiResponse(responseCode = "403", description = "User is not an admin of the chat space",
      content = @Content(schema = @Schema(implementation = NotAnAdminOfChatSpaceException.class))),
    @ApiResponse(responseCode = "404", description = "Stream not found",
      content = @Content(schema = @Schema(implementation = StreamNotFoundException.class))),
    @ApiResponse(responseCode = "403", description = "Stream not created by user",
      content = @Content(schema = @Schema(implementation = StreamNotCreatedByUserException.class))),
    @ApiResponse(responseCode = "400", description = "Failed operation",
      content = @Content(schema = @Schema(implementation = FailedOperationException.class))),
    @ApiResponse(responseCode = "400", description = "Failed operation",
      content = @Content(schema = @Schema(implementation = FailedOperationException.class)))
  })
  @PostMapping(value = "/add")
  public PollCreateResponse addPoll(
    @Parameter(description = "Poll details for creation", required = true)
      @Valid @RequestBody final AddPollDto addPollDto,
    @Parameter(hidden = true)
      @AuthenticationPrincipal final RegisteredUser user) {
    return pollService.addPoll(addPollDto, user);
  }

  @Operation(summary = "Delete an existing poll",
    description = "Deletes an existing poll. Requires user authentication and appropriate permissions.")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Successfully deleted the poll",
      content = @Content(schema = @Schema(implementation = PollDeleteResponse.class))),
    @ApiResponse(responseCode = "404", description = "Poll not found",
      content = @Content(schema = @Schema(implementation = PollNotFoundException.class))),
    @ApiResponse(responseCode = "400", description = "Failed operation",
      content = @Content(schema = @Schema(implementation = FailedOperationException.class)))
  })
  @PutMapping(value = "/delete/{pollId}")
  public PollDeleteResponse deletePoll(
    @Parameter(description = "ID of the poll to delete", required = true)
      @PathVariable(name = "pollId") final Long pollId,
    @Parameter(hidden = true)
      @AuthenticationPrincipal final RegisteredUser user) {
    final DeletePollDto deletePollDto = DeletePollDto.of(pollId);
    return pollService.deletePoll(deletePollDto, user);
  }

}
