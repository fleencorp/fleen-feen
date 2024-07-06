package com.fleencorp.feen.controller;

import com.fleencorp.feen.service.CalendarService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/calendar")
public class CalendarController {

  private final CalendarService calendarService;

  public CalendarController(final CalendarService calendarService) {
    this.calendarService = calendarService;
  }
}
