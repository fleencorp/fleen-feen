package com.fleencorp.feen.model.response.holder;

import com.fleencorp.feen.model.domain.stream.FleenStream;
import com.fleencorp.feen.model.domain.stream.StreamAttendee;
import com.fleencorp.feen.model.info.stream.attendance.AttendanceInfo;

public record TryToJoinPublicStreamResponse(FleenStream stream, StreamAttendee attendee, AttendanceInfo attendanceInfo) {

  public static TryToJoinPublicStreamResponse of(final FleenStream stream, final StreamAttendee attendee, final AttendanceInfo attendanceInfo) {
    return new TryToJoinPublicStreamResponse(stream, attendee, attendanceInfo);
  }
}
