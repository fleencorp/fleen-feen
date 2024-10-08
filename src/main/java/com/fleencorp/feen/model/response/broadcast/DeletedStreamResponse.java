package com.fleencorp.feen.model.response.broadcast;

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
  "stream_id"
})
public class DeletedStreamResponse extends ApiResponse {

  @JsonProperty("stream_id")
  private Long streamId;

  @Override
  public String getMessageCode() {
    return "deleted.stream";
  }

  public static DeletedStreamResponse of(final long streamId) {
    return DeletedStreamResponse.builder()
            .streamId(streamId)
            .build();
  }
}
