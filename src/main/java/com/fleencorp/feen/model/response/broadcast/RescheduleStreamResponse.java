package com.fleencorp.feen.model.response.broadcast;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.feen.model.response.base.FleenStreamResponse;
import lombok.*;
import lombok.Builder.Default;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
  "message",
  "stream_id",
  "stream"
})
public class RescheduleStreamResponse {

  @JsonProperty("stream_id")
  private Long streamId;

  @JsonProperty("stream")
  private FleenStreamResponse stream;

  @Default
  @JsonProperty("message")
  private String message = "Rescheduling of stream successful";

  public static RescheduleStreamResponse of(Long streamId, FleenStreamResponse stream) {
    return RescheduleStreamResponse.builder()
            .streamId(streamId)
            .stream(stream)
            .build();
  }
}
