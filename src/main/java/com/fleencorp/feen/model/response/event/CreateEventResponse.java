package com.fleencorp.feen.model.response.event;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.feen.model.response.base.FleenStreamResponse;
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
public class CreateEventResponse {

  @JsonProperty("event_id")
  protected Long eventId;

  @JsonProperty("event")
  protected FleenStreamResponse event;

  @Default
  @JsonProperty("message")
  protected String message = "Event created successfully";

  public static CreateEventResponse of(Long eventId, EventResponse event) {
    return CreateEventResponse.builder()
            .eventId(eventId)
            .event(event)
            .build();
  }

}
