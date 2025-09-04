package com.fleencorp.feen.stream.mapper.attendee;

import com.fleencorp.feen.shared.stream.contract.IsAttendee;
import com.fleencorp.feen.stream.model.response.StreamResponse;
import com.fleencorp.feen.stream.model.response.attendee.StreamAttendeeResponse;

import java.util.Collection;
import java.util.List;

public interface StreamAttendeeMapper {

  StreamAttendeeResponse toStreamAttendeeResponse(IsAttendee entry, StreamResponse streamResponse);

  StreamAttendeeResponse toStreamAttendeeResponsePublic(IsAttendee entry, StreamResponse streamResponse);

  Collection<StreamAttendeeResponse> toStreamAttendeeResponsesPublic(List<IsAttendee> entries, StreamResponse streamResponse);
}
