package com.fleencorp.feen.model.response.broadcast;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.feen.model.response.stream.FleenStreamResponse;
import lombok.AllArgsConstructor;
import lombok.Builder.Default;
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
public class UpdateStreamResponse extends CreateStreamResponse {

  @Default
  @JsonProperty("message")
  private String message = "Stream updated successfully";

  public static UpdateStreamResponse of(final Long streamId, final FleenStreamResponse stream) {
    return UpdateStreamResponse.builder()
            .streamId(streamId)
            .stream(stream)
            .build();
  }
}
