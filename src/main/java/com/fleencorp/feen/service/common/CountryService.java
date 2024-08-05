package com.fleencorp.feen.service.common;

import com.fleencorp.base.model.view.search.SearchResultView;
import com.fleencorp.feen.model.domain.other.Country;
import com.fleencorp.feen.model.request.search.CountrySearchRequest;
import com.fleencorp.feen.model.response.other.CountAllResponse;

public interface CountryService {

  SearchResultView findCountries(CountrySearchRequest searchRequest);

  Country getCountry(Long id);

  CountAllResponse countAll();

  boolean isCountryExists(Long id);

}
