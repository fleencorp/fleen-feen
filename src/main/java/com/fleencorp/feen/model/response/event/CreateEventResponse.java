package com.fleencorp.feen.model.response.event;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.base.model.response.base.ApiResponse;
import com.fleencorp.feen.model.response.stream.FleenStreamResponse;
import lombok.AllArgsConstructor;
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
public class CreateEventResponse extends ApiResponse {

  @JsonProperty("event_id")
  protected Long eventId;

  @JsonProperty("event")
  protected FleenStreamResponse event;

  @Override
  public String getMessageCode() {
    return "create.event";
  }

  public static CreateEventResponse of(final Long eventId, final FleenStreamResponse event) {
    return CreateEventResponse.builder()
            .eventId(eventId)
            .event(event)
            .build();
  }

}
