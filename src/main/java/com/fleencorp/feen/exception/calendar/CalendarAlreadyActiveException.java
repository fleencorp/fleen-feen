package com.fleencorp.feen.exception.calendar;

import com.fleencorp.feen.exception.base.FleenException;

public class CalendarAlreadyActiveException extends FleenException {

  private static final String MESSAGE = "Calendar already active.";

  public CalendarAlreadyActiveException() {
    super(MESSAGE);
  }
}
