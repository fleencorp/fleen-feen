package com.fleencorp.feen.country.service;

import com.fleencorp.base.service.BasicCountryService;
import com.fleencorp.feen.country.exception.CountryNotFoundException;
import com.fleencorp.feen.country.model.domain.Country;
import com.fleencorp.feen.country.model.response.CountryResponse;
import com.fleencorp.feen.country.model.response.RetrieveCountryResponse;
import com.fleencorp.feen.country.model.search.CountrySearchResult;
import com.fleencorp.feen.model.request.search.CountrySearchRequest;
import com.fleencorp.feen.model.response.other.CountAllResponse;

import java.util.Optional;

public interface CountryService extends BasicCountryService {

  CountrySearchResult findCountries(CountrySearchRequest searchRequest);

  RetrieveCountryResponse getCountry(Long countryId) throws CountryNotFoundException;

  Country getCountryByCode(String code);

  CountAllResponse countAll();

  Optional<CountryResponse> getCountryFromCache(String title);

  Optional<String> getCountryCodeByTitle(String title);
}
