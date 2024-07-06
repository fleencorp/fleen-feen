package com.fleencorp.feen.mapper;

import com.fleencorp.feen.model.domain.calendar.Calendar;
import com.fleencorp.feen.model.response.calendar.base.CalendarResponse;

import java.util.List;
import java.util.Objects;

import static java.util.Collections.emptyList;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toList;

/**
* Utility class for mapping Calendar entities to CalendarResponse DTOs.
*
* @author Yusuf Alamu Musa
* @version 1.0
*/
public class CalendarMapper {

  /**
  * Maps a Calendar entity to a CalendarResponse DTO.
  *
  * @param calendar the Calendar entity to map
  * @return the mapped CalendarResponse DTO, or null if the input is null
  */
  public static CalendarResponse toCalendarResponse(final Calendar calendar) {
    if (nonNull(calendar)) {
      return CalendarResponse.builder()
          .id(calendar.getCalendarId())
          .title(calendar.getTitle())
          .description(calendar.getDescription())
          .timezone(calendar.getTimezone())
          .code(calendar.getCode())
          .createdOn(calendar.getCreatedOn())
          .updatedOn(calendar.getUpdatedOn())
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
  public static List<CalendarResponse> toCalendars(final List<Calendar> entries) {
    if (nonNull(entries) && !entries.isEmpty()) {
      return entries.stream()
          .map(CalendarMapper::toCalendarResponse)
          .filter(Objects::nonNull)
          .collect(toList());
    }
    return emptyList();
  }

}
