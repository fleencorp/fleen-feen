package com.fleencorp.feen.model.response.broadcast;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.feen.model.response.base.FleenStreamResponse;
import lombok.*;
import lombok.Builder.Default;
import lombok.experimental.SuperBuilder;

@SuperBuilder
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
public class CreateStreamResponse {

  @JsonProperty("stream_id")
  protected Long streamId;

  @JsonProperty("stream")
  protected FleenStreamResponse stream;

  @Default
  @JsonProperty("message")
  protected String message = "Stream created successfully";

  public static CreateStreamResponse of(final Long streamId, final FleenStreamResponse stream) {
    return builder()
            .streamId(streamId)
            .stream(stream)
            .build();
  }
}
