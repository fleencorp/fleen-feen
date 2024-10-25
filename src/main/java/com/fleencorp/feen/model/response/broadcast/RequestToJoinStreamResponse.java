package com.fleencorp.feen.model.response.broadcast;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.base.model.response.base.ApiResponse;
import com.fleencorp.feen.constant.stream.StreamAttendeeRequestToJoinStatus;
import lombok.*;

import static com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
  "message",
  "stream_id",
  "request_to_join_status",
  "join_status"
})
public class RequestToJoinStreamResponse extends ApiResponse {

  @JsonProperty("stream_id")
  private Long streamId;

  @JsonFormat(shape = STRING)
  @JsonProperty("request_to_join_status")
  private StreamAttendeeRequestToJoinStatus requestToJoinStatus;

  @JsonProperty("join_status")
  private String joinStatus;

  @Override
  public String getMessageCode() {
    return "request.to.join.stream";
  }

  public static RequestToJoinStreamResponse of(final Long streamId, final StreamAttendeeRequestToJoinStatus requestToJoinStatus, final String joinStatus) {
    return RequestToJoinStreamResponse.builder()
            .streamId(streamId)
            .requestToJoinStatus(requestToJoinStatus)
            .joinStatus(joinStatus)
            .build();
  }
}
