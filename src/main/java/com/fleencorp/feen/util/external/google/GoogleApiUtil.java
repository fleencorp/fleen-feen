package com.fleencorp.feen.util.external.google;

import com.google.api.client.util.DateTime;

import java.time.LocalDateTime;
import java.util.Date;

import static com.fleencorp.feen.constant.external.google.youtube.base.YouTubeParameter.LIVE_STREAMING_BASE_LINK;
import static com.fleencorp.feen.util.DateTimeUtil.toDate;
import static java.util.Objects.nonNull;


public enum GoogleApiUtil {
    ;

    /**
   * Converts a LocalDateTime object to a DateTime object.
   *
   * <p>This method converts a Java LocalDateTime object to a DateTime object
   * suitable for use with the Google Calendar API.</p>
   *
   * <p>If the conversion succeeds, a new DateTime object is returned; otherwise,
   * null is returned.</p>
   *
   * @param dateTime the LocalDateTime object to convert
   * @return a DateTime object representing the same date and time, or null if the conversion fails
   */
  public static DateTime toDateTime(final LocalDateTime dateTime) {
    final Date date = toDate(dateTime);
    if (nonNull(date)) {
      return new DateTime(date);
    }
    return null;
  }

  public static String getYouTubeLiveStreamingByBroadcastId(final String broadcastId) {
    return LIVE_STREAMING_BASE_LINK + broadcastId;
  }
}
