package com.fleencorp.feen.exception.calendar;

import com.fleencorp.feen.exception.base.FleenException;

import java.util.Objects;

import static com.fleencorp.feen.constant.message.ResponseMessage.UNKNOWN;
import static java.lang.String.format;

public class CalendarNotFoundException extends FleenException {

  private static final String MESSAGE = "Calendar does not exist or cannot be found. ID: %s";

  public CalendarNotFoundException(final Object calendarId) {
    super(format(MESSAGE, Objects.toString(calendarId, UNKNOWN)));
  }
}
