package com.fleencorp.feen.model.response.event;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.base.model.response.base.ApiResponse;
import com.fleencorp.feen.model.info.stream.StreamVisibilityInfo;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(Include.NON_NULL)
@JsonPropertyOrder({
  "message",
  "event_id",
  "visibility_info"
})
public class UpdateEventVisibilityResponse extends ApiResponse {

  @JsonProperty("event_id")
  private Long eventId;

  @JsonProperty("visibility_info")
  private StreamVisibilityInfo streamVisibilityInfo;

  @Override
  public String getMessageCode() {
    return "update.event.visibility";
  }

  public static UpdateEventVisibilityResponse of(final Long eventId, final StreamVisibilityInfo streamVisibilityInfo) {
    return UpdateEventVisibilityResponse.builder()
            .eventId(eventId)
            .streamVisibilityInfo(streamVisibilityInfo)
            .build();
  }
}
