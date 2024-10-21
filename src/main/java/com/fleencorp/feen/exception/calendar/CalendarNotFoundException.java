package com.fleencorp.feen.exception.calendar;

import com.fleencorp.base.exception.FleenException;

import java.util.function.Supplier;

public class CalendarNotFoundException extends FleenException {

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
