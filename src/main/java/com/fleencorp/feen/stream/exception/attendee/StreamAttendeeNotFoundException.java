package com.fleencorp.feen.stream.exception.attendee;

import com.fleencorp.localizer.model.exception.LocalizedException;

import java.util.function.Supplier;

public class StreamAttendeeNotFoundException extends LocalizedException {

  @Override
  public String getMessageCode() {
    return "stream.attendee.not.found";
  }

  public StreamAttendeeNotFoundException(final Object...params) {
    super(params);
  }

  public static Supplier<StreamAttendeeNotFoundException> of(final Object attendeeId) {
    return () -> new StreamAttendeeNotFoundException(attendeeId);
  }
}
