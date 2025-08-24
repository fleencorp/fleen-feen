package com.fleencorp.feen.calendar.controller;

import com.fleencorp.base.resolver.SearchParam;
import com.fleencorp.feen.calendar.constant.CalendarStatus;
import com.fleencorp.feen.calendar.model.dto.CreateCalendarDto;
import com.fleencorp.feen.calendar.model.dto.ShareCalendarWithUserDto;
import com.fleencorp.feen.calendar.model.dto.UpdateCalendarDto;
import com.fleencorp.feen.calendar.model.request.search.CalendarSearchRequest;
import com.fleencorp.feen.calendar.model.response.*;
import com.fleencorp.feen.calendar.model.search.CalendarSearchResult;
import com.fleencorp.feen.calendar.service.CalendarService;
import com.fleencorp.feen.user.model.security.RegisteredUser;
import jakarta.validation.Valid;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/calendar")
@PreAuthorize("hasAnyRole('ADMINISTRATOR', 'SUPER_ADMINISTRATOR')")
public class CalendarController {

  private final CalendarService calendarService;

  public CalendarController(final CalendarService calendarService) {
    this.calendarService = calendarService;
  }

  @GetMapping(value = "/data-create-calendar")
  @Cacheable(value = "data-required-to-create-calendar")
  public DataForCreateCalendarResponse getDataCreateCalendar() {
    return calendarService.getDataForCreateCalendar();
  }

  @GetMapping(value = "/entries")
  public CalendarSearchResult findCalendars(@SearchParam final CalendarSearchRequest searchRequest) {
    searchRequest.setStatus(CalendarStatus.ACTIVE);
    return calendarService.findCalendars(searchRequest);
  }

  @GetMapping(value = "/detail/{calendarId}")
  public RetrieveCalendarResponse findCalendar(@PathVariable(name = "calendarId") final Long calendarId) {
    return calendarService.retrieveCalendar(calendarId);
  }

  @PostMapping(value = "/create")
  public CreateCalendarResponse createCalendar(
      @Valid @RequestBody final CreateCalendarDto createCalendarDto,
      @AuthenticationPrincipal final RegisteredUser user) {
    return calendarService.createCalendar(createCalendarDto, user);
  }

  @PutMapping(value = "/update/{calendarId}")
  public UpdateCalendarResponse updateCalendar(
      @PathVariable(name = "calendarId") final Long calendarId,
      @Valid @RequestBody final UpdateCalendarDto updateCalendarDto,
      @AuthenticationPrincipal final RegisteredUser user) {
    return calendarService.updateCalendar(calendarId, updateCalendarDto, user);
  }

  @PutMapping(value = "/reactivate/{calendarId}")
  public ReactivateCalendarResponse reactivateCalendar(
      @PathVariable(name = "calendarId") final Long calendarId,
      @AuthenticationPrincipal final RegisteredUser user) {
    return calendarService.reactivateCalendar(calendarId, user);
  }

  @DeleteMapping(value = "/delete/{calendarId}")
  public DeletedCalendarResponse deleteCalendar(
      @PathVariable(name = "calendarId") final Long calendarId,
      @AuthenticationPrincipal final RegisteredUser user) {
    return calendarService.deleteCalendar(calendarId, user);
  }

  @PutMapping(value = "/share-with-user/{calendarId}")
  public ShareCalendarWithUserResponse shareCalendarWithUser(
      @PathVariable(name = "calendarId") final Long calendarId,
      @Valid @RequestBody final ShareCalendarWithUserDto shareCalendarWithUserDto,
      @AuthenticationPrincipal final RegisteredUser user) {
    return calendarService.shareCalendarWithUser(calendarId, shareCalendarWithUserDto, user);
  }
}
