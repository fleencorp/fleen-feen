package com.fleencorp.feen.constant.stream;

import com.fleencorp.base.constant.base.ApiParameter;
import lombok.Getter;

import static com.fleencorp.base.util.EnumUtil.parseEnumOrNull;

/**
* Enum representing different types of streams source.
*
* @author Yusuf Alamu Musa
* @version 1.0
*/
@Getter
public enum StreamSource implements ApiParameter {

  GOOGLE_MEET("Google Meet", "stream.source.google.meet"),
  GOOGLE_MEET_LIVESTREAM("Google Meet Live Stream", "stream.source.google.meet.live.stream"),
  NONE("None", "stream.source.none"),
  YOUTUBE_LIVE("YouTube Live", "stream.source.youtube.live"),;

  private final String value;
  private final String messageCode;

  StreamSource(
      final String value,
      final String messageCode) {
    this.value = value;
    this.messageCode = messageCode;
  }

  public static StreamSource of(final String value) {
    return parseEnumOrNull(value, StreamSource.class);
  }

  /**
   * Retrieves the stream source for YouTube Live.
   *
   * @return the YouTube Live stream source
   */
  public static String youtubeLive() {
    return YOUTUBE_LIVE.getValue();
  }

  /**
   * Retrieves the stream source for Google Meet.
   *
   * @return the Google Meet stream source
   */
  public static String googleMeet() {
    return GOOGLE_MEET.getValue();
  }

  /**
   * Checks if the given stream source is for YouTube Live.
   *
   * @param streamSource The stream source to check.
   * @return {@code true} if the stream source is YouTube Live; {@code false} otherwise.
   */
  public static boolean isYouTubeLive(final StreamSource streamSource) {
    return YOUTUBE_LIVE == streamSource;
  }

  /**
   * Checks if the given stream source is for Google Meet.
   *
   * @param streamSource The stream source to check.
   * @return {@code true} if the stream source is Google Meet; {@code false} otherwise.
   */
  public static boolean isGoogleMeet(final StreamSource streamSource) {
    return GOOGLE_MEET == streamSource;
  }
}
