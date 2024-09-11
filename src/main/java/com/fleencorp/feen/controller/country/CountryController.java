package com.fleencorp.feen.controller.country;

import com.fleencorp.base.model.view.search.SearchResultView;
import com.fleencorp.feen.exception.user.CountryNotFoundException;
import com.fleencorp.feen.model.domain.other.Country;
import com.fleencorp.feen.model.request.search.CountrySearchRequest;
import com.fleencorp.feen.model.response.other.CountAllResponse;
import com.fleencorp.feen.service.common.CountryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequestMapping("api/country")
@RestController
@PreAuthorize("hasAnyRole('ADMINISTRATOR', 'SUPER_ADMINISTRATOR')")
public class CountryController {
    private final CountryService countryService;

    public CountryController(CountryService countryService) {
        this.countryService = countryService;
    }

    @GetMapping(value = "")
    public SearchResultView findCountries(CountrySearchRequest searchRequest) {
        return countryService.findCountries(searchRequest);
    }

    @GetMapping(value = "/id/{id}")
    public Country getCountry(@PathVariable Long id) {
        return countryService.getCountry(id);
    }

    @GetMapping(value = "/code/{code}")
    public Country getCountryByCode(@PathVariable String code) {
        return countryService.getCountryByCode(code);
    }

    @GetMapping(value = "/count-all")
    public CountAllResponse countAll() {
        return countryService.countAll();
    }

    @GetMapping(value = "/title/{title}")
    public String getCountryCodeByTitle(@PathVariable String title) {
        return countryService.getCountryCodeByTitle(title).orElseThrow(() -> {
            throw new CountryNotFoundException(title);
        });
    }
}