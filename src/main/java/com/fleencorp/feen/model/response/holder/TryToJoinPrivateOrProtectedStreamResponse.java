package com.fleencorp.feen.model.response.holder;

import com.fleencorp.feen.model.domain.stream.FleenStream;
import com.fleencorp.feen.model.domain.stream.StreamAttendee;
import com.fleencorp.feen.model.info.stream.attendance.AttendanceInfo;

public record TryToJoinPrivateOrProtectedStreamResponse(FleenStream stream, StreamAttendee attendee, AttendanceInfo attendanceInfo) {

  public static TryToJoinPrivateOrProtectedStreamResponse of(final FleenStream stream, final StreamAttendee attendee, final AttendanceInfo attendanceInfo) {
    return new TryToJoinPrivateOrProtectedStreamResponse(stream, attendee, attendanceInfo);
  }
}
