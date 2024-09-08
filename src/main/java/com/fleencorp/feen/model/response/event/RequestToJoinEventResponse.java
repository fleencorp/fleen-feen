package com.fleencorp.feen.model.response.event;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.base.model.response.base.ApiResponse;
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
public class RequestToJoinEventResponse extends ApiResponse {

  @JsonProperty("event_id")
  private Long eventId;

  @Override
  public String getMessageCode() {
    return "request.to.join.event";
  }

  public static RequestToJoinEventResponse of(final Long eventId) {
    return RequestToJoinEventResponse.builder()
            .eventId(eventId)
            .build();
  }
}
