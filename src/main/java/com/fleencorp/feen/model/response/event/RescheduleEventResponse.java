package com.fleencorp.feen.model.response.event;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.feen.model.response.base.FleenStreamResponse;
import lombok.*;
import lombok.Builder.Default;

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
public class RescheduleEventResponse {

  @JsonProperty("event_id")
  private Long eventId;

  @JsonProperty("event")
  private FleenStreamResponse event;

  @Default
  @JsonProperty("message")
  private String message = "Rescheduling of event successful";

  public static RescheduleEventResponse of(final Long eventId, final FleenStreamResponse event) {
    return RescheduleEventResponse.builder()
            .eventId(eventId)
            .event(event)
            .build();
  }
}
