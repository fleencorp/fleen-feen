package com.fleencorp.feen.model.response.stream;

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
  "attendee_id",
  "attendee_user_id",
  "full_name",
  "join_status"
})
public class StreamAttendeeResponse {

  @JsonProperty("attendee_id")
  private Long attendeeId;

  @JsonProperty("full_name")
  private String fullName;

  @JsonFormat(shape = STRING)
  @JsonProperty("request_to_join_status")
  private StreamAttendeeRequestToJoinStatus requestToJoinStatus;

  @JsonProperty("attendee_user_id")
  private Long attendeeUserId;

  public static StreamAttendeeResponse of(final Long attendeeId, final Long attendeeUserId, final String fullName) {
    return StreamAttendeeResponse.builder()
        .attendeeId(attendeeId)
        .fullName(fullName)
        .attendeeUserId(attendeeUserId)
        .build();
  }

  public static StreamAttendeeResponse of(final Long attendeeId, final Long attendeeUserId, final String fullName, final StreamAttendeeRequestToJoinStatus joinStatus) {
    final StreamAttendeeResponse streamAttendee = of(attendeeId, attendeeUserId, fullName);
    streamAttendee.setRequestToJoinStatus(joinStatus);
    return streamAttendee;
  }
}
