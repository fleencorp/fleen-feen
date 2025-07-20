package com.fleencorp.feen.softask.controller.answer;

import com.fleencorp.feen.common.exception.FailedOperationException;
import com.fleencorp.feen.softask.exception.core.SoftAskAnswerNotFoundException;
import com.fleencorp.feen.softask.exception.core.SoftAskNotFoundException;
import com.fleencorp.feen.softask.exception.core.SoftAskUpdateDeniedException;
import com.fleencorp.feen.softask.model.dto.answer.AddSoftAskAnswerDto;
import com.fleencorp.feen.softask.model.dto.answer.DeleteSoftAskAnswerDto;
import com.fleencorp.feen.softask.model.response.answer.SoftAskAnswerAddResponse;
import com.fleencorp.feen.softask.model.response.answer.SoftAskAnswerDeleteResponse;
import com.fleencorp.feen.softask.service.answer.SoftAskAnswerService;
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
@RequestMapping(value = "/api/soft-ask/answer")
@PreAuthorize("hasAnyRole('USER', 'ADMINISTRATOR', 'SUPER_ADMINISTRATOR')")
public class SoftAskAnswerController {

  private final SoftAskAnswerService softAskAnswerService;

  public SoftAskAnswerController(final SoftAskAnswerService softAskAnswerService) {
    this.softAskAnswerService = softAskAnswerService;
  }

  @Operation(summary = "Add an answer to a soft ask",
    description = "Adds a new answer to an existing soft ask. Requires user authentication.")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Successfully added the answer",
      content = @Content(schema = @Schema(implementation = SoftAskAnswerAddResponse.class))),
    @ApiResponse(responseCode = "404", description = "Soft Ask cannot be found",
      content = @Content(schema = @Schema(implementation = SoftAskNotFoundException.class))),
    @ApiResponse(responseCode = "404", description = "Member cannot be found",
      content = @Content(schema = @Schema(implementation = MemberNotFoundException.class))),
    @ApiResponse(responseCode = "400", description = "Failed operation",
      content = @Content(schema = @Schema(implementation = FailedOperationException.class)))
  })
  @PostMapping(value = "/add")
  public SoftAskAnswerAddResponse addSoftAskAnswer(
    @Parameter(description = "Answer dto for the soft ask", required = true)
      @Valid @RequestBody final AddSoftAskAnswerDto addSoftAskAnswerDto,
    @Parameter(hidden = true)
      @AuthenticationPrincipal final RegisteredUser user) {
    return softAskAnswerService.addSoftAskAnswer(addSoftAskAnswerDto, user);
  }

  @Operation(summary = "Delete an existing soft ask answer",
    description = "Deletes an existing soft ask answer. Requires user authentication and appropriate permissions.")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Successfully deleted the answer",
      content = @Content(schema = @Schema(implementation = SoftAskAnswerDeleteResponse.class))),
    @ApiResponse(responseCode = "404", description = "Answer not found",
      content = @Content(schema = @Schema(implementation = SoftAskAnswerNotFoundException.class))),
    @ApiResponse(responseCode = "403", description = "Update denied",
      content = @Content(schema = @Schema(implementation = SoftAskUpdateDeniedException.class))),
    @ApiResponse(responseCode = "400", description = "Failed operation",
      content = @Content(schema = @Schema(implementation = FailedOperationException.class)))
  })
  @PutMapping(value = "/delete/{answerId}")
  public SoftAskAnswerDeleteResponse deleteSoftAskAnswer(
    @Parameter(description = "ID of the answer to delete", required = true)
      @PathVariable(name = "answerId") final Long answerId,
    @Parameter(hidden = true)
      @AuthenticationPrincipal final RegisteredUser user) {
    final DeleteSoftAskAnswerDto deleteSoftAskAnswerDto = DeleteSoftAskAnswerDto.of(answerId);
    return softAskAnswerService.deleteSoftAskAnswer(deleteSoftAskAnswerDto, user);
  }
} 