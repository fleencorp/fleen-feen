package com.fleencorp.feen.country.mapper.impl;

import com.fleencorp.feen.mapper.impl.BaseMapper;
import com.fleencorp.feen.country.mapper.CountryMapper;
import com.fleencorp.feen.country.model.domain.Country;
import com.fleencorp.feen.country.model.response.CountryResponse;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

import static java.util.Objects.nonNull;

/**
* Mapper class for converting Country entities to CountryResponse DTOs and vice versa.
*
* @author Yusuf Alamu Musa
* @version 1.0
*/
@Component
public final class CountryMapperImpl extends BaseMapper implements CountryMapper {

  public CountryMapperImpl(final MessageSource messageSource) {
    super(messageSource);
  }

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
  @Override
  public CountryResponse toCountryResponse(final Country entry) {
    if (nonNull(entry)) {
      final CountryResponse response = new CountryResponse();

      response.setId(entry.getCountryId());
      response.setTitle(entry.getTitle());
      response.setCode(entry.getCode());
      response.setTimezone(entry.getTimezone());

      response.setCreatedOn(entry.getCreatedOn());
      response.setUpdatedOn(entry.getUpdatedOn());

      return response;
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
  @Override
  public List<CountryResponse> toCountryResponses(final List<Country> entries) {
    if (nonNull(entries) && !entries.isEmpty()) {
      return entries.stream()
          .filter(Objects::nonNull)
          .map(this::toCountryResponse)
          .toList();
    }
    return List.of();
  }

}
