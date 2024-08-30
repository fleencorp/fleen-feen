package com.fleencorp.feen.model.response.event;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.feen.model.response.base.ApiResponse;
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
public class CancelEventResponse extends ApiResponse {

  @JsonProperty("event_id")
  private Long eventId;

  @Override
  public String getMessageKey() {
    return "cancel.event";
  }

  public static CancelEventResponse of(final long eventId) {
    return builder()
            .eventId(eventId)
            .build();
  }
}
