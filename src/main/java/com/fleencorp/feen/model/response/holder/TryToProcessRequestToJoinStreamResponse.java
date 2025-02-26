package com.fleencorp.feen.model.response.holder;

import com.fleencorp.feen.model.domain.stream.FleenStream;
import com.fleencorp.feen.model.domain.stream.StreamAttendee;

public record TryToProcessRequestToJoinStreamResponse(FleenStream stream, StreamAttendee attendee) {

  public static TryToProcessRequestToJoinStreamResponse of(final FleenStream stream, final StreamAttendee attendee) {
    return new TryToProcessRequestToJoinStreamResponse(stream, attendee);
  }

  public static TryToProcessRequestToJoinStreamResponse of() {
    return new TryToProcessRequestToJoinStreamResponse(null, null);
  }
}
