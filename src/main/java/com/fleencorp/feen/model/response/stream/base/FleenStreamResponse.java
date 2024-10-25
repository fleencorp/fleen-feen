package com.fleencorp.feen.model.response.stream.base;

import com.fasterxml.jackson.annotation.*;
import com.fleencorp.feen.constant.security.mask.MaskedStreamLinkUri;
import com.fleencorp.feen.constant.stream.*;
import com.fleencorp.feen.model.other.Organizer;
import com.fleencorp.feen.model.other.Schedule;
import com.fleencorp.feen.model.response.base.FleenFeenResponse;
import com.fleencorp.feen.model.response.stream.StreamAttendeeResponse;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.List;

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
  "location",
  "schedule",
  "other_schedule",
  "created_on",
  "updated_on",
  "organizer",
  "for_kids",
  "stream_link",
  "stream_type",
  "stream_source",
  "visibility",
  "status",
  "request_to_join_status",
  "join_status",
  "schedule_time_type",
  "total_attending",
  "some_attendees",
  "is_private",
  "schedule",
  "other_schedule"
})
public class FleenStreamResponse extends FleenFeenResponse {

  @JsonProperty("title")
  private String title;

  @JsonProperty("description")
  private String description;

  @JsonProperty("location")
  private String location;

  @JsonProperty("organizer")
  private Organizer organizer;

  @JsonProperty("for_kids")
  private Boolean forKids;

  @JsonFormat(shape = STRING)
  @JsonProperty("stream_link")
  private MaskedStreamLinkUri streamLink;

  @JsonIgnore
  private String streamLinkUnmasked;

  @JsonFormat(shape = STRING)
  @JsonProperty("stream_type")
  private StreamType streamType;

  @JsonFormat(shape = STRING)
  @JsonProperty("stream_source")
  private StreamSource streamSource;

  @JsonFormat(shape = STRING)
  @JsonProperty("visibility")
  private StreamVisibility visibility;

  @JsonProperty("schedule")
  private Schedule schedule;

  @JsonProperty("other_schedule")
  private Schedule otherSchedule;

  @JsonFormat(shape = STRING)
  @JsonProperty("request_to_join_status")
  private StreamAttendeeRequestToJoinStatus requestToJoinStatus;

  @JsonFormat(shape = STRING)
  @JsonProperty("status")
  private StreamStatus status;

  @JsonProperty("total_attending")
  private long totalAttending;

  @JsonProperty("some_attendees")
  private List<StreamAttendeeResponse> someAttendees;

  @Builder.Default
  @JsonProperty("join_status")
  private String joinStatus = JoinStatus.NOT_JOINED.getValue();

  @JsonProperty("schedule_time_type")
  private StreamTimeType scheduleTimeType;

  @JsonProperty("is_private")
  public boolean isPrivate() {
    return StreamVisibility.isPrivateOrProtected(visibility);
  }

  @JsonProperty("stream_link_unmasked")
  public String getStreamLinkUnmasked() {
    disableAndResetUnmaskedLinkIfNotApproved();
    return streamLinkUnmasked;
  }

  /**
   * For the purpose of use in Chat Space where an event is created and is available for member of the space to join.
   */
  @JsonIgnore
  public String streamLinkNotMasked;

  /**
   * Updates the schedule status of this FleenStream by setting the stream time type
   * based on the current time.
   */
  public void updateStreamSchedule() {
    updateStreamTimeType(LocalDateTime.now());
  }

  /**
   * Updates the streamTimeType of this FleenStream based on the provided currentTime.
   * The stream is considered Upcoming if currentTime is before the scheduled start time,
   * Live if it is between the scheduled start and end times, and Past if it is after the
   * scheduled end time.
   *
   * @param currentTime the current time to compare against the stream's scheduled start and end times
   * @throws NullPointerException if currentTime is null
   */
  public void updateStreamTimeType(final LocalDateTime currentTime) {
    if (currentTime.isBefore(this.schedule.getStartDate())) {
      this.scheduleTimeType = StreamTimeType.UPCOMING;
    } else if (currentTime.isAfter(this.schedule.getStartDate())) {
      this.scheduleTimeType = StreamTimeType.PAST;
    } else {
      this.scheduleTimeType = StreamTimeType.LIVE;
    }
  }

  /**
   * Increments the total number of attendees or guests by one. This method is designed to ensure
   * that the count is increased just once per invocation.
   */
  public void incrementAttendeesOrGuestsAttendingJustOnce() {
    totalAttending++;
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
    if (nonNull(joinStatus) && JoinStatus.isNotApproved(joinStatus)) {
      streamLinkUnmasked = null;
    }
  }

}
