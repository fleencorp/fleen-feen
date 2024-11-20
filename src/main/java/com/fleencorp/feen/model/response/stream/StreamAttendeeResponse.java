package com.fleencorp.feen.model.response.stream;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.feen.model.info.stream.attendee.StreamAttendeeRequestToJoinStatusInfo;
import com.fleencorp.feen.model.info.JoinStatusInfo;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
  "attendee_id",
  "attendee_user_id",
  "full_name",
  "request_to_join_status_info",
  "join_status_info"
})
public class StreamAttendeeResponse {

  @JsonProperty("attendee_id")
  private Long attendeeId;

  @JsonProperty("full_name")
  private String fullName;

  @JsonProperty("request_to_join_status_info")
  private StreamAttendeeRequestToJoinStatusInfo requestToJoinStatusInfo;

  @JsonProperty("join_status_info")
  private JoinStatusInfo joinStatusInfo;

  @JsonProperty("attendee_user_id")
  private Long attendeeUserId;

  public static StreamAttendeeResponse of(final Long attendeeId, final Long attendeeUserId, final String fullName) {
    return StreamAttendeeResponse.builder()
        .attendeeId(attendeeId)
        .fullName(fullName)
        .attendeeUserId(attendeeUserId)
        .build();
  }

  public static StreamAttendeeResponse of(final Long attendeeId, final Long attendeeMemberId, final String fullName,
      final StreamAttendeeRequestToJoinStatusInfo requestToJoinStatusInfo, final JoinStatusInfo joinStatusInfo) {
    final StreamAttendeeResponse streamAttendee = of(attendeeId, attendeeMemberId, fullName);
    streamAttendee.setRequestToJoinStatusInfo(requestToJoinStatusInfo);
    streamAttendee.setJoinStatusInfo(joinStatusInfo);
    return streamAttendee;
  }
}
