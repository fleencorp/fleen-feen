package com.fleencorp.feen.model.response.stream.attendee;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.feen.model.info.stream.attendance.AttendanceInfo;
import com.fleencorp.feen.model.info.stream.attendee.IsOrganizerInfo;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
  "attendee_id",
  "username",
  "full_name",
  "display_photo",
  "comment",
  "organizer_comment",
  "is_organizer_info",
  "attendance_info"
})
public class StreamAttendeeResponse {

  @JsonProperty("attendee_id")
  private Long attendeeId;

  @JsonProperty("username")
  private String username;

  @JsonProperty("full_name")
  private String fullName;

  @JsonProperty("is_organizer_info")
  private IsOrganizerInfo isOrganizerInfo;

  @JsonProperty("attendance_info")
  private AttendanceInfo attendanceInfo;

  @JsonProperty("display_photo")
  private String displayPhoto;

  @JsonProperty("comment")
  private String comment;

  @JsonProperty("organizer_comment")
  private String organizerComment;
}
