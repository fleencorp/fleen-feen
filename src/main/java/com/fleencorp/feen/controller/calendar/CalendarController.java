package com.fleencorp.feen.controller.calendar;

import com.fleencorp.base.model.view.search.SearchResultView;
import com.fleencorp.base.resolver.SearchParam;
import com.fleencorp.feen.model.dto.calendar.CreateCalendarDto;
import com.fleencorp.feen.model.dto.calendar.ShareCalendarWithUserDto;
import com.fleencorp.feen.model.dto.calendar.UpdateCalendarDto;
import com.fleencorp.feen.model.request.search.calendar.CalendarSearchRequest;
import com.fleencorp.feen.model.response.calendar.*;
import com.fleencorp.feen.model.security.FleenUser;
import com.fleencorp.feen.service.calendar.CalendarService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
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
  public SearchResultView findCalendars(@SearchParam final CalendarSearchRequest searchRequest) {
    return calendarService.findCalendars(searchRequest);
  }

  @GetMapping(value = "/detail/{id}")
  public RetrieveCalendarResponse findCalendar(@PathVariable(name = "id") final Long calendarId) {
    return calendarService.findCalendar(calendarId);
  }

  @PostMapping(value = "/create")
  public CreateCalendarResponse createCalendar(
      @Valid @RequestBody final CreateCalendarDto createCalendarDto,
      @AuthenticationPrincipal final FleenUser user) {
    return calendarService.createCalendar(createCalendarDto, user);
  }

  @PutMapping(value = "/update/{id}")
  public UpdateCalendarResponse updateCalendar(
      @PathVariable(name = "id") final Long calendarId,
      @Valid @RequestBody final UpdateCalendarDto updateCalendarDto,
      @AuthenticationPrincipal final FleenUser user) {
    return calendarService.updateCalendar(calendarId, updateCalendarDto, user);
  }

  @PutMapping(value = "/reactivate/{id}")
  public ReactivateCalendarResponse reactivateCalendar(
      @PathVariable(name = "id") final Long calendarId,
      @AuthenticationPrincipal final FleenUser user) {
    return calendarService.reactivateCalendar(calendarId, user);
  }

  @DeleteMapping(value = "/delete/{id}")
  public DeletedCalendarResponse deleteCalendar(
      @PathVariable(name = "id") final Long calendarId,
      @AuthenticationPrincipal final FleenUser user) {
    return calendarService.deleteCalendar(calendarId, user);
  }

  @PutMapping(value = "/share-with-user/{id}")
  public ShareCalendarWithUserResponse shareCalendarWithUser(
      @PathVariable(name = "id") final Long calendarId,
      @Valid @RequestBody final ShareCalendarWithUserDto shareCalendarWithUserDto,
      @AuthenticationPrincipal final FleenUser user) {
    return calendarService.shareCalendarWithUser(calendarId, shareCalendarWithUserDto, user);
  }
}
