package com.fleencorp.feen.exception.calendar;

import com.fleencorp.localizer.model.exception.ApiException;

import java.util.function.Supplier;

public class CalendarNotFoundException extends ApiException {

  @Override
  public String getMessageCode() {
    return "calendar.not.found";
  }

  public CalendarNotFoundException(final Object...params) {
    super(params);
  }

  public static Supplier<CalendarNotFoundException> of(final Object calendarId) {
    return () -> new CalendarNotFoundException(calendarId);
  }
}
