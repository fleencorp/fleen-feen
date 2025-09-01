package com.fleencorp.feen.softask.controller.reply;

import com.fleencorp.feen.common.exception.FailedOperationException;
import com.fleencorp.feen.softask.exception.core.SoftAskNotFoundException;
import com.fleencorp.feen.softask.exception.core.SoftAskUpdateDeniedException;
import com.fleencorp.feen.softask.model.dto.reply.AddSoftAskReplyDto;
import com.fleencorp.feen.softask.model.dto.reply.DeleteSoftAskReplyDto;
import com.fleencorp.feen.softask.model.response.reply.SoftAskReplyAddResponse;
import com.fleencorp.feen.softask.model.response.reply.SoftAskReplyDeleteResponse;
import com.fleencorp.feen.softask.service.reply.SoftAskReplyService;
import com.fleencorp.feen.user.exception.member.MemberNotFoundException;
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
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/soft-ask/reply")
@PreAuthorize("hasAnyRole('USER', 'ADMINISTRATOR', 'SUPER_ADMINISTRATOR')")
public class SoftAskReplyController {

  private final SoftAskReplyService softAskReplyService;

  public SoftAskReplyController(final SoftAskReplyService softAskReplyService) {
    this.softAskReplyService = softAskReplyService;
  }

  @Operation(summary = "Add a reply to a soft ask",
    description = "Adds a new reply to an existing soft ask. Requires user authentication.")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Successfully added the reply",
      content = @Content(schema = @Schema(implementation = SoftAskReplyAddResponse.class))),
    @ApiResponse(responseCode = "404", description = "Soft Ask cannot be found",
      content = @Content(schema = @Schema(implementation = SoftAskNotFoundException.class))),
    @ApiResponse(responseCode = "404", description = "Member cannot be found",
      content = @Content(schema = @Schema(implementation = MemberNotFoundException.class))),
    @ApiResponse(responseCode = "400", description = "Failed operation",
      content = @Content(schema = @Schema(implementation = FailedOperationException.class)))
  })
  @PostMapping(value = "/add")
  public SoftAskReplyAddResponse addSoftAskReply(
    @Parameter(description = "Add Reply dto for a soft ask", required = true)
      @Valid @RequestBody final AddSoftAskReplyDto addSoftAskReplyDto,
    @Parameter(hidden = true)
      @AuthenticationPrincipal final RegisteredUser user) {
    return softAskReplyService.addSoftAskReply(addSoftAskReplyDto, user);
  }

  @Operation(summary = "Delete an existing soft ask reply",
    description = "Deletes an existing soft ask reply. Requires user authentication and appropriate permissions.")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Successfully deleted the reply",
      content = @Content(schema = @Schema(implementation = SoftAskReplyDeleteResponse.class))),
    @ApiResponse(responseCode = "403", description = "Update denied",
      content = @Content(schema = @Schema(implementation = SoftAskUpdateDeniedException.class))),
    @ApiResponse(responseCode = "400", description = "Failed operation",
      content = @Content(schema = @Schema(implementation = FailedOperationException.class)))
  })
  @PutMapping(value = "/delete/{replyId}")
  public SoftAskReplyDeleteResponse deleteSoftAskReply(
    @Parameter(description = "ID of the reply to delete", required = true)
      @PathVariable(name = "replyId") final Long replyId,
    @Parameter(description = "Delete Soft Ask Reply dto", required = true)
      @Valid @RequestBody final DeleteSoftAskReplyDto deleteSoftAskReplyDto,
    @Parameter(hidden = true)
      @AuthenticationPrincipal final RegisteredUser user) {
    return softAskReplyService.deleteSoftAskReply(replyId, deleteSoftAskReplyDto, user);
  }
} 