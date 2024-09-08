package com.fleencorp.feen.model.response.broadcast;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.base.model.response.base.ApiResponse;
import com.fleencorp.feen.model.response.stream.FleenStreamResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
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
public class CreateStreamResponse extends ApiResponse {

  @JsonProperty("stream_id")
  protected Long streamId;

  @JsonProperty("stream")
  protected FleenStreamResponse stream;

  @Override
  public String getMessageCode() {
    return "create.stream";
  }

  public static CreateStreamResponse of(final Long streamId, final FleenStreamResponse stream) {
    return builder()
            .streamId(streamId)
            .stream(stream)
            .build();
  }
}
