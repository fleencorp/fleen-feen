package com.fleencorp.feen.model.response.stream;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.feen.constant.security.mask.MaskedEmailAddress;
import com.fleencorp.feen.constant.security.mask.MaskedPhoneNumber;
import com.fleencorp.feen.constant.stream.*;
import com.fleencorp.feen.model.response.base.FleenFeenResponse;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.List;

import static com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING;
import static com.fleencorp.base.util.datetime.DateFormatUtil.DATE_TIME;

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
  "timezone",
  "created_on",
  "updated_on",
  "organizer",
  "for_kids",
  "stream_link",
  "stream_source",
  "visibility",
  "join_status",
  "scheduled_start_date",
  "scheduled_end_date",
  "schedule_time_type",
  "total_attending",
  "some_attendees",
  "is_private"
})
public class FleenStreamResponse extends FleenFeenResponse {

  @JsonProperty("title")
  private String title;

  @JsonProperty("description")
  private String description;

  @JsonProperty("location")
  private String location;

  @JsonProperty("timezone")
  private String timezone;

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

  @JsonFormat(shape = STRING, pattern = DATE_TIME)
  @JsonProperty("scheduled_start_date")
  private LocalDateTime scheduledStartDate;

  @JsonFormat(shape = STRING, pattern = DATE_TIME)
  @JsonProperty("scheduled_end_date")
  private LocalDateTime scheduledEndDate;

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
    if (currentTime.isBefore(this.scheduledStartDate)) {
      this.scheduleTimeType = StreamTimeType.UPCOMING;
    } else if (currentTime.isAfter(this.scheduledEndDate)) {
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
}
