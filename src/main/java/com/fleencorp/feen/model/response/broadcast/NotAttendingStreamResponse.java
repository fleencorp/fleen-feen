package com.fleencorp.feen.model.response.broadcast;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.base.model.response.base.ApiResponse;
import com.fleencorp.feen.model.info.stream.attendee.IsAttendingInfo;
import com.fleencorp.feen.model.info.JoinStatusInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
  "message",
  "is_attending_info",
  "join_status_info",
})
public class NotAttendingStreamResponse extends ApiResponse {

  @JsonProperty("is_attending_info")
  private IsAttendingInfo isAttendingInfo;

  @JsonProperty("join_status_info")
  private JoinStatusInfo joinStatusInfo;

  @Override
  public String getMessageCode() {
    return "not.attending.stream";
  }

  public static NotAttendingStreamResponse of() {
    return NotAttendingStreamResponse.builder().build();
  }
}
