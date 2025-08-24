package com.fleencorp.feen.calendar.service;

import com.fleencorp.feen.calendar.model.dto.CreateCalendarDto;
import com.fleencorp.feen.calendar.model.dto.ShareCalendarWithUserDto;
import com.fleencorp.feen.calendar.model.dto.UpdateCalendarDto;
import com.fleencorp.feen.calendar.model.request.search.CalendarSearchRequest;
import com.fleencorp.feen.calendar.model.response.*;
import com.fleencorp.feen.calendar.model.search.CalendarSearchResult;
import com.fleencorp.feen.user.model.security.RegisteredUser;

public interface CalendarService {

  DataForCreateCalendarResponse getDataForCreateCalendar();

  CalendarSearchResult findCalendars(CalendarSearchRequest searchRequest);

  RetrieveCalendarResponse retrieveCalendar(Long calendarId);

  CreateCalendarResponse createCalendar(CreateCalendarDto createCalendarDto, RegisteredUser user);

  UpdateCalendarResponse updateCalendar(Long calendarId, UpdateCalendarDto updateCalendarDto, RegisteredUser user);

  ReactivateCalendarResponse reactivateCalendar(Long calendarId, RegisteredUser user);

  DeletedCalendarResponse deleteCalendar(Long calendarId, RegisteredUser user);

  ShareCalendarWithUserResponse shareCalendarWithUser(Long calendarId, ShareCalendarWithUserDto shareCalendarWithUserDto, RegisteredUser user);
}
