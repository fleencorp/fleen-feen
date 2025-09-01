package com.fleencorp.feen.softask.controller.softask;

import com.fleencorp.feen.chat.space.exception.core.ChatSpaceNotFoundException;
import com.fleencorp.feen.common.exception.FailedOperationException;
import com.fleencorp.feen.softask.exception.core.SoftAskNotFoundException;
import com.fleencorp.feen.softask.exception.core.SoftAskUpdateDeniedException;
import com.fleencorp.feen.softask.model.dto.softask.AddSoftAskDto;
import com.fleencorp.feen.softask.model.dto.softask.DeleteSoftAskDto;
import com.fleencorp.feen.softask.model.response.softask.SoftAskAddResponse;
import com.fleencorp.feen.softask.model.response.softask.SoftAskDeleteResponse;
import com.fleencorp.feen.softask.service.softask.SoftAskService;
import com.fleencorp.feen.stream.exception.core.StreamNotFoundException;
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
@RequestMapping(value = "/api/soft-ask")
@PreAuthorize("hasAnyRole('USER', 'ADMINISTRATOR', 'SUPER_ADMINISTRATOR')")
public class SoftAskController {

  private final SoftAskService softAskService;

  public SoftAskController(final SoftAskService softAskService) {
    this.softAskService = softAskService;
  }

  @Operation(summary = "Create a new soft ask",
    description = "Creates a new soft ask with the provided details. Requires user authentication.")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Successfully created the soft ask",
      content = @Content(schema = @Schema(implementation = SoftAskAddResponse.class))),
    @ApiResponse(responseCode = "404", description = "Member not found",
      content = @Content(schema = @Schema(implementation = MemberNotFoundException.class))),
    @ApiResponse(responseCode = "404", description = "Chat Space not found",
      content = @Content(schema = @Schema(implementation = ChatSpaceNotFoundException.class))),
    @ApiResponse(responseCode = "404", description = "Stream not found",
      content = @Content(schema = @Schema(implementation = StreamNotFoundException.class))),
    @ApiResponse(responseCode = "400", description = "Failed operation",
      content = @Content(schema = @Schema(implementation = FailedOperationException.class)))
  })
  @PostMapping(value = "/add")
  public SoftAskAddResponse addSoftAsk(
    @Parameter(description = "Soft ask details for creation", required = true)
      @Valid @RequestBody final AddSoftAskDto addSoftAskDto,
    @Parameter(hidden = true)
      @AuthenticationPrincipal final RegisteredUser user) {
    return softAskService.addSoftAsk(addSoftAskDto, user);
  }

  @Operation(summary = "Delete an existing soft ask",
    description = "Deletes an existing soft ask. Requires user authentication and appropriate permissions.")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Successfully deleted the soft ask",
      content = @Content(schema = @Schema(implementation = SoftAskDeleteResponse.class))),
    @ApiResponse(responseCode = "404", description = "Soft ask not found",
      content = @Content(schema = @Schema(implementation = SoftAskNotFoundException.class))),
    @ApiResponse(responseCode = "403", description = "Update denied",
      content = @Content(schema = @Schema(implementation = SoftAskUpdateDeniedException.class))),
    @ApiResponse(responseCode = "400", description = "Failed operation",
      content = @Content(schema = @Schema(implementation = FailedOperationException.class)))
  })
  @PutMapping(value = "/delete/{softAskId}")
  public SoftAskDeleteResponse deleteSoftAsk(
    @Parameter(description = "ID of the soft ask to delete", required = true)
      @PathVariable(name = "softAskId") final Long softAskId,
    @Parameter(hidden = true)
      @AuthenticationPrincipal final RegisteredUser user) {
    final DeleteSoftAskDto deleteSoftAskDto = DeleteSoftAskDto.of(softAskId);
    return softAskService.deleteSoftAsk(deleteSoftAskDto, user);
  }
}
