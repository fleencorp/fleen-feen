package com.fleencorp.feen.model.response.event;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.feen.model.response.event.base.EventResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@Setter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
  "message",
  "event_id",
  "event"
})
public class UpdateEventResponse extends CreateEventResponse {

  @Override
  public String getMessageCode() {
    return "update.event";
  }

  public static UpdateEventResponse of(final Long eventId, final EventResponse event) {
    return UpdateEventResponse.builder()
            .eventId(eventId)
            .event(event)
            .build();
  }
}
