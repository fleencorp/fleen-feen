package com.fleencorp.feen.exception.calendar;

import com.fleencorp.localizer.model.exception.LocalizedException;

public class CalendarAlreadyActiveException extends LocalizedException {

  @Override
  public String getMessageCode() {
    return "calendar.already.active";
  }
}
