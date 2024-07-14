package com.fleencorp.feen.service.calendar;

import com.fleencorp.base.model.view.search.SearchResultView;
import com.fleencorp.feen.model.dto.calendar.CreateCalendarDto;
import com.fleencorp.feen.model.dto.calendar.ShareCalendarWithUserDto;
import com.fleencorp.feen.model.dto.calendar.UpdateCalendarDto;
import com.fleencorp.feen.model.request.search.calendar.CalendarSearchRequest;
import com.fleencorp.feen.model.response.other.DeleteResponse;
import com.fleencorp.feen.model.response.calendar.CreateCalendarResponse;
import com.fleencorp.feen.model.response.calendar.RetrieveCalendarResponse;
import com.fleencorp.feen.model.response.calendar.ShareCalendarWithUserResponse;
import com.fleencorp.feen.model.response.calendar.UpdateCalendarResponse;

public interface CalendarService {

  SearchResultView findCalendars(CalendarSearchRequest searchRequest);

  RetrieveCalendarResponse findCalendar(Long calendarId);

  CreateCalendarResponse createCalendar(CreateCalendarDto createCalendarDto);

  UpdateCalendarResponse updateCalendar(Long calendarId, UpdateCalendarDto updateCalendarDto);

  DeleteResponse deleteCalendar(Long calendarId);

  ShareCalendarWithUserResponse shareCalendarWithUser(Long calendarId, ShareCalendarWithUserDto shareCalendarWithUserDto);
}
