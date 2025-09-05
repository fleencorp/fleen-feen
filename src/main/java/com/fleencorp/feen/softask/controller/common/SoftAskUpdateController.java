package com.fleencorp.feen.softask.controller.common;

import com.fleencorp.feen.common.exception.FailedOperationException;
import com.fleencorp.feen.shared.security.RegisteredUser;
import com.fleencorp.feen.softask.exception.core.SoftAskNotFoundException;
import com.fleencorp.feen.softask.exception.core.SoftAskReplyNotFoundException;
import com.fleencorp.feen.softask.exception.core.SoftAskUpdateDeniedException;
import com.fleencorp.feen.softask.model.dto.common.UpdateSoftAskContentDto;
import com.fleencorp.feen.softask.model.response.common.SoftAskContentUpdateResponse;
import com.fleencorp.feen.softask.service.common.SoftAskCommonService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/soft-ask/update")
public class SoftAskUpdateController {

  private final SoftAskCommonService softAskCommonService;

  public SoftAskUpdateController(final SoftAskCommonService softAskCommonService) {
    this.softAskCommonService = softAskCommonService;
  }

  @Operation(summary = "Update the content of a soft ask or reply",
    description = "Updates the content of an existing soft ask or reply. Requires user authentication and permissions.")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Successfully updated the content",
      content = @Content(schema = @Schema(implementation = SoftAskContentUpdateResponse.class))),
    @ApiResponse(responseCode = "404", description = "Soft ask or reply not found",
      content = @Content(schema = @Schema(oneOf = {
        SoftAskNotFoundException.class,
        SoftAskReplyNotFoundException.class
      }))),
    @ApiResponse(responseCode = "403", description = "Update denied due to insufficient permissions",
      content = @Content(schema = @Schema(implementation = SoftAskUpdateDeniedException.class))),
    @ApiResponse(responseCode = "400", description = "Failed operation",
      content = @Content(schema = @Schema(implementation = FailedOperationException.class)))
  })
  @PutMapping(value = "/content")
  public SoftAskContentUpdateResponse updateSoftAskContent(
    @Parameter(description = "Updated content details", required = true)
      @Valid @RequestBody final UpdateSoftAskContentDto updateSoftAskContentDto,
    @Parameter(hidden = true)
      @AuthenticationPrincipal final RegisteredUser user) {
    return softAskCommonService.updateSoftAskContent(updateSoftAskContentDto, user);
  }
}
