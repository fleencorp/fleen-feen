package com.fleencorp.feen.calendar.mapper;

import com.fleencorp.feen.calendar.model.domain.Calendar;
import com.fleencorp.feen.calendar.model.response.base.CalendarResponse;

import java.util.List;
import java.util.Objects;

import static java.util.Objects.nonNull;

/**
* Utility class for mapping Calendar entities to CalendarResponse DTOs.
*
* @author Yusuf Alamu Musa
* @version 1.0
*/
public final class CalendarMapper {

  private CalendarMapper() {}

  /**
  * Maps a Calendar entity to a CalendarResponse DTO.
  *
  * @param entry the Calendar entity to map
  * @return the mapped CalendarResponse DTO, or null if the input is null
  */
  public static CalendarResponse toCalendarResponse(final Calendar entry) {
    if (nonNull(entry)) {
      final CalendarResponse response = new CalendarResponse();

      response.setId(entry.getCalendarId());
      response.setTitle(entry.getTitle());
      response.setDescription(entry.getDescription());
      response.setTimezone(entry.getTimezone());
      response.setCode(entry.getCode());

      response.setCreatedOn(entry.getCreatedOn());
      response.setUpdatedOn(entry.getUpdatedOn());

      return response;
    }
    return null;
  }

  /**
  * Maps a list of Calendar entities to a list of CalendarResponse DTOs.
  *
  * @param entries the list of Calendar entities to map
  * @return the list of mapped CalendarResponse DTOs, or an empty list if the input is null or empty
  */
  public static List<CalendarResponse> toCalendarResponses(final List<Calendar> entries) {
    if (nonNull(entries) && !entries.isEmpty()) {
      return entries.stream()
          .filter(Objects::nonNull)
          .map(CalendarMapper::toCalendarResponse)
          .toList();
    }
    return List.of();
  }

}
