package com.fleencorp.feen.exception.stream.attendee;

import com.fleencorp.localizer.model.exception.ApiException;

import java.util.function.Supplier;

public class StreamAttendeeNotFoundException extends ApiException {

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
