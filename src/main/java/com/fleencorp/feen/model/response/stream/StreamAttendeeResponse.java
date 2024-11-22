package com.fleencorp.feen.model.response.stream;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.feen.model.info.stream.AttendanceInfo;
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
  "attendance_info"
})
public class StreamAttendeeResponse {

  @JsonProperty("attendee_id")
  private Long attendeeId;

  @JsonProperty("attendee_user_id")
  private Long attendeeUserId;

  @JsonProperty("full_name")
  private String fullName;

  @JsonProperty("attendance_info")
  private AttendanceInfo attendanceInfo;

  public static StreamAttendeeResponse of(final Long attendeeId, final Long attendeeUserId, final String fullName) {
    return StreamAttendeeResponse.builder()
        .attendeeId(attendeeId)
        .fullName(fullName)
        .attendeeUserId(attendeeUserId)
        .build();
  }
}
