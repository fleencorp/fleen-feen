package com.fleencorp.feen.model.response.event;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.base.model.response.base.ApiResponse;
import com.fleencorp.feen.model.response.stream.base.FleenStreamResponse;
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
public class ProcessAttendeeRequestToJoinEventResponse extends ApiResponse {

  @JsonProperty("event_id")
  private Long eventId;

  @JsonProperty("event")
  private FleenStreamResponse event;

  @Override
  public String getMessageCode() {
    return "process.attendee.request.to.join.event";
  }

  public static ProcessAttendeeRequestToJoinEventResponse of(final Long eventId, final FleenStreamResponse event) {
    return ProcessAttendeeRequestToJoinEventResponse.builder()
            .eventId(eventId)
            .event(event)
            .build();
  }
}
