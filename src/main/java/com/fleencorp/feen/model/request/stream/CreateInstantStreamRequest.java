package com.fleencorp.feen.model.request.stream;

import com.fleencorp.feen.constant.stream.StreamType;
import com.fleencorp.feen.model.domain.calendar.Calendar;
import com.fleencorp.feen.model.domain.stream.FleenStream;
import com.fleencorp.feen.model.dto.event.CreateInstantCalendarEventDto;

import static java.util.Objects.nonNull;

public record CreateInstantStreamRequest(Calendar calendar, FleenStream stream, StreamType streamType, CreateInstantCalendarEventDto createInstantEventDto) {

  public String calendarExternalId() {
    return nonNull(calendar) ? calendar.getExternalId() : null;
  }

  public boolean isAnEvent() {
    return StreamType.isEvent(streamType);
  }

  public static CreateInstantStreamRequest of(final Calendar calendar, final FleenStream stream, final StreamType streamType, final CreateInstantCalendarEventDto createInstantEventDto) {
    return new CreateInstantStreamRequest(calendar, stream, streamType, createInstantEventDto);
  }
}
