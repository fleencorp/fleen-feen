package com.fleencorp.feen.business.controller;

import com.fleencorp.feen.business.exception.BusinessNotFoundException;
import com.fleencorp.feen.business.exception.BusinessNotOwnerException;
import com.fleencorp.feen.business.model.dto.AddBusinessDto;
import com.fleencorp.feen.business.model.dto.DeleteBusinessDto;
import com.fleencorp.feen.business.model.dto.UpdateBusinessDto;
import com.fleencorp.feen.business.model.response.BusinessAddResponse;
import com.fleencorp.feen.business.model.response.BusinessDeleteResponse;
import com.fleencorp.feen.business.model.response.BusinessUpdateResponse;
import com.fleencorp.feen.business.service.BusinessService;
import com.fleencorp.feen.common.exception.FailedOperationException;
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
@RequestMapping(value = "/api/business")
public class BusinessController {

  private final BusinessService businessService;

  public BusinessController(final BusinessService businessService) {
    this.businessService = businessService;
  }

  @Operation(summary = "Add a business",
    description = "Creates a new business entry. Requires full user authentication.")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Business successfully added",
      content = @Content(schema = @Schema(implementation = BusinessAddResponse.class))),
    @ApiResponse(responseCode = "400", description = "Business not found",
      content = @Content(schema = @Schema(implementation = BusinessNotFoundException.class))),
    @ApiResponse(responseCode = "400", description = "Failed operation",
      content = @Content(schema = @Schema(implementation = FailedOperationException.class)))
  })
  @PreAuthorize("isFullyAuthenticated()")
  @PostMapping(value = "")
  public BusinessAddResponse addBusiness(
    @Parameter(description = "Business details to add", required = true)
      @Valid @RequestBody final AddBusinessDto addBusinessDto,
    @Parameter(hidden = true)
      @AuthenticationPrincipal final RegisteredUser user) {
    return businessService.addBusiness(addBusinessDto, user);
  }

  @Operation(summary = "Update a business",
    description = "Updates the details of an existing business by its ID. Requires full user authentication.")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Business successfully updated",
      content = @Content(schema = @Schema(implementation = BusinessUpdateResponse.class))),
    @ApiResponse(responseCode = "404", description = "Business not found",
      content = @Content(schema = @Schema(implementation = BusinessNotFoundException.class))),
    @ApiResponse(responseCode = "400", description = "Business not created by owner",
      content = @Content(schema = @Schema(implementation = BusinessNotOwnerException.class))),
    @ApiResponse(responseCode = "400", description = "Failed operation",
      content = @Content(schema = @Schema(implementation = FailedOperationException.class)))
  })
  @PreAuthorize("isFullyAuthenticated()")
  @PutMapping(value = "/{businessId}")
  public BusinessUpdateResponse updateBusiness(
    @Parameter(description = "Business ID", required = true)
      @PathVariable final Long businessId,
    @Parameter(description = "Business details to update", required = true)
      @Valid @RequestBody final UpdateBusinessDto updateBusinessDto,
    @Parameter(hidden = true)
      @AuthenticationPrincipal final RegisteredUser user) {
    return businessService.updateBusiness(businessId, updateBusinessDto, user);
  }

  @Operation(summary = "Delete a business",
    description = "Deletes a business entry by its ID. Requires full user authentication.")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Business successfully deleted",
      content = @Content(schema = @Schema(implementation = BusinessDeleteResponse.class))),
    @ApiResponse(responseCode = "404", description = "Business not found",
      content = @Content(schema = @Schema(implementation = BusinessNotFoundException.class))),
    @ApiResponse(responseCode = "400", description = "Failed operation",
      content = @Content(schema = @Schema(implementation = FailedOperationException.class)))
  })
  @PreAuthorize("isFullyAuthenticated()")
  @DeleteMapping(value = "/{businessId}")
  public BusinessDeleteResponse deleteBusiness(
    @Parameter(description = "Business ID", required = true)
      @PathVariable final Long businessId,
    @Parameter(hidden = true)
      @AuthenticationPrincipal final RegisteredUser user) {
    final DeleteBusinessDto deleteBusinessDto = DeleteBusinessDto.of(businessId);
    return businessService.deleteBusiness(deleteBusinessDto, user);
  }
}

