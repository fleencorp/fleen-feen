package com.fleencorp.feen.model.response.broadcast;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.feen.model.response.base.ApiResponse;
import com.fleencorp.feen.model.response.stream.FleenStreamResponse;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(Include.NON_NULL)
@JsonPropertyOrder({
  "message",
  "stream_id",
  "stream"
})
public class UpdateStreamVisibilityResponse extends ApiResponse {

  @JsonProperty("stream_id")
  private Long streamId;

  @JsonProperty("stream")
  private FleenStreamResponse stream;

  @Override
  public String getMessageKey() {
    return "update.stream.visibility";
  }

  public static UpdateStreamVisibilityResponse of(final Long streamId, final FleenStreamResponse stream) {
    return UpdateStreamVisibilityResponse.builder()
            .streamId(streamId)
            .stream(stream)
            .build();
  }
}
