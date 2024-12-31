package com.fleencorp.feen.model.request.stream;

import com.fleencorp.feen.constant.stream.StreamType;
import com.fleencorp.feen.model.domain.calendar.Calendar;
import com.fleencorp.feen.model.domain.stream.FleenStream;

import static java.util.Objects.nonNull;

public record NotAttendingStreamRequest(Calendar calendar, FleenStream stream, String attendeeEmailAddress, StreamType streamType) {

  public String calendarExternalId() {
    return nonNull(calendar) ? calendar.getExternalId() : null;
  }

  public String streamExternalId() {
    return nonNull(stream) ? stream.getExternalId() : null;
  }

  public boolean isAnEvent() {
    return StreamType.isEvent(streamType);
  }

  public static NotAttendingStreamRequest of(final Calendar calendar, final FleenStream stream, final String attendeeEmailAddress, final StreamType streamType) {
    return new NotAttendingStreamRequest(calendar, stream, attendeeEmailAddress, streamType);
  }

}
