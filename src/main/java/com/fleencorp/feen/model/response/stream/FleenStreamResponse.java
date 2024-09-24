package com.fleencorp.feen.model.response.stream;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.feen.constant.security.mask.MaskedEmailAddress;
import com.fleencorp.feen.constant.security.mask.MaskedPhoneNumber;
import com.fleencorp.feen.constant.stream.*;
import com.fleencorp.feen.model.response.base.FleenFeenResponse;
import com.fleencorp.feen.util.DateTimeUtil;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.List;

import static com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING;
import static com.fleencorp.base.util.datetime.DateFormatUtil.DATE_TIME;
import static com.fleencorp.feen.util.DateTimeUtil.getTimezoneAbbreviation;
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
  "stream_source",
  "visibility",
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

  @JsonProperty("stream_link")
  private String streamLink;

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
    if (currentTime.isBefore(this.schedule.startDate)) {
      this.scheduleTimeType = StreamTimeType.UPCOMING;
    } else if (currentTime.isAfter(this.schedule.endDate)) {
      this.scheduleTimeType = StreamTimeType.PAST;
    } else {
      this.scheduleTimeType = StreamTimeType.LIVE;
    }
  }

  @Builder
  @Getter
  @Setter
  @NoArgsConstructor
  @AllArgsConstructor
  @JsonInclude(JsonInclude.Include.NON_NULL)
  @JsonPropertyOrder({
    "name",
    "email",
    "phone"
  })
  public static class Organizer {

    @JsonProperty("name")
    private String organizerName;

    @JsonProperty("email")
    private MaskedEmailAddress organizerEmail;

    @JsonProperty("phone")
    private MaskedPhoneNumber organizerPhone;

    public static Organizer of(final String organizerName, final String organizerEmail, final String organizerPhone) {
      return Organizer.builder()
        .organizerName(organizerName)
        .organizerEmail(MaskedEmailAddress.of(organizerEmail))
        .organizerPhone(MaskedPhoneNumber.of(organizerPhone))
        .build();
    }
  }

  @Builder
  @Getter
  @Setter
  @NoArgsConstructor
  @AllArgsConstructor
  @JsonInclude(JsonInclude.Include.NON_NULL)
  @JsonPropertyOrder({
    "start_date",
    "end_date",
    "timezone",
    "abbreviated_timezone",
    "gmt_offset"
  })
  public static class Schedule {

    @JsonFormat(shape = STRING, pattern = DATE_TIME)
    @JsonProperty("start_date")
    private LocalDateTime startDate;

    @JsonFormat(shape = STRING, pattern = DATE_TIME)
    @JsonProperty("end_date")
    private LocalDateTime endDate;

    @JsonProperty("timezone")
    private String timezone;

    @JsonProperty("is_schedule_set")
    private boolean isScheduleSet;

    /**
     * Retrieves the abbreviated timezone for the stream's start date.
     *
     * @return The abbreviated timezone (e.g., "PST") if both startDate and timezone are not null; {@code null} otherwise.
     */
    @JsonProperty("abbreviated_timezone")
    public String getAbbreviatedTimezone() {
      return nonNull(startDate) && nonNull(timezone)
        ? getTimezoneAbbreviation(startDate, timezone)
        : null;
    }

    /**
     * Retrieves the GMT offset for the stream's start date.
     *
     * @return The GMT offset in the format "(UTC+/-XX:XX)" if both startDate and timezone are not null; {@code null} otherwise.
     */
    @JsonProperty("gmt_offset")
    public String getGmtOffset() {
      return nonNull(startDate) && nonNull(timezone)
        ? DateTimeUtil.getGmtOffset(startDate, timezone)
        : null;
    }

    /**
     * Creates and returns a new Schedule instance with the provided start date, end date, and timezone.
     *
     * @param scheduledStartDate The scheduled start date of the event.
     * @param scheduledEndDate The scheduled end date of the event.
     * @param timezone The timezone in which the event is scheduled.
     * @return A Schedule object with the provided details and a flag indicating the schedule is set.
     */
    public static Schedule of(final LocalDateTime scheduledStartDate, final LocalDateTime scheduledEndDate, final String timezone) {
      return Schedule.builder()
        .startDate(scheduledStartDate)
        .endDate(scheduledEndDate)
        .timezone(timezone)
        .isScheduleSet(true)
        .build();
    }

    /**
     * Creates and returns a new Schedule instance with default settings.
     *
     * @return A Schedule object with the schedule set flag as false, indicating no schedule details are provided.
     */
    public static Schedule of() {
      return Schedule.builder()
        .isScheduleSet(false)
        .build();
    }
  }
}
