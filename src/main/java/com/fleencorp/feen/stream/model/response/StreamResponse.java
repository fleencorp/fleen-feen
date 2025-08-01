package com.fleencorp.feen.stream.model.response;

import com.fasterxml.jackson.annotation.*;
import com.fleencorp.feen.common.constant.common.JoinStatus;
import com.fleencorp.feen.common.constant.mask.MaskedStreamLinkUri;
import com.fleencorp.feen.stream.constant.core.StreamType;
import com.fleencorp.feen.stream.constant.core.StreamVisibility;
import com.fleencorp.feen.like.model.info.UserLikeInfo;
import com.fleencorp.feen.link.model.response.base.LinkMusicResponse;
import com.fleencorp.feen.model.contract.HasId;
import com.fleencorp.feen.model.contract.HasOrganizer;
import com.fleencorp.feen.model.contract.Likeable;
import com.fleencorp.feen.model.contract.Updatable;
import com.fleencorp.feen.common.model.info.IsDeletedInfo;
import com.fleencorp.feen.common.model.info.IsForKidsInfo;
import com.fleencorp.feen.like.model.info.LikeCountInfo;
import com.fleencorp.feen.review.model.info.ReviewCountInfo;
import com.fleencorp.feen.stream.model.info.schedule.ScheduleTimeTypeInfo;
import com.fleencorp.feen.stream.model.other.Organizer;
import com.fleencorp.feen.stream.model.other.Schedule;
import com.fleencorp.feen.common.model.response.core.FleenFeenResponse;
import com.fleencorp.feen.stream.model.response.attendee.StreamAttendeeResponse;
import com.fleencorp.feen.review.model.response.base.ReviewResponse;
import com.fleencorp.feen.stream.model.info.attendance.AttendanceInfo;
import com.fleencorp.feen.stream.model.info.attendance.AttendeeCountInfo;
import com.fleencorp.feen.stream.model.info.core.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collection;
import java.util.HashSet;

import static com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING;
import static java.util.Objects.nonNull;

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
  "music_link",
  "stream_type_info",
  "stream_source_info",
  "stream_status_info",
  "visibility_info",
  "is_deleted_info",
  "other_detail_info",
  "user_like_info",
  "like_count_info",
  "music_link",
  "schedule_time_type_info",
  "attendee_count_info",
  "review_count_info",
  "some_attendees",
  "reviews",
  "is_updatable",
  "is_private",
  "attendance_info"
})
public class StreamResponse extends FleenFeenResponse
    implements HasId, HasOrganizer, Updatable, Likeable {

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
  private IsForKidsInfo forKidsInfo;

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

  @JsonProperty("attendee_count_info")
  private AttendeeCountInfo attendeeCountInfo;

  @JsonProperty("review_count_info")
  private ReviewCountInfo reviewCountInfo;

  @JsonProperty("some_attendees")
  private Collection<StreamAttendeeResponse> someAttendees = new HashSet<>();

  @JsonProperty("reviews")
  private Collection<ReviewResponse> reviews = new HashSet<>();

  @JsonProperty("attendance_info")
  private AttendanceInfo attendanceInfo;

  @JsonProperty("is_deleted_info")
  private IsDeletedInfo deletedInfo;

  @JsonProperty("other_detail_info")
  private OtherStreamDetailInfo otherDetailInfo;

  @JsonProperty("user_like_info")
  private UserLikeInfo userLikeInfo;

  @JsonProperty("like_count_info")
  private LikeCountInfo likeCountInfo;

  @JsonProperty("music_link")
  private LinkMusicResponse musicLink;

  @JsonProperty("is_updatable")
  private Boolean isUpdatable;

  @JsonIgnore
  private String streamLinkUnmasked;

  @JsonIgnore
  private Long organizerId;

  @JsonIgnore
  private String externalId;

  /**
   * For the purpose of use in Chat Space where an event is created and is available for member of the space to join.
   */
  @JsonIgnore
  public String streamLinkNotMasked;

  @JsonProperty("is_private")
  public boolean isPrivate() {
    return StreamVisibility.isPrivateOrProtected(getVisibility());
  }

  @JsonProperty("ended")
  public boolean hasEnded() {
    return LocalDateTime.now(ZoneId.of(schedule.getTimezone())).isAfter(schedule.getEndDate());
  }

  @Override
  @JsonIgnore
  public Long getAuthorId() {
    return getOrganizerId();
  }

  @JsonProperty("stream_link_unmasked")
  public String getStreamLinkUnmasked() {
    // Disable and reset the unmasked link if the user's join status is not approved
    disableAndResetUnmaskedLinkIfNotApproved();
    return streamLinkUnmasked;
  }

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

  public void setReviews(final ReviewResponse review) {
    if (nonNull(review)) {
      reviews.add(review);
    }
  }

  @Override
  public void setIsOrganizer(final boolean isOrganizer) {
    this.organizer.setIsOrganizer(isOrganizer);
  }

  @Override
  public void setIsUpdatable(final boolean isUpdatable) {
    this.isUpdatable = isUpdatable;
  }

  @Override
  public void markAsUpdatable() {
    setIsUpdatable(true);
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
