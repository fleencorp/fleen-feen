package com.fleencorp.feen.model.response.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.*;
import lombok.Builder.Default;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonPropertyOrder({
  "message",
  "event_id"
})
public class DeleteEventResponse {

  @JsonProperty("event_id")
  private Long eventId;

  @Default
  @JsonProperty("message")
  private String message = "Event deleted successfully";

  public static DeleteEventResponse of(final long eventId) {
    return DeleteEventResponse.builder()
            .eventId(eventId)
            .build();
  }
}
