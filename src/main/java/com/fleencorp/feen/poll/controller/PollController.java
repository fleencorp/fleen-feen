package com.fleencorp.feen.poll.controller;

import com.fleencorp.feen.exception.base.FailedOperationException;
import com.fleencorp.feen.exception.chat.space.ChatSpaceNotFoundException;
import com.fleencorp.feen.exception.chat.space.core.NotAnAdminOfChatSpaceException;
import com.fleencorp.feen.exception.stream.StreamNotFoundException;
import com.fleencorp.feen.exception.stream.core.StreamNotCreatedByUserException;
import com.fleencorp.feen.poll.exception.poll.PollNotFoundException;
import com.fleencorp.feen.poll.model.dto.AddPollDto;
import com.fleencorp.feen.poll.model.response.PollCreateResponse;
import com.fleencorp.feen.poll.model.response.PollDeleteResponse;
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

  private final PollService pollService;

  public PollController(final PollService pollService) {
    this.pollService = pollService;
  }

  @Operation(summary = "Create a new poll",
    description = "Creates a new poll with the provided details. Requires user authentication.")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Successfully created the poll",
      content = @Content(schema = @Schema(implementation = PollCreateResponse.class))),
    @ApiResponse(responseCode = "404", description = "Member not found",
      content = @Content(schema = @Schema(implementation = MemberNotFoundException.class))),
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

  @Operation(summary = "Create a new poll for a stream",
    description = "Creates a new poll associated with a specific stream. Requires user authentication and stream ownership.")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Successfully created the stream poll",
      content = @Content(schema = @Schema(implementation = PollCreateResponse.class))),
    @ApiResponse(responseCode = "404", description = "Member not found",
      content = @Content(schema = @Schema(implementation = MemberNotFoundException.class))),
    @ApiResponse(responseCode = "404", description = "Stream not found",
      content = @Content(schema = @Schema(implementation = StreamNotFoundException.class))),
    @ApiResponse(responseCode = "403", description = "Stream not created by user",
      content = @Content(schema = @Schema(implementation = StreamNotCreatedByUserException.class))),
    @ApiResponse(responseCode = "400", description = "Failed operation",
      content = @Content(schema = @Schema(implementation = FailedOperationException.class)))
  })
  @PostMapping(value = "/add/stream/{streamId}")
  public PollCreateResponse streamAddPoll(
    @Parameter(description = "ID of the stream to associate the poll with", required = true)
      @PathVariable(name = "streamId") final Long streamId,
    @Parameter(description = "Poll details for creation", required = true)
      @Valid @RequestBody final AddPollDto addPollDto,
    @Parameter(hidden = true)
      @AuthenticationPrincipal final RegisteredUser user) {
    return pollService.streamAddPoll(streamId, addPollDto, user);
  }

  @Operation(summary = "Create a new poll for a chat space",
    description = "Creates a new poll associated with a specific chat space. Requires user authentication and admin privileges for the chat space.")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Successfully created the chat space poll",
      content = @Content(schema = @Schema(implementation = PollCreateResponse.class))),
    @ApiResponse(responseCode = "404", description = "Member or chat space not found",
      content = @Content(schema = @Schema(implementation = MemberNotFoundException.class))),
    @ApiResponse(responseCode = "404", description = "Chat Space not found",
      content = @Content(schema = @Schema(implementation = ChatSpaceNotFoundException.class))),
    @ApiResponse(responseCode = "403", description = "User is not an admin of the chat space",
      content = @Content(schema = @Schema(implementation = NotAnAdminOfChatSpaceException.class))),
    @ApiResponse(responseCode = "400", description = "Failed operation",
      content = @Content(schema = @Schema(implementation = FailedOperationException.class)))
  })
  @PostMapping(value = "/add/chat-space/{chatSpaceId}")
  public PollCreateResponse chatSpaceAddPoll(
    @Parameter(description = "ID of the chat space to associate the poll with", required = true)
      @PathVariable(name = "chatSpaceId") final Long chatSpaceId,
    @Parameter(description = "Poll details for creation", required = true)
      @Valid @RequestBody final AddPollDto addPollDto,
    @Parameter(hidden = true)
      @AuthenticationPrincipal final RegisteredUser user) {
    return pollService.chatSpaceAddPoll(chatSpaceId, addPollDto, user);
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
  @DeleteMapping(value = "/delete/{pollId}")
  public PollDeleteResponse deletePoll(
    @Parameter(description = "ID of the poll to delete", required = true)
      @PathVariable(name = "pollId") final Long pollId,
    @Parameter(hidden = true)
      @AuthenticationPrincipal final RegisteredUser user) {
    return pollService.deletePoll(pollId, user);
  }

}
