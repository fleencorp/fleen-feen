package com.fleencorp.feen.model.response.broadcast;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.base.model.response.base.ApiResponse;
import com.fleencorp.feen.model.info.JoinStatusInfo;
import com.fleencorp.feen.model.info.stream.attendee.StreamAttendeeRequestToJoinStatusInfo;
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
  "request_to_join_status_info",
  "join_status_info"
})
public class JoinStreamResponse extends ApiResponse {

  @JsonProperty("stream_id")
  private Long streamId;

  @JsonProperty("request_to_join_status_info")
  private StreamAttendeeRequestToJoinStatusInfo requestToJoinStatusInfo;

  @JsonProperty("join_status_info")
  private JoinStatusInfo joinStatusInfo;

  @Override
  public String getMessageCode() {
    return "join.stream";
  }

  public static JoinStreamResponse of(final Long streamId, final StreamAttendeeRequestToJoinStatusInfo requestToJoinStatusInfo, final JoinStatusInfo joinStatusInfo) {
    return JoinStreamResponse.builder()
      .streamId(streamId)
      .requestToJoinStatusInfo(requestToJoinStatusInfo)
      .joinStatusInfo(joinStatusInfo)
      .build();
  }
}
