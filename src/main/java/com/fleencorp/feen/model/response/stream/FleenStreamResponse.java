package com.fleencorp.feen.model.response.stream;

import com.fasterxml.jackson.annotation.*;
import com.fleencorp.feen.constant.security.mask.MaskedStreamLinkUri;
import com.fleencorp.feen.constant.stream.JoinStatus;
import com.fleencorp.feen.constant.stream.StreamType;
import com.fleencorp.feen.constant.stream.StreamVisibility;
import com.fleencorp.feen.model.info.IsDeletedInfo;
import com.fleencorp.feen.model.info.IsForKidsInfo;
import com.fleencorp.feen.model.info.schedule.ScheduleTimeTypeInfo;
import com.fleencorp.feen.model.info.stream.StreamSourceInfo;
import com.fleencorp.feen.model.info.stream.StreamStatusInfo;
import com.fleencorp.feen.model.info.stream.StreamTypeInfo;
import com.fleencorp.feen.model.info.stream.StreamVisibilityInfo;
import com.fleencorp.feen.model.info.stream.attendance.AttendanceInfo;
import com.fleencorp.feen.model.other.Organizer;
import com.fleencorp.feen.model.other.Schedule;
import com.fleencorp.feen.model.response.base.FleenFeenResponse;
import com.fleencorp.feen.model.response.stream.attendee.StreamAttendeeResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.Set;

import static com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING;
import static java.util.Objects.nonNull;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
  "id",
  "message",
  "title",
  "description",
  "tags",
  "location",
  "schedule",
  "other_schedule",
  "created_on",
  "updated_on",
  "organizer",
  "is_for_kids_info",
  "stream_link",
  "stream_type_info",
  "stream_source_info",
  "stream_status_info",
  "visibility_info",
  "is_deleted_info",
  "status",
  "schedule_time_type_info",
  "total_attending",
  "some_attendees",
  "is_private",
  "attendance_info"
})
public class FleenStreamResponse extends FleenFeenResponse {

  @JsonProperty("title")
  private String title;

  @JsonProperty("description")
  private String description;

  @JsonProperty("tags")
  private String tags;

  @JsonProperty("location")
  private String location;

  @JsonProperty("organizer")
  private Organizer organizer;

  @JsonProperty("is_for_kids_info")
  private IsForKidsInfo forKidsIno;

  @JsonFormat(shape = STRING)
  @JsonProperty("stream_link")
  private MaskedStreamLinkUri streamLink;

  @JsonProperty("stream_type_info")
  private StreamTypeInfo streamTypeInfo;

  @JsonProperty("stream_source_info")
  private StreamSourceInfo streamSourceInfo;

  @JsonProperty("stream_status_info")
  private StreamStatusInfo streamStatusInfo;

  @JsonProperty("visibility_info")
  private StreamVisibilityInfo streamVisibilityInfo;

  @JsonProperty("schedule_time_type_info")
  private ScheduleTimeTypeInfo scheduleTimeTypeInfo;

  @JsonProperty("schedule")
  private Schedule schedule;

  @JsonProperty("other_schedule")
  private Schedule otherSchedule;

  @JsonProperty("total_attending")
  private long totalAttending;

  @JsonProperty("some_attendees")
  private Set<StreamAttendeeResponse> someAttendees;

  @JsonProperty("attendance_info")
  private AttendanceInfo attendanceInfo;

  @JsonProperty("is_deleted_info")
  private IsDeletedInfo deletedInfo;

  @JsonProperty("is_private")
  public boolean isPrivate() {
    return StreamVisibility.isPrivateOrProtected(getVisibility());
  }

  @JsonProperty("stream_link_unmasked")
  public String getStreamLinkUnmasked() {
    // Disable and reset the unmasked link if the user's join status is not approved
    disableAndResetUnmaskedLinkIfNotApproved();
    return streamLinkUnmasked;
  }

  @JsonIgnore
  private String streamLinkUnmasked;

  /**
   * For the purpose of use in Chat Space where an event is created and is available for member of the space to join.
   */
  @JsonIgnore
  public String streamLinkNotMasked;

  @JsonIgnore
  private String externalId;

  @JsonIgnore
  public StreamVisibility getVisibility() {
    return nonNull(streamVisibilityInfo) ? streamVisibilityInfo.getVisibility() : null;
  }

  @JsonIgnore
  public LocalDateTime getEndDate() {
    return nonNull(schedule.getEndDate()) ? schedule.getEndDate() : null;
  }

  @JsonIgnore
  public JoinStatus getJoinStatus() {
    return nonNull(attendanceInfo) ? attendanceInfo.getJoinStatusInfo().getJoinStatus() : null;
  }

  @JsonIgnore
  public StreamType getStreamType() {
    return nonNull(streamTypeInfo) ? streamTypeInfo.getStreamType() : null;
  }

  @JsonIgnore
  public boolean hasHappened() {
    return schedule.getEndDate().isBefore(LocalDateTime.now());
  }

  /**
   * Disables and resets the unmasked stream link if the join status is not approved.
   *
   * <p>This method checks the current join status, and if it is not approved,
   * it resets the unmasked stream link to {@code null} to ensure the user
   * does not have access to the unmasked link.</p>
   *
   * <p>This operation ensures that users without approval cannot access unmasked stream links.</p>
   */
  public void disableAndResetUnmaskedLinkIfNotApproved() {
    if (nonNull(getJoinStatus()) && JoinStatus.isNotApproved(getJoinStatus())) {
      streamLinkUnmasked = null;
    }
  }

}
