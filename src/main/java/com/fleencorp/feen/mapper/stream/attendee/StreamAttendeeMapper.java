package com.fleencorp.feen.mapper.stream.attendee;

import com.fleencorp.feen.model.domain.stream.StreamAttendee;
import com.fleencorp.feen.model.response.stream.FleenStreamResponse;
import com.fleencorp.feen.model.response.stream.attendee.StreamAttendeeResponse;

public interface StreamAttendeeMapper {
  StreamAttendeeResponse toStreamAttendeeResponse(StreamAttendee entry, FleenStreamResponse streamResponse);
}
