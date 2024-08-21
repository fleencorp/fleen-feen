package com.fleencorp.feen.model.response.event;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.feen.model.response.event.base.EventResponse;
import com.fleencorp.feen.model.response.stream.FleenStreamResponse;
import lombok.*;
import lombok.Builder.Default;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(Include.NON_NULL)
@JsonPropertyOrder({
  "message",
  "event_id",
  "event"
})
public class UpdateEventVisibilityResponse {

  @JsonProperty("event_id")
  private Long eventId;

  @JsonProperty("event")
  private FleenStreamResponse event;

  @Default
  @JsonProperty("message")
  private String message = "Event visibility updated successfully";

  public static UpdateEventVisibilityResponse of(final Long eventId, final EventResponse event) {
    return UpdateEventVisibilityResponse.builder()
            .eventId(eventId)
            .event(event)
            .build();
  }
}
