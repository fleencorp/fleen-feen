package com.fleencorp.feen.exception.calendar;

import com.fleencorp.localizer.model.exception.ApiException;

public class CalendarAlreadyActiveException extends ApiException {

  @Override
  public String getMessageCode() {
    return "calendar.already.active";
  }
}
