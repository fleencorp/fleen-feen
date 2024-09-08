package com.fleencorp.feen.model.response.broadcast;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.base.model.response.base.ApiResponse;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
  "message",
  "stream_id"
})
public class RequestToJoinStreamResponse extends ApiResponse {

  @JsonProperty("stream_id")
  private Long streamId;

  @Override
  public String getMessageCode() {
    return "request.to.join.stream";
  }

  public static RequestToJoinStreamResponse of(final Long streamId) {
    return RequestToJoinStreamResponse.builder()
            .streamId(streamId)
            .build();
  }
}
