package com.fleencorp.feen.stream.mapper.common;

import com.fleencorp.feen.shared.stream.contract.IsAttendee;
import com.fleencorp.feen.stream.model.domain.StreamAttendee;
import com.fleencorp.feen.stream.model.response.StreamResponse;
import com.fleencorp.feen.stream.model.response.attendance.NotAttendingStreamResponse;
import com.fleencorp.feen.stream.model.response.attendance.ProcessAttendeeRequestToJoinStreamResponse;

public interface StreamCommonMapper {

  NotAttendingStreamResponse notAttendingStream();

  ProcessAttendeeRequestToJoinStreamResponse processAttendeeRequestToJoinStream(StreamResponse stream, IsAttendee attendee);
}
