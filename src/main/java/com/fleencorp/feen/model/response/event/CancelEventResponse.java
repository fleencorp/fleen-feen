package com.fleencorp.feen.model.response.event;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
  "message",
  "event_id"
})
public class CancelEventResponse {

  @JsonProperty("event_id")
  private Long eventId;

  @Builder.Default
  @JsonProperty("message")
  private String message = "Event cancelled successfully";

  public static CancelEventResponse of(final long eventId) {
    return builder()
            .eventId(eventId)
            .build();
  }
}
