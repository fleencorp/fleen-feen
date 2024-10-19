package com.fleencorp.feen.controller.country;

import com.fleencorp.base.resolver.SearchParam;
import com.fleencorp.feen.model.request.search.CountrySearchRequest;
import com.fleencorp.feen.model.response.country.RetrieveCountryResponse;
import com.fleencorp.feen.model.response.other.CountAllResponse;
import com.fleencorp.feen.model.search.country.CountrySearchResult;
import com.fleencorp.feen.service.common.CountryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequestMapping(value = "/api/country")
@RestController
@PreAuthorize("hasAnyRole('USER', 'ADMINISTRATOR', 'SUPER_ADMINISTRATOR')")
public class CountryController {

  private final CountryService countryService;

  public CountryController(final CountryService countryService) {
    this.countryService = countryService;
  }

  @GetMapping(value = "/entries")
  public CountrySearchResult findCountries(
      @SearchParam final CountrySearchRequest searchRequest) {
    return countryService.findCountries(searchRequest);
  }

  @GetMapping(value = "/detail/{countryId}")
  public RetrieveCountryResponse getCountry(
      @PathVariable(name = "countryId") final Long countryId) {
    return countryService.getCountry(countryId);
  }

  @GetMapping(value = "/count-all")
  public CountAllResponse countAll() {
    return countryService.countAll();
  }
}