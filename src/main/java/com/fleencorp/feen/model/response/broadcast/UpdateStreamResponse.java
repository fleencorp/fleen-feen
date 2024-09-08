package com.fleencorp.feen.model.response.broadcast;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.feen.model.response.stream.FleenStreamResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
  "message",
  "stream_id",
  "stream"
})
public class UpdateStreamResponse extends CreateStreamResponse {

  @Override
  public String getMessageCode() {
    return "update.stream";
  }

  public static UpdateStreamResponse of(final Long streamId, final FleenStreamResponse stream) {
    return UpdateStreamResponse.builder()
            .streamId(streamId)
            .stream(stream)
            .build();
  }
}
