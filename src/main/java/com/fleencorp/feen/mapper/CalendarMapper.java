package com.fleencorp.feen.mapper;

import com.fleencorp.feen.model.domain.calendar.Calendar;
import com.fleencorp.feen.model.response.calendar.base.CalendarResponse;

import java.util.List;
import java.util.Objects;

import static java.util.Objects.nonNull;

/**
* Utility class for mapping Calendar entities to CalendarResponse DTOs.
*
* @author Yusuf Alamu Musa
* @version 1.0
*/
public class CalendarMapper {

  private CalendarMapper() {}

  /**
  * Maps a Calendar entity to a CalendarResponse DTO.
  *
  * @param entry the Calendar entity to map
  * @return the mapped CalendarResponse DTO, or null if the input is null
  */
  public static CalendarResponse toCalendarResponse(final Calendar entry) {
    if (nonNull(entry)) {
      return CalendarResponse.builder()
          .id(entry.getCalendarId())
          .title(entry.getTitle())
          .description(entry.getDescription())
          .timezone(entry.getTimezone())
          .code(entry.getCode())
          .createdOn(entry.getCreatedOn())
          .updatedOn(entry.getUpdatedOn())
          .build();
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
