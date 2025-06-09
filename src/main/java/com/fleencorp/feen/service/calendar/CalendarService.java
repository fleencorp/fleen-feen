package com.fleencorp.feen.service.calendar;

import com.fleencorp.feen.model.dto.calendar.CreateCalendarDto;
import com.fleencorp.feen.model.dto.calendar.ShareCalendarWithUserDto;
import com.fleencorp.feen.model.dto.calendar.UpdateCalendarDto;
import com.fleencorp.feen.model.request.search.calendar.CalendarSearchRequest;
import com.fleencorp.feen.model.response.calendar.*;
import com.fleencorp.feen.model.search.calendar.CalendarSearchResult;
import com.fleencorp.feen.user.security.RegisteredUser;

public interface CalendarService {

  DataForCreateCalendarResponse getDataForCreateCalendar();

  CalendarSearchResult findCalendars(CalendarSearchRequest searchRequest);

  RetrieveCalendarResponse findCalendar(Long calendarId);

  CreateCalendarResponse createCalendar(CreateCalendarDto createCalendarDto, RegisteredUser user);

  UpdateCalendarResponse updateCalendar(Long calendarId, UpdateCalendarDto updateCalendarDto, RegisteredUser user);

  ReactivateCalendarResponse reactivateCalendar(Long calendarId, RegisteredUser user);

  DeletedCalendarResponse deleteCalendar(Long calendarId, RegisteredUser user);

  ShareCalendarWithUserResponse shareCalendarWithUser(Long calendarId, ShareCalendarWithUserDto shareCalendarWithUserDto, RegisteredUser user);
}
