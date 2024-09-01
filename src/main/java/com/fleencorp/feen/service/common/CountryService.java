package com.fleencorp.feen.service.common;

import com.fleencorp.base.model.view.search.SearchResultView;
import com.fleencorp.feen.model.domain.other.Country;
import com.fleencorp.feen.model.request.search.CountrySearchRequest;
import com.fleencorp.feen.model.response.other.CountAllResponse;

import java.util.Optional;

public interface CountryService {

  SearchResultView findCountries(CountrySearchRequest searchRequest);

  Country getCountry(Long id);

  Country getCountryByCode(String code);

  CountAllResponse countAll();

  boolean isCountryExists(String code);

  Optional<String> getCountryCodeByTitle(String title);
}
