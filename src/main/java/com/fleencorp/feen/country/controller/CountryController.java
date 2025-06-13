package com.fleencorp.feen.country.controller;

import com.fleencorp.base.resolver.SearchParam;
import com.fleencorp.feen.country.exception.CountryNotFoundException;
import com.fleencorp.feen.country.model.response.RetrieveCountryResponse;
import com.fleencorp.feen.country.model.search.CountrySearchResult;
import com.fleencorp.feen.country.service.CountryService;
import com.fleencorp.feen.model.request.search.CountrySearchRequest;
import com.fleencorp.feen.model.response.other.CountAllResponse;
import com.fleencorp.feen.user.exception.auth.InvalidAuthenticationException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping(value = "/api/country")
@RestController
@PreAuthorize("hasAnyRole('USER', 'ADMINISTRATOR', 'SUPER_ADMINISTRATOR')")
public class CountryController {

  private final CountryService countryService;

  public CountryController(final CountryService countryService) {
    this.countryService = countryService;
  }

  @Operation(summary = "Search for countries",
    description = "Retrieves a paginated list of countries based on specified search criteria. " +
      "Accessible by users with USER, ADMINISTRATOR, or SUPER_ADMINISTRATOR roles."
  )
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Successfully retrieved countries",
      content = @Content(schema = @Schema(implementation = CountrySearchResult.class))),
    @ApiResponse(responseCode = "401", description = "User not authenticated",
      content = @Content(schema = @Schema(implementation = InvalidAuthenticationException.class)))
  })
  @GetMapping(value = "/entries")
  public CountrySearchResult findCountries(
      @Parameter(description = "Search criteria and pagination parameters", required = true)
        @SearchParam final CountrySearchRequest searchRequest) {
    return countryService.findCountries(searchRequest);
  }

  @Operation(summary = "Get country details by ID",
    description = "Retrieves the details of a specific country using its unique identifier. " +
      "Accessible by users with USER, ADMINISTRATOR, or SUPER_ADMINISTRATOR roles."
  )
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Successfully retrieved country details",
      content = @Content(schema = @Schema(implementation = RetrieveCountryResponse.class))),
    @ApiResponse(responseCode = "401", description = "User not authenticated",
      content = @Content(schema = @Schema(implementation = InvalidAuthenticationException.class))),
    @ApiResponse(responseCode = "404", description = "Country not found for the provided ID",
      content = @Content(schema = @Schema(implementation = CountryNotFoundException.class)))
  })
  @GetMapping(value = "/detail/{countryId}")
  public RetrieveCountryResponse getCountry(
      @Parameter(description = "ID of the country to retrieve", required = true)
        @PathVariable(name = "countryId") final Long countryId) {
    return countryService.getCountry(countryId);
  }

  @Operation(summary = "Count all countries",
    description = "Retrieves the total number of countries available in the system. " +
      "Accessible by users with USER, ADMINISTRATOR, or SUPER_ADMINISTRATOR roles."
  )
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Successfully retrieved country count",
      content = @Content(schema = @Schema(implementation = CountAllResponse.class))),
    @ApiResponse(responseCode = "401", description = "User not authenticated",
      content = @Content(schema = @Schema(implementation = InvalidAuthenticationException.class)))
  })
  @GetMapping(value = "/count-all")
  public CountAllResponse countAll() {
    return countryService.countAll();
  }
}