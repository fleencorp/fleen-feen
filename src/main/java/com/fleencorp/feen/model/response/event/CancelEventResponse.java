package com.fleencorp.feen.model.response.event;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.base.model.response.base.ApiResponse;
import com.fleencorp.feen.model.info.stream.StreamStatusInfo;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
  "message",
  "event_id",
  "stream_status_info"
})
public class CancelEventResponse extends ApiResponse {

  @JsonProperty("event_id")
  private Long eventId;

  @JsonProperty("stream_status_info")
  private StreamStatusInfo streamStatusInfo;

  @Override
  public String getMessageCode() {
    return "cancel.event";
  }

  public static CancelEventResponse of(final long eventId, final StreamStatusInfo streamStatusInfo) {
    return CancelEventResponse.builder()
            .eventId(eventId)
            .streamStatusInfo(streamStatusInfo)
            .build();
  }
}
