package com.fleencorp.feen.mapper.stream.attendee;

import com.fleencorp.feen.model.domain.stream.StreamAttendee;
import com.fleencorp.feen.model.response.stream.StreamResponse;
import com.fleencorp.feen.model.response.stream.attendee.StreamAttendeeResponse;

import java.util.Collection;
import java.util.List;

public interface StreamAttendeeMapper {

  StreamAttendeeResponse toStreamAttendeeResponse(StreamAttendee entry, StreamResponse streamResponse);

  StreamAttendeeResponse toStreamAttendeeResponsePublic(StreamAttendee entry, StreamResponse streamResponse);

  Collection<StreamAttendeeResponse> toStreamAttendeeResponsesPublic(List<StreamAttendee> entries, StreamResponse streamResponse);
}
