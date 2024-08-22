package com.fleencorp.feen.util.external.google;

import com.fleencorp.feen.service.impl.external.google.oauth2.GoogleOauth2Service;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.util.DateTime;

import java.time.LocalDateTime;
import java.util.Date;

import static com.fleencorp.feen.constant.external.google.youtube.base.YouTubeParameter.LIVE_STREAMING_BASE_LINK;
import static com.fleencorp.feen.util.DateTimeUtil.toDate;
import static com.google.auth.http.AuthHttpConstants.AUTHORIZATION;
import static com.google.auth.http.AuthHttpConstants.BEARER;
import static java.util.Objects.nonNull;


public class GoogleApiUtil {

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

  public static String getYouTubeLiveStreamingLinkByBroadcastId(final String broadcastId) {
    return LIVE_STREAMING_BASE_LINK + broadcastId;
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
      httpRequest.setParser(new JsonObjectParser(GoogleOauth2Service.getJsonFactory()));
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
}
