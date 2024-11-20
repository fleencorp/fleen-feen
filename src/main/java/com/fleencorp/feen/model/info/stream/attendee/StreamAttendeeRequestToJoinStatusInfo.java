package com.fleencorp.feen.model.info.stream.attendee;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
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
  "request_to_join_status",
  "request_to_join_status_text"
})
public class StreamAttendeeRequestToJoinStatusInfo {

  @JsonFormat(shape = STRING)
  @JsonProperty("request_to_join_status")
  private StreamAttendeeRequestToJoinStatus requestToJoinStatus;

  @JsonProperty("request_to_join_status_text")
  private String requestToJoinStatusText;

  public static StreamAttendeeRequestToJoinStatusInfo of(final StreamAttendeeRequestToJoinStatus requestToJoinStatus, final String requestToJoinStatusText) {
    return StreamAttendeeRequestToJoinStatusInfo.builder()
      .requestToJoinStatus(requestToJoinStatus)
      .requestToJoinStatusText(requestToJoinStatusText)
      .build();
  }

  public static StreamAttendeeRequestToJoinStatusInfo of() {
    return new StreamAttendeeRequestToJoinStatusInfo();
  }
}
