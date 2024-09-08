package com.fleencorp.feen.model.response.broadcast;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.base.model.response.base.ApiResponse;
import com.fleencorp.feen.model.response.stream.FleenStreamResponse;
import lombok.*;

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
public class ProcessAttendeeRequestToJoinStreamResponse extends ApiResponse {

  @JsonProperty("stream_id")
  private Long streamId;

  @JsonProperty("stream")
  private FleenStreamResponse stream;

  @Override
  public String getMessageCode() {
    return "process.attendee.request.to.join.stream";
  }

  public static ProcessAttendeeRequestToJoinStreamResponse of(final Long streamId, final FleenStreamResponse stream) {
    return ProcessAttendeeRequestToJoinStreamResponse.builder()
            .streamId(streamId)
            .stream(stream)
            .build();
  }
}
