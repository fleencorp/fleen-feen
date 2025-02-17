package com.fleencorp.feen.model.response.stream.attendee;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.feen.model.info.stream.attendance.AttendanceInfo;
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
  "display_photo",
  "comment",
  "organizer_comment",
  "attendance_info"
})
public class StreamAttendeeResponse {

  @JsonProperty("attendee_id")
  private Long attendeeId;

  @JsonProperty("attendee_user_id")
  private Long attendeeMemberId;

  @JsonProperty("full_name")
  private String fullName;

  @JsonProperty("attendance_info")
  private AttendanceInfo attendanceInfo;

  @JsonProperty("display_photo")
  private String displayPhoto;

  @JsonProperty("comment")
  private String comment;

  @JsonProperty("organizer_comment")
  private String organizerComment;

  public static StreamAttendeeResponse of(final Long attendeeId, final Long attendeeMemberId, final String fullName) {
    final StreamAttendeeResponse attendeeResponse = new StreamAttendeeResponse();
    attendeeResponse.setAttendeeId(attendeeId);
    attendeeResponse.setAttendeeMemberId(attendeeMemberId);
    attendeeResponse.setFullName(fullName);

    return attendeeResponse;
  }
}
