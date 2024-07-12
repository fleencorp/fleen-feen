package com.fleencorp.feen.service.common;

import com.fleencorp.feen.model.domain.other.Country;
import com.fleencorp.feen.model.response.other.CountAllResponse;

public interface CountryService {

  Country getCountry(Long id);

  CountAllResponse countAll();

  boolean isCountryExists(Long id);

}
