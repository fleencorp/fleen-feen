package com.fleencorp.feen.controller;

import com.fleencorp.base.model.view.search.SearchResultView;
import com.fleencorp.base.resolver.SearchParam;
import com.fleencorp.feen.model.dto.calendar.CreateCalendarDto;
import com.fleencorp.feen.model.dto.calendar.ShareCalendarWithUserDto;
import com.fleencorp.feen.model.dto.calendar.UpdateCalendarDto;
import com.fleencorp.feen.model.request.search.calendar.CalendarSearchRequest;
import com.fleencorp.feen.model.response.calendar.*;
import com.fleencorp.feen.model.response.other.DeleteResponse;
import com.fleencorp.feen.service.calendar.CalendarService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/calendar")
public class CalendarController {

  private final CalendarService calendarService;

  public CalendarController(final CalendarService calendarService) {
    this.calendarService = calendarService;
  }

  @GetMapping(value = "/data-create-calendar")
  public DataForCreateCalendarResponse getDataCreateCalendar() {
    return calendarService.getDataForCreateCalendar();
  }

  @GetMapping(value = "/entries")
  public SearchResultView findCalendars(@SearchParam CalendarSearchRequest searchRequest) {
    return calendarService.findCalendars(searchRequest);
  }

  @GetMapping(value = "/detail/{id}")
  public RetrieveCalendarResponse findCalendar(@PathVariable(name = "id") Long calendarId) {
    return calendarService.findCalendar(calendarId);
  }

  @PostMapping(value = "/create")
  public CreateCalendarResponse createCalendar(@Valid @RequestBody CreateCalendarDto createCalendarDto) {
    return calendarService.createCalendar(createCalendarDto);
  }

  @PutMapping(value = "/update/{id}")
  public UpdateCalendarResponse updateCalendar(
      @PathVariable(name = "id") Long calendarId,
      @Valid @RequestBody UpdateCalendarDto updateCalendarDto) {
    return calendarService.updateCalendar(calendarId, updateCalendarDto);
  }

  @DeleteMapping(value = "/delete/{id}")
  public DeleteResponse deleteCalendar(@PathVariable(name = "id") Long calendarId) {
    return calendarService.deleteCalendar(calendarId);
  }

  @PutMapping(value = "/share-with-user/{id}")
  public ShareCalendarWithUserResponse shareCalendarWithUser(
      @PathVariable(name = "id") Long calendarId,
      @Valid @RequestBody ShareCalendarWithUserDto shareCalendarWithUserDto) {
    return calendarService.shareCalendarWithUser(calendarId, shareCalendarWithUserDto);
  }
}
