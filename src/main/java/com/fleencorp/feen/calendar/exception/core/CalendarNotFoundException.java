package com.fleencorp.feen.calendar.exception.core;

import com.fleencorp.localizer.model.exception.LocalizedException;

import java.util.function.Supplier;

public class CalendarNotFoundException extends LocalizedException {

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
