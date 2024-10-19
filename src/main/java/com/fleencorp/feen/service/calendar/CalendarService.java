package com.fleencorp.feen.service.calendar;

import com.fleencorp.feen.model.dto.calendar.CreateCalendarDto;
import com.fleencorp.feen.model.dto.calendar.ShareCalendarWithUserDto;
import com.fleencorp.feen.model.dto.calendar.UpdateCalendarDto;
import com.fleencorp.feen.model.request.search.calendar.CalendarSearchRequest;
import com.fleencorp.feen.model.response.calendar.*;
import com.fleencorp.feen.model.search.calendar.CalendarSearchResult;
import com.fleencorp.feen.model.security.FleenUser;

public interface CalendarService {

  DataForCreateCalendarResponse getDataForCreateCalendar();

  CalendarSearchResult findCalendars(CalendarSearchRequest searchRequest);

  RetrieveCalendarResponse findCalendar(Long calendarId);

  CreateCalendarResponse createCalendar(CreateCalendarDto createCalendarDto, FleenUser user);

  UpdateCalendarResponse updateCalendar(Long calendarId, UpdateCalendarDto updateCalendarDto, FleenUser user);

  ReactivateCalendarResponse reactivateCalendar(Long calendarId, FleenUser user);

  DeletedCalendarResponse deleteCalendar(Long calendarId, FleenUser user);

  ShareCalendarWithUserResponse shareCalendarWithUser(Long calendarId, ShareCalendarWithUserDto shareCalendarWithUserDto, FleenUser user);
}
