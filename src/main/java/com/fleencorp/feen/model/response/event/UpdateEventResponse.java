package com.fleencorp.feen.model.response.event;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.feen.model.response.event.base.EventResponse;
import lombok.AllArgsConstructor;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
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
public class UpdateEventResponse extends CreateEventResponse {

  @Default
  @JsonProperty("message")
  private String message = "Event updated successfully";

  public static UpdateEventResponse of(Long eventId, EventResponse event) {
    return UpdateEventResponse.builder()
            .eventId(eventId)
            .event(event)
            .build();
  }
}
