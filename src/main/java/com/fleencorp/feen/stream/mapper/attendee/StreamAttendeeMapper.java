package com.fleencorp.feen.stream.mapper.attendee;

import com.fleencorp.feen.stream.model.domain.StreamAttendee;
import com.fleencorp.feen.stream.model.response.StreamResponse;
import com.fleencorp.feen.stream.model.response.attendee.StreamAttendeeResponse;

import java.util.Collection;
import java.util.List;

public interface StreamAttendeeMapper {

  StreamAttendeeResponse toStreamAttendeeResponse(StreamAttendee entry, StreamResponse streamResponse);

  StreamAttendeeResponse toStreamAttendeeResponsePublic(StreamAttendee entry, StreamResponse streamResponse);

  Collection<StreamAttendeeResponse> toStreamAttendeeResponsesPublic(List<StreamAttendee> entries, StreamResponse streamResponse);
}
