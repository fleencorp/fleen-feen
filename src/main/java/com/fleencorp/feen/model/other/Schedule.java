package com.fleencorp.feen.model.other;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.feen.util.DateTimeUtil;
import com.fleencorp.feen.util.external.google.GoogleApiUtil;
import lombok.*;

import java.time.LocalDateTime;

import static com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING;
import static com.fleencorp.base.util.datetime.DateFormatUtil.DATE_TIME;
import static com.fleencorp.feen.util.DateTimeUtil.getTimezoneAbbreviation;
import static java.util.Objects.nonNull;

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
  "gmt_offset",
  "formatted_date",
  "formatted_timezone"
})
public class Schedule {

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
   * Retrieves the formatted date range for the schedule.
   *
   * <p>This method returns the start and end dates in a human-readable format. It uses
   * {@link GoogleApiUtil#formatDateRange(Schedule)} to format the dates, typically producing a string
   * like "September 20th, 2024 11:00am - 22:00pm", depending on the actual start and end
   * date values.</p>
   *
   * @return A formatted string representing the date range for the schedule, or {@code null}
   *         if the start date or end date is not set.
   */
  @JsonProperty("formatted_date")
  public String getDate() {
    return GoogleApiUtil.formatDateRange(this);
  }

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
   * Formats the full timezone string in the format "Africa/Lagos, WAT (+1:00)".
   *
   * @return A formatted string containing the timezone name, abbreviation, and GMT offset.
   */
  @JsonProperty("formatted_timezone")
  public String getFormattedTimezone() {
    if (nonNull(startDate) && nonNull(timezone)) {
      final String abbreviation = getAbbreviatedTimezone();
      final String gmtOffset = getGmtOffset();
      return String.format("%s, %s %s", timezone, abbreviation, gmtOffset);
    }
    return null;
  }

  /**
   * Creates and returns a new Schedule instance with the provided start date, end date, and timezone.
   *
   * @param scheduledStartDate The scheduled start date of the event.
   * @param scheduledEndDate   The scheduled end date of the event.
   * @param timezone           The timezone in which the event is scheduled.
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
