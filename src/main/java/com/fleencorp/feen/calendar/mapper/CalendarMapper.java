package com.fleencorp.feen.calendar.mapper;

import com.fleencorp.feen.calendar.model.domain.Calendar;
import com.fleencorp.feen.calendar.model.response.base.CalendarResponse;

import java.util.Collection;
import java.util.List;

public interface CalendarMapper {

  CalendarResponse toCalendarResponse(Calendar entry);

  Collection<CalendarResponse> toCalendarResponses(List<Calendar> entries);
}
