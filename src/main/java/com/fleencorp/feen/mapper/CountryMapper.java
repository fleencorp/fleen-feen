package com.fleencorp.feen.mapper;

import com.fleencorp.feen.model.domain.other.Country;
import com.fleencorp.feen.model.response.country.CountryResponse;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;
import static java.util.Objects.nonNull;

/**
* Mapper class for converting Country entities to CountryResponse DTOs and vice versa.
*
* @author Yusuf Alamu Musa
* @version 1.0
*/
public enum CountryMapper {
    ;

  /**
  * Converts a Country entity to a CountryResponse DTO.
  *
  * <p>If the input Country entity is not null, maps its fields to a new CountryResponse using CountryResponse.builder().
  * The resulting CountryResponse includes the country's ID, title, code, created date, and updated date.
  * If the input Country entity is null, returns null.</p>
  *
  * @param entry the Country entity to convert
  * @return a CountryResponse DTO corresponding to the input Country entity, or null if the input is null
  */
  public static CountryResponse toCountryResponse(final Country entry) {
    if (nonNull(entry)) {
      return CountryResponse.builder()
          .id(entry.getCountryId())
          .title(entry.getTitle())
          .code(entry.getCode())
          .createdOn(entry.getCreatedOn())
          .updatedOn(entry.getUpdatedOn())
          .build();
    }
    return null;
  }

  /**
  * Converts a list of Country entities to a list of CountryResponse DTOs.
  *
  * <p>If the input list is not null and not empty, each Country entity is mapped to a CountryResponse using CountryMapper,
  * filtering out any null results, and then collected into a new list. If the input list is null or empty, an empty list is returned.</p>
  *
  * @param entries the list of Country entities to convert
  * @return a list of CountryResponse DTOs corresponding to the input list of Country entities
  */
  public static List<CountryResponse> toCountryResponses(final List<Country> entries) {
    if (nonNull(entries) && !entries.isEmpty()) {
      return entries.stream()
          .map(CountryMapper::toCountryResponse)
          .filter(Objects::nonNull)
          .collect(Collectors.toList());
    }
    return emptyList();
  }

}
