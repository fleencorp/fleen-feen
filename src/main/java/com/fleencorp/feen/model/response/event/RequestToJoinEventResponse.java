package com.fleencorp.feen.model.response.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.base.model.response.base.ApiResponse;
import com.fleencorp.feen.constant.stream.StreamAttendeeRequestToJoinStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import static com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
  "message",
  "event_id",
  "request_to_join_status",
  "join_status"
})
public class RequestToJoinEventResponse extends ApiResponse {

  @JsonProperty("event_id")
  private Long eventId;

  @JsonFormat(shape = STRING)
  @JsonProperty("request_to_join_status")
  private StreamAttendeeRequestToJoinStatus requestToJoinStatus;

  @JsonProperty("join_status")
  private String joinStatus;

  @Override
  public String getMessageCode() {
    return "request.to.join.event";
  }

  public static RequestToJoinEventResponse of(final Long eventId, final StreamAttendeeRequestToJoinStatus requestToJoinStatus, final String joinStatus) {
    return RequestToJoinEventResponse.builder()
            .eventId(eventId)
            .requestToJoinStatus(requestToJoinStatus)
            .joinStatus(joinStatus)
            .build();
  }
}
