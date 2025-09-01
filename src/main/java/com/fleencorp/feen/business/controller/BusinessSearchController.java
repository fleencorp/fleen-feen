package com.fleencorp.feen.business.controller;

import com.fleencorp.base.resolver.SearchParam;
import com.fleencorp.feen.business.model.request.search.BusinessSearchRequest;
import com.fleencorp.feen.business.model.search.BusinessSearchResult;
import com.fleencorp.feen.business.service.BusinessSearchService;
import com.fleencorp.feen.shared.security.RegisteredUser;
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
@RequestMapping(value = "/api/business")
public class BusinessSearchController {

  private final BusinessSearchService businessSearchService;

  public BusinessSearchController(final BusinessSearchService businessSearchService) {
    this.businessSearchService = businessSearchService;
  }

  @Operation(summary = "Find businesses",
    description = """
      Searches for and retrieves a list of businesses based on specified search criteria.Requires full user authentication.
    """)
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Successfully found businesses",
      content = @Content(schema = @Schema(implementation = BusinessSearchResult.class)))
  })
  @PreAuthorize("isFullyAuthenticated()")
  @GetMapping(value = "/entries")
  public BusinessSearchResult findBusinesses(
    @Parameter(description = "Search criteria for businesses")
      @SearchParam final BusinessSearchRequest searchRequest,
    @Parameter(hidden = true)
      @AuthenticationPrincipal final RegisteredUser user) {
    return businessSearchService.findBusinesses(searchRequest, user);
  }

}
