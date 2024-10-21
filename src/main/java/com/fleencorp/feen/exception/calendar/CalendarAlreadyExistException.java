package com.fleencorp.feen.exception.calendar;

import com.fleencorp.base.exception.FleenException;

public class CalendarAlreadyExistException extends FleenException {

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
