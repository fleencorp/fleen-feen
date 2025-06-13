package com.fleencorp.feen.country.mapper;

import com.fleencorp.feen.country.model.domain.Country;
import com.fleencorp.feen.country.model.response.CountryResponse;

import java.util.List;

public interface CountryMapper {

  CountryResponse toCountryResponse(Country entry);

  List<CountryResponse> toCountryResponses(List<Country> entries);
}
