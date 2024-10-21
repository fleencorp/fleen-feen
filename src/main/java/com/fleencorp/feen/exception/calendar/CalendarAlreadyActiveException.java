package com.fleencorp.feen.exception.calendar;

import com.fleencorp.base.exception.FleenException;

public class CalendarAlreadyActiveException extends FleenException {

  @Override
  public String getMessageCode() {
    return "calendar.already.active";
  }
}
