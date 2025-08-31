package com.fleencorp.feen.stream.util;

import com.fleencorp.feen.stream.model.other.Schedule;
import com.fleencorp.feen.stream.model.response.StreamResponse;
import com.fleencorp.feen.user.model.domain.Member;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import static com.fleencorp.feen.common.util.common.CommonUtil.allNonNull;
import static com.fleencorp.feen.common.util.common.DateTimeUtil.convertToTimezone;
import static java.util.Objects.nonNull;

public final class StreamServiceUtil {

  private StreamServiceUtil() {}

  /**
   * Sets the stream's schedule in the user's timezone if it differs from the stream's original timezone.
   *
   * <p>If the user's timezone is different from the stream's original timezone, this method converts
   * the stream's schedule accordingly and sets it as the "other schedule" in the response. Otherwise,
   * an empty schedule is set.</p>
   *
   * @param streamResponse the stream response containing the original schedule
   * @param member the user whose timezone is used for conversion
   */
  public static void setOtherScheduleBasedOnUserTimezone(final StreamResponse streamResponse, final Member member) {
    if (allNonNull(streamResponse, member)) {
      // Get the stream's original timezone
      final String streamTimezone = streamResponse.getSchedule().getTimezone();
      // Get the user's timezone
      final String userTimezone = member.getTimezone();
      // Check if the stream's timezone and user's timezone are different
      if (!streamTimezone.equalsIgnoreCase(userTimezone)) {
        // Convert the stream's schedule to the user's timezone
        final Schedule otherSchedule = createSchedule(streamResponse, userTimezone);
        // Set the converted dates and user's timezone in the stream's other schedule
        streamResponse.setOtherSchedule(otherSchedule);
      } else {
        // If the timezones are the same, set an empty schedule
        streamResponse.setOtherSchedule(Schedule.of());
      }
    }
  }

  /**
   * Creates a schedule for a stream adjusted to the user's timezone.
   *
   * <p>This method checks if the stream and user timezone are valid. If so, it retrieves the
   * stream's original start and end dates, converts them to the user's timezone, and returns
   * the converted schedule with the adjusted dates. If either the stream or user timezone is
   * invalid (null), an empty schedule is returned.</p>
   *
   * @param stream the FleenStreamResponse object representing the stream.
   * @param userTimezone the timezone of the user to which the schedule will be adjusted.
   * @return a Schedule object with the adjusted start and end dates in the user's timezone.
   */
  public static Schedule createSchedule(final StreamResponse stream, final String userTimezone) {
    if (nonNull(stream) && nonNull(userTimezone)) {
      // Get the stream's original timezone
      final String streamTimezone = stream.getSchedule().getTimezone();

      // Retrieve the start dates from the stream's schedule
      final LocalDateTime startDate = stream.getSchedule().getStartDate();
      // Retrieve the end dates from the stream's schedule
      final LocalDateTime endDate = stream.getSchedule().getEndDate();

      // Convert the stream's start date to the user's timezone
      final LocalDateTime userStartDate = convertToTimezone(startDate, streamTimezone, userTimezone);
      // Convert the stream's end date to the user's timezone
      final LocalDateTime userEndDate = convertToTimezone(endDate, streamTimezone, userTimezone);
      // Return the schedule with the dates in the user's timezone
      return Schedule.of(userStartDate, userEndDate, userTimezone);
    }
    // If the stream or userTimezone is null, return an empty schedule
    return Schedule.of();
  }

  /**
   * Extracts the stream number IDs from a collection of {@link StreamResponse} objects.
   *
   * <p>Null entries in the collection are ignored. Each non-null {@code StreamResponse}
   * is mapped to its {@code numberId}.</p>
   *
   * @param streamResponses the collection of stream responses
   * @return a list of stream number IDs
   */
  public static List<Long> getStreamsIds(final Collection<StreamResponse> streamResponses) {
    return streamResponses.stream()
      .filter(Objects::nonNull)
      .map(StreamResponse::getNumberId)
      .toList();
  }

}
