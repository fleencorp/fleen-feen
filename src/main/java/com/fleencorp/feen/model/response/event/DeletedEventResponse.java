package com.fleencorp.feen.model.response.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.base.model.response.base.ApiResponse;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonPropertyOrder({
  "message",
  "event_id"
})
public class DeletedEventResponse extends ApiResponse {

  @JsonProperty("event_id")
  private Long eventId;

  @Override
  public String getMessageCode() {
    return "deleted.event";
  }

  public static DeletedEventResponse of(final long eventId) {
    return DeletedEventResponse.builder()
            .eventId(eventId)
            .build();
  }
}
