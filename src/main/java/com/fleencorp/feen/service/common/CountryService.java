package com.fleencorp.feen.service.common;

import com.fleencorp.base.model.view.search.SearchResultView;
import com.fleencorp.base.service.BasicCountryService;
import com.fleencorp.feen.model.domain.other.Country;
import com.fleencorp.feen.model.request.search.CountrySearchRequest;
import com.fleencorp.feen.model.response.other.CountAllResponse;

import java.util.Optional;

public interface CountryService extends BasicCountryService {

  SearchResultView findCountries(CountrySearchRequest searchRequest);

  Country getCountry(Long id);

  Country getCountryByCode(String code);

  CountAllResponse countAll();

  Optional<String> getCountryCodeByTitle(String title);
}
