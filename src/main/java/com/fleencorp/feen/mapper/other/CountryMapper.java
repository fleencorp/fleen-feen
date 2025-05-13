package com.fleencorp.feen.mapper.other;

import com.fleencorp.feen.model.domain.other.Country;
import com.fleencorp.feen.model.response.country.CountryResponse;

import java.util.List;

public interface CountryMapper {

  CountryResponse toCountryResponse(Country entry);

  List<CountryResponse> toCountryResponses(List<Country> entries);
}
