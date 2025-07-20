package com.fleencorp.feen.common.util.external.google;

import com.fleencorp.feen.common.constant.external.google.chat.base.GoogleChatParameter;
import com.fleencorp.feen.stream.model.other.Schedule;
import com.fleencorp.feen.oauth2.service.external.impl.external.GoogleOauth2ServiceImpl;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.util.DateTime;
import com.google.protobuf.Timestamp;
import org.apache.commons.lang3.StringUtils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;

import static com.fleencorp.feen.common.constant.base.SimpleConstant.FORWARD_SLASH;
import static com.fleencorp.feen.common.constant.external.google.youtube.base.YouTubeParameter.LIVE_STREAMING_BASE_LINK;
import static com.fleencorp.feen.common.util.DateTimeUtil.toDate;
import static com.google.auth.http.AuthHttpConstants.AUTHORIZATION;
import static com.google.auth.http.AuthHttpConstants.BEARER;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

/**
 * Utility class for handling operations related to the Google API.
 *
 * <p>This class provides static methods to assist with various tasks related to
 * interacting with the Google API, such as constructing URLs, parsing responses,
 * and managing request parameters. It is designed to be a helper for simplifying
 * common operations needed when working with Google services.</p>
 *
 * @author Yusuf Alamu Musa
 * @version 1.0
 */
public final class GoogleApiUtil {

  private GoogleApiUtil() {}

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

  /**
   * Generates a YouTube live streaming link using the given broadcast ID.
   *
   * <p>This method concatenates the base YouTube live streaming URL with the provided
   * broadcast ID to form the complete link for accessing the live stream.
   *
   * @param broadcastId the unique identifier of the YouTube broadcast
   * @return the full URL to the YouTube live stream associated with the broadcast ID
   */
  public static String getYouTubeLiveStreamingLinkByBroadcastId(final String broadcastId) {
    return LIVE_STREAMING_BASE_LINK.getValue() + broadcastId;
  }

  /**
   * Creates and returns an {@link HttpRequestInitializer} with the provided access token.
   *
   * <p>This method constructs an {@link HttpRequestInitializer} that configures an
   * HTTP request with a JSON parser, sets the headers using the provided access token,
   * and ensures exceptions are thrown on execution errors.</p>
   *
   * @param accessToken The access token to be included in the HTTP request headers.
   * @return An {@link HttpRequestInitializer} configured with the provided access token.
   *
   * @see <a href="https://velog.io/@ssongji/%EC%9C%A0%ED%8A%9C%EB%B8%8C-%EB%8D%B0%EC%9D%B4%ED%84%B0-%ED%81%AC%EB%A1%A4%EB%A7%81-%EB%B0%8F-%EC%8B%9C%EA%B0%81%ED%99%94-%ED%94%84%EB%A1%9C%EC%A0%9D%ED%8A%B8-1.-YOUTUBE-API-%EC%82%AC%EC%9A%A9-%ED%99%98%EA%B2%BD-%EC%84%A4%EC%A0%95">
   *  YouTube API usage environment settings</a>
   */
  public static HttpRequestInitializer getHttpRequestInitializer(final String accessToken) {
    return httpRequest -> {
      httpRequest.setParser(new JsonObjectParser(GoogleOauth2ServiceImpl.getJsonFactory()));
      httpRequest.setHeaders(getHeaders(accessToken));
      httpRequest.setThrowExceptionOnExecuteError(true);
      httpRequest.setConnectTimeout(50_000);
      httpRequest.setReadTimeout(50_000);

      httpRequest.executeAsync();
    };
  }

  /**
   * Creates and returns HTTP headers with the provided access token.
   *
   * <p>This method constructs an instance of {@link HttpHeaders} and sets the
   * Authorization header with the bearer token generated from the provided access token.
   * The bearer token is created using the {@link #getBearerToken(String)} method.</p>
   *
   * <p>The resulting HttpHeaders object can be used to authenticate HTTP requests
   * that require OAuth 2.0 bearer token authentication.</p>
   *
   * @param accessToken The access token to be included in the Authorization header.
   * @return An {@link HttpHeaders} object with the Authorization header set.
   *
   * @see <a href="https://developers.google.com/youtube/v3/docs/">
   *   YouTube API Reference - Calling the API</a>
   * @see <a href="https://stackoverflow.com/a/31169962/10152132">
   *   Set Bearer Token for Google YouTube API Request</a>
   */
  public static HttpHeaders getHeaders(final String accessToken) {
    final HttpHeaders headers = new HttpHeaders();
    headers.set(AUTHORIZATION, getBearerToken(accessToken));
    return headers;
  }

  /**
   * Constructs a bearer token from the provided access token.
   *
   * <p>This method takes an access token as input and constructs a bearer token
   * by concatenating the "Bearer" prefix with the access token. The resulting bearer
   * token is used for authorizing API requests that require OAuth 2.0 authentication.</p>
   *
   * <p>The "Bearer" prefix is a standard convention for OAuth 2.0 token usage, ensuring
   * that the access token is correctly formatted for authorization headers in HTTP requests.</p>
   *
   * @param accessToken The access token to be converted into a bearer token.
   * @return A bearer token constructed from the provided access token.
   *
   * @see <a href="https://kingbbode.tistory.com/8">
   *   Integrating Google API in Spring Boot (1) Setting up Oauth authentication</a>
   */
  public static String getBearerToken(final String accessToken) {
    return BEARER.concat(" ").concat(accessToken);
  }

  /**
   * Converts a {@code Timestamp} object to a {@code LocalDateTime}.
   *
   * <p>If the provided {@code Timestamp} is non-null, the method converts it to a
   * {@code LocalDateTime} object using the seconds and nanoseconds of the timestamp
   * in the UTC timezone. If the {@code Timestamp} is null, the method returns the
   * current date and time using {@code LocalDateTime.now()}.</p>
   *
   * @param timestamp the {@code Timestamp} object to convert
   * @return a {@code LocalDateTime} object representing the converted timestamp,
   * or the current date and time if the input is null
   **/
  public static LocalDateTime convertToLocalDateTime(final Timestamp timestamp) {
    if (nonNull(timestamp)) {
      return LocalDateTime.ofEpochSecond(timestamp.getSeconds(), timestamp.getNanos(), ZoneOffset.UTC);
    }
    return LocalDateTime.now();
  }

  /**
   * Converts a {@code LocalDateTime} object to a Protobuf {@code Timestamp}.
   *
   * <p>If the provided {@code LocalDateTime} is null, the method defaults to the current date and time.
   * The {@code LocalDateTime} is first converted to an {@code Instant} using UTC as the timezone,
   * then the seconds and nanoseconds of the instant are used to build the Protobuf {@code Timestamp}.</p>
   *
   * @param localDateTime the {@code LocalDateTime} to convert; if null, the current date and time are used
   * @return a Protobuf {@code Timestamp} built from the provided or default {@code LocalDateTime}
   **/
  public static Timestamp convertToProtobufTimestamp(final LocalDateTime localDateTime) {
    LocalDateTime newDateTime = localDateTime;
    if (isNull(localDateTime)) {
      newDateTime = LocalDateTime.now();
    }
    // Convert LocalDateTime to Instant
    final Instant instant = newDateTime.toInstant(ZoneOffset.UTC);

    // Build the Timestamp using the seconds and nanos from the Instant
    return Timestamp.newBuilder()
      .setSeconds(instant.getEpochSecond())  // Set seconds since epoch
      .setNanos(instant.getNano())           // Set nanoseconds
      .build();
  }

  /**
   * Creates a fully qualified URI for a chat space based on its name.
   *
   * <p>This method extracts the space ID or name from the provided {@code spaceName}
   * and appends it to the base URI for the chat space, forming a complete URI.</p>
   *
   * @param spaceName the name of the chat space from which the space ID or name will be derived
   * @return a fully qualified chat space URI based on the space name
   **/
  public static String createSpaceUriFromSpaceName(final String spaceName) {
    final String spaceIdOrName = getSpaceIdOrNameFrom(spaceName);
    return GoogleChatParameter.chatSpaceBaseUri().concat(spaceIdOrName);
  }

  /**
   * Extracts the space ID or name from the provided space name.
   *
   * <p>This method checks if the provided space name is non-null, non-empty, and starts
   * with the value returned by {@link GoogleChatParameter#spaces()}. If these conditions
   * are met, it extracts the space ID or name by removing the prefix.</p>
   *
   * <p>If the conditions are not met, the original space name is returned.</p>
   *
   * @param spaceName the full name of the space, including any prefix
   * @return the extracted space ID or name, or the original space name if conditions are not met
   */
  public static String getSpaceIdOrNameFrom(final String spaceName) {
    if (nonNull(spaceName) && !StringUtils.isBlank(spaceName) && spaceName.startsWith(GoogleChatParameter.spaces())) {
      final int index = GoogleChatParameter.spaces().length() + 1;
      return spaceName.substring(index);
    }
    return spaceName;
  }

  /**
   * Extracts the member ID or name from the provided member name.
   *
   * <p>This method checks if the provided member name is non-null, non-empty, and starts
   * with the value returned by {@link GoogleChatParameter#users()}. If these conditions
   * are met, it extracts the member ID or name by removing the prefix.</p>
   *
   * <p>If the conditions are not met, the original member name is returned.</p>
   *
   * @param memberName the full name of the member, including any prefix
   * @return the extracted member ID or name, or the original member name if conditions are not met
   */
  public static String getSpaceMemberIdOrNameFrom(final String memberName) {
    if (nonNull(memberName) && !StringUtils.isBlank(memberName) && memberName.startsWith(GoogleChatParameter.users())) {
      final int index = GoogleChatParameter.users().length() + 1;
      return memberName.substring(index);
    }
    return memberName;
  }

  /**
   * Constructs a required pattern for a chat space ID or name.
   *
   * <p>This method concatenates the value returned by {@link GoogleChatParameter#spaces()}
   * with a forward slash and the provided space ID or name. The resulting string can be
   * used to create a standardized reference for the chat space.</p>
   *
   * @param spaceIdOrName the chat space ID or name to be included in the pattern
   * @return the constructed chat space ID or name pattern
   */
  public static String getChatSpaceIdOrNameRequiredPattern(final String spaceIdOrName) {
    if (nonNull(spaceIdOrName) && spaceIdOrName.startsWith(GoogleChatParameter.spaces())) {
      return spaceIdOrName;
    }

    return GoogleChatParameter.spaces() + FORWARD_SLASH + spaceIdOrName;
  }

  /**
   * Constructs the required pattern for a chat space user ID or name
   * based on the provided email address or user ID.
   *
   * <p>This method concatenates the user parameter prefix with a forward slash and
   * the specified email address or user ID to create a standardized
   * identifier for chat space users.</p>
   *
   * @param emailAddressOrUserId The email address or user ID to be included in the pattern.
   * @return A string representing the required pattern for the chat space user ID or name.
   */
  public static String getChatSpaceUserIdOrNameRequiredPattern(final String emailAddressOrUserId) {
    if (nonNull(emailAddressOrUserId) && emailAddressOrUserId.startsWith(GoogleChatParameter.users())) {
      return emailAddressOrUserId;
    }
    return GoogleChatParameter.users() + FORWARD_SLASH + emailAddressOrUserId;
  }

  /**
   * Constructs a required pattern for a chat space ID or name along with a member ID or name.
   *
   * <p>This method concatenates the value returned by {@link GoogleChatParameter#spaces()}
   * with a forward slash, the provided space ID or name, another forward slash,
   * the value from {@link GoogleChatParameter#members()}, and finally another forward slash
   * followed by the member ID or name. The resulting string forms a standardized reference
   * for a specific member within a chat space.</p>
   *
   * @param spaceIdOrName the chat space ID or name to be included in the pattern
   * @param memberIdOrName the member ID or name to be included in the pattern
   * @return the constructed chat space and member ID or name pattern
   */
  public static String getChatSpaceIdOrNameAndMemberRequiredPattern(final String spaceIdOrName, final String memberIdOrName) {
    return GoogleChatParameter.spaces() + FORWARD_SLASH + spaceIdOrName + FORWARD_SLASH + GoogleChatParameter.members() + FORWARD_SLASH + memberIdOrName;
  }

  /**
   * Formats a date range from a given schedule into a human-readable string.
   *
   * <p>This method takes a {@link Schedule} object and formats the start and end dates
   * into a string representation. The start date is displayed with a day suffix (e.g., "September 20th, 2024"),
   * while both the start and end times are shown in 12-hour format with lowercase am/pm
   * (e.g., "11:00am"). If either the start date or end date is null, the method returns null.</p>
   *
   * @param schedule the {@link Schedule} object containing the start and end dates
   * @return a formatted string representing the date range, or null if either date is missing
   */
  public static String formatDateRange(final Schedule schedule) {
    if (schedule.getStartDate() == null || schedule.getEndDate() == null) {
      return null;
    }

    // Formatter for month (e.g., "September")
    final DateTimeFormatter monthFormatter = DateTimeFormatter.ofPattern("MMMM", Locale.ENGLISH);
    // Formatter for day (e.g., "20")
    final DateTimeFormatter dayFormatter = DateTimeFormatter.ofPattern("d", Locale.ENGLISH);
    // Formatter for year (e.g., "2024")
    final DateTimeFormatter yearFormatter = DateTimeFormatter.ofPattern("yyyy", Locale.ENGLISH);
    // Formatter for time in 12-hour format with am/pm (e.g., "11:00am")
    final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("h:mma", Locale.ENGLISH);

    // Format start date components
    final String startMonth = schedule.getStartDate().format(monthFormatter);
    final String startDay = schedule.getStartDate().format(dayFormatter) + getDayOfMonthSuffix(schedule.getStartDate().getDayOfMonth());
    final String startYear = schedule.getStartDate().format(yearFormatter);
    final String startTime = schedule.getStartDate().format(timeFormatter).toLowerCase(); // To make AM/PM lowercase

    // Format end date components
    final String endDay = schedule.getEndDate().format(dayFormatter) + getDayOfMonthSuffix(schedule.getEndDate().getDayOfMonth());
    final String endTime = schedule.getEndDate().format(timeFormatter).toLowerCase(); // To make AM/PM lowercase

    // If start date and end date are on the same day
    if (schedule.getStartDate().toLocalDate().isEqual(schedule.getEndDate().toLocalDate())) {
      // Create the final string for the same day
      return String.format("%s %s, %s %s - %s", startMonth, startDay, startYear, startTime, endTime);
    } else {
      // Create the final string for different days
      return String.format("%s %s, %s %s - %s %s, %s", startMonth, startDay, startYear, startTime, endTime, endDay, schedule.getEndDate().format(yearFormatter));
    }
  }

  /**
   * Returns the appropriate suffix for a given day of the month.
   *
   * <p>This method determines the correct ordinal suffix to be appended to a day
   * number, such as "st" for 1, "nd" for 2, "rd" for 3, and "th" for all other days.
   * Special cases for the teens (11th, 12th, 13th) are handled to ensure accuracy.</p>
   *
   * @param day the day of the month (1-31)
   * @return the ordinal suffix for the given day as a String
   */
  private static String getDayOfMonthSuffix(final int day) {
    if (day >= 11 && day <= 13) {
      return "th";
    }
    return switch (day % 10) {
      case 1 -> "st";
      case 2 -> "nd";
      case 3 -> "rd";
      default -> "th";
    };
  }
}
