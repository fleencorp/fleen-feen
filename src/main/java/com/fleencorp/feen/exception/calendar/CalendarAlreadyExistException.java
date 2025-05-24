package com.fleencorp.feen.exception.calendar;

import com.fleencorp.localizer.model.exception.LocalizedException;

public class CalendarAlreadyExistException extends LocalizedException {

  @Override
  public String getMessageCode() {
    return "calendar.already.exist";
  }

  public CalendarAlreadyExistException(final Object...params) {
    super(params);
  }

  public static CalendarAlreadyExistException of(final Object calendarCode) {
    return new CalendarAlreadyExistException(calendarCode);
  }
}
