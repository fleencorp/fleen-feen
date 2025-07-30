package com.fleencorp.feen.softask.controller.softask;

import com.fleencorp.base.resolver.SearchParam;
import com.fleencorp.feen.common.exception.FailedOperationException;
import com.fleencorp.feen.softask.exception.core.SoftAskNotFoundException;
import com.fleencorp.feen.softask.model.request.SoftAskSearchRequest;
import com.fleencorp.feen.softask.model.response.softask.SoftAskRetrieveResponse;
import com.fleencorp.feen.softask.model.search.SoftAskSearchResult;
import com.fleencorp.feen.softask.service.softask.SoftAskSearchService;
import com.fleencorp.feen.user.model.security.RegisteredUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/soft-ask")
public class SoftAskSearchController {

  private final SoftAskSearchService softAskSearchService;

  public SoftAskSearchController(final SoftAskSearchService softAskSearchService) {
    this.softAskSearchService = softAskSearchService;
  }

  @Operation(summary = "Retrieve a specific soft ask",
    description = "Fetches the details of a soft ask by its ID.")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Successfully retrieved the soft ask",
      content = @Content(schema = @Schema(implementation = SoftAskRetrieveResponse.class))),
    @ApiResponse(responseCode = "404", description = "Soft ask not found",
      content = @Content(schema = @Schema(implementation = SoftAskNotFoundException.class))),
    @ApiResponse(responseCode = "400", description = "Failed operation",
      content = @Content(schema = @Schema(implementation = FailedOperationException.class)))
  })
  @GetMapping(value = "/detail/{softAskId}")
  public SoftAskRetrieveResponse findSoftAsk(
    @Parameter(description = "ID of the soft ask to retrieve", required = true)
      @PathVariable(name = "softAskId") final Long softAskId,
    @Parameter(hidden = true)
      @AuthenticationPrincipal final RegisteredUser user) {
    return softAskSearchService.retrieveSoftAsk(softAskId, user);
  }

  @Operation(summary = "Search for soft asks",
    description = "Searches for soft asks based on the provided search criteria.")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Successfully retrieved search results",
      content = @Content(schema = @Schema(implementation = SoftAskSearchResult.class))),
    @ApiResponse(responseCode = "400", description = "Failed operation",
      content = @Content(schema = @Schema(implementation = FailedOperationException.class)))
  })
  @GetMapping(value = "/find-soft-asks")
  public SoftAskSearchResult findSoftAsks(
    @Parameter(description = "Search criteria for soft asks", required = true)
      @SearchParam final SoftAskSearchRequest searchRequest,
    @Parameter(hidden = true)
      @AuthenticationPrincipal final RegisteredUser user) {
    return softAskSearchService.findSoftAsks(searchRequest, user);
  }
}

