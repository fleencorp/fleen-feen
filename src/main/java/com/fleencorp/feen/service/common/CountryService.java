package com.fleencorp.feen.service.common;

import com.fleencorp.base.service.BasicCountryService;
import com.fleencorp.feen.model.domain.other.Country;
import com.fleencorp.feen.model.request.search.CountrySearchRequest;
import com.fleencorp.feen.model.response.country.CountryResponse;
import com.fleencorp.feen.model.response.country.RetrieveCountryResponse;
import com.fleencorp.feen.model.response.other.CountAllResponse;
import com.fleencorp.feen.model.search.country.CountrySearchResult;

import java.util.Optional;

public interface CountryService extends BasicCountryService {

  CountrySearchResult findCountries(CountrySearchRequest searchRequest);

  RetrieveCountryResponse getCountry(Long countryId);

  Country getCountryByCode(String code);

  CountAllResponse countAll();

  Optional<CountryResponse> getCountryFromCache(String title);

  Optional<String> getCountryCodeByTitle(String title);
}
