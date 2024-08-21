package com.fleencorp.feen.model.response.event;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.feen.model.response.stream.FleenStreamResponse;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
  "message",
  "event_id",
  "event"
})
public class ProcessAttendeeRequestToJoinEventResponse {

  @JsonProperty("event_id")
  private Long eventId;

  @JsonProperty("event")
  private FleenStreamResponse event;

  @Builder.Default
  @JsonProperty("message")
  private String message = "Attendee request to join event processed successfully";

  public static ProcessAttendeeRequestToJoinEventResponse of(final Long eventId, final FleenStreamResponse event) {
    return ProcessAttendeeRequestToJoinEventResponse.builder()
            .eventId(eventId)
            .event(event)
            .build();
  }
}
