package com.fleencorp.feen.model.response.stream;

import com.fleencorp.feen.model.response.stream.base.FleenStreamResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
public class RetrieveEventOrStreamResponse {

  private FleenStreamResponse stream;
  private Set<StreamAttendeeResponse> attendees;
  private Long totalAttending;

  public static RetrieveEventOrStreamResponse of(final FleenStreamResponse stream, final Set<StreamAttendeeResponse> attendees, final Long totalAttending) {
    return new RetrieveEventOrStreamResponse(stream, attendees, totalAttending);
  }
}
