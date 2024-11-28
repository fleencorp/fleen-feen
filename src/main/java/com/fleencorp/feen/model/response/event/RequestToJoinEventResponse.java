package com.fleencorp.feen.model.response.event;

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
  "event_id",
  "request_to_join_status_info",
  "join_status_info"
})
public class RequestToJoinEventResponse extends ApiResponse {

  @JsonProperty("event_id")
  private Long eventId;

  @JsonProperty("request_to_join_status_info")
  private StreamAttendeeRequestToJoinStatusInfo requestToJoinStatusInfo;

  @JsonProperty("join_status_info")
  private JoinStatusInfo joinStatusInfo;

  @Override
  public String getMessageCode() {
    return "request.to.join.event";
  }

  public static RequestToJoinEventResponse of(final Long eventId, final StreamAttendeeRequestToJoinStatusInfo requestToJoinStatusInfo, final JoinStatusInfo joinStatusInfo) {
    return RequestToJoinEventResponse.builder()
            .eventId(eventId)
            .requestToJoinStatusInfo(requestToJoinStatusInfo)
            .joinStatusInfo(joinStatusInfo)
            .build();
  }
}
