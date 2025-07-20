package com.fleencorp.feen.softask.controller.user;

import com.fleencorp.feen.common.exception.FailedOperationException;
import com.fleencorp.feen.softask.model.response.user.SoftAskUserProfileRetrieveResponse;
import com.fleencorp.feen.softask.service.participant.SoftAskParticipantService;
import com.fleencorp.feen.user.model.security.RegisteredUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/softask/user")
@PreAuthorize("hasAnyRole('USER', 'ADMINISTRATOR', 'SUPER_ADMINISTRATOR')")
public class SoftAskUserController {

  private final SoftAskParticipantService softAskParticipantService;

  public SoftAskUserController(final SoftAskParticipantService softAskParticipantService) {
    this.softAskParticipantService = softAskParticipantService;
  }

  @Operation(summary = "Get user profile for soft ask",
    description = "Retrieves the user profile information for soft ask participation.")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Successfully retrieved user profile",
      content = @Content(schema = @Schema(implementation = SoftAskUserProfileRetrieveResponse.class))),
    @ApiResponse(responseCode = "400", description = "Failed operation",
      content = @Content(schema = @Schema(implementation = FailedOperationException.class)))
  })
  @GetMapping(value = "/profile")
  public SoftAskUserProfileRetrieveResponse findUserProfile(
    @Parameter(hidden = true)
      @AuthenticationPrincipal final RegisteredUser user) {
    return softAskParticipantService.findUserProfile(user);
  }
} 