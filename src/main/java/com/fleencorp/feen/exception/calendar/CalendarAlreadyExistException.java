package com.fleencorp.feen.exception.calendar;

import com.fleencorp.localizer.model.exception.ApiException;

public class CalendarAlreadyExistException extends ApiException {

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
