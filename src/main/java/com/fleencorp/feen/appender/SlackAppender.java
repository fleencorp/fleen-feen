package com.fleencorp.feen.appender;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import com.fleencorp.base.util.StringUtil;
import lombok.Setter;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestTemplate;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static com.fleencorp.base.util.security.AuthUtil.BEARER;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON;


/**
 * Custom appender for sending log events to Slack.
 * Extends AppenderBase to handle logging events and format them for Slack.
 *
 * @author Yusuf Alamu Musa
 * @version 1.0
 *
 * @see <a href="https://velog.io/@swager253/Spring-Slack-%EC%97%90%EB%9F%AC-%EB%AA%A8%EB%8B%88%ED%84%B0%EB%A7%81">
 *   Spring Slack Error Monitoring</a>
 * @see <a href="https://velog.io/@jaeygun/spring-boot-logback-%EC%84%A4%EC%A0%95">
 *   spring boot logback configuration</a>
 */
public class SlackAppender extends AppenderBase<ILoggingEvent> {

  private final String API_URL = "https://slack.com/api/chat.postMessage";
  private final RestTemplate restTemplate;

  @Setter
  private String token;
  @Setter
  private String username;
  private Map<String, String> logLevelChannelIdsMap = new HashMap<>();
  private Map<String, String> logLevelIconEmojisMap = new HashMap<>();

  /**
   * Default constructor for SlackAppender.
   * Initializes a new instance of RestTemplate for making HTTP requests.
   */
  public SlackAppender() {
    this.restTemplate = new RestTemplate();
  }

  /**
   * Sets the mapping of log levels to channel IDs.
   *
   * @param channelIds a string representing the channel IDs mapping, where each entry is separated by a pair separator
   *                   and each key-value pair is separated by a key-value separator
   */
  public void setChannelIds(String channelIds) {
    // Convert the string to a map and set it to logLevelChannelIdsMap
    this.logLevelChannelIdsMap = StringUtil.strToMap(channelIds);
  }

  /**
   * Sets the mapping of log levels to icon emojis.
   *
   * @param iconEmojis a string representing the icon emojis mapping, where each entry is separated by a pair separator
   *                   and each key-value pair is separated by a key-value separator
   */
  public void setIconEmojis(String iconEmojis) {
    // Convert the string to a map and set it to logLevelIconEmojisMap
    this.logLevelIconEmojisMap = StringUtil.strToMap(iconEmojis);
  }

  /**
   * Appends a logging event by creating a formatted message and sending it via a POST request.
   *
   * @param loggingEvent the logging event to be appended
   */
  @Override
  protected void append(ILoggingEvent loggingEvent) {
    // Create a formatted message from the logging event
    final String message = createMessage(loggingEvent);
    // Build the request body with the message and logging level
    Map<String, String> body = createBody(message, loggingEvent.getLevel());
    // Send the request asynchronously
    doCall(body);
  }

  /**
   * Asynchronously sends a POST request with the provided body map.
   *
   * @param body the body of the request as a map of key-value pairs
   */
  protected void doCall(Map<String, String> body) {
    CompletableFuture.runAsync(() -> restTemplate.postForEntity(API_URL, createRequest(body), String.class))
      .exceptionally(_ -> null);
  }

  /**
   * Creates a body map for an HTTP request.
   *
   * @param message the message to include in the body
   * @param level the logging level, used to determine the channel and icon emoji
   * @return a map representing the body of the request with text, token, username, channel, and icon emoji
   */
  protected Map<String, String> createBody(String message, Level level) {
    Map<String, String> body = new HashMap<>();
    body.put("text", message);
    body.put("token", token);
    body.put("username", username);
    body.put("channel", getChannelByLevel(level));
    body.put("icon_emoji", getIconEmojisByLevel(level));

    return body;
  }

  /**
   * Creates an HTTP request entity with headers and body.
   *
   * @param body the body of the request, represented as a map of key-value pairs
   * @return the configured HttpEntity object containing the request body and headers
   */
  protected HttpEntity<Map<String, String>> createRequest(Map<String, String> body) {
    HttpHeaders headers = createHeaders(token);
    return new HttpEntity<>(body, headers);
  }

  /**
   * Creates HTTP headers with authorization and content type for JSON.
   *
   * @param token the authorization token to be included in the headers
   * @return the configured HttpHeaders object
   */
  protected HttpHeaders createHeaders(String token) {
    HttpHeaders headers = new HttpHeaders();
    headers.set(AUTHORIZATION, BEARER + token);
    headers.setContentType(APPLICATION_JSON);

    return headers;
  }

  /**
   * Creates a formatted log message from a logging event.
   *
   * @param event the logging event containing the log data
   * @return the formatted log message string
   */
  private String createMessage(final ILoggingEvent event) {
    final String pattern = "```%s %s %s [%s] - %s```";
    final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    return String.format(pattern,
      dateFormat.format(event.getTimeStamp()),
      event.getLevel(),
      event.getThreadName(),
      event.getLoggerName(),
      event.getFormattedMessage());
  }

  /**
   * Converts a given log level to its string representation in lowercase.
   *
   * @param level the log level to convert
   * @return the lowercase string representation of the log level
   */
  private String getLevel(final Level level) {
    return level.toString().toLowerCase();
  }

  /**
   * Retrieves the channel ID associated with a given log level.
   *
   * @param level the log level for which to retrieve the channel ID
   * @return the channel ID associated with the specified log level, or null if no channel is found
   */
  protected String getChannelByLevel(final Level level) {
    String levelStr = getLevel(level);
    return switch (levelStr) {
      case "error", "info", "warn" -> getChannel(levelStr);
      default -> null;
    };
  }

  /**
   * Retrieves the icon emoji associated with a given log level.
   *
   * @param level the log level for which to retrieve the icon emoji
   * @return the icon emoji associated with the specified log level, or null if no emoji is found
   */
  protected String getIconEmojisByLevel(final Level level) {
    String levelStr = getLevel(level);
    return switch (levelStr) {
      case "error", "info", "warn" -> getIconEmoji(levelStr);
      default -> null;
    };
  }

  /**
   * Retrieves the channel ID associated with a given log level.
   *
   * @param level the log level for which to retrieve the channel ID
   * @return the channel ID associated with the specified log level, or null if no channel is found
   */
  private String getChannel(final String level) {
    return logLevelChannelIdsMap.get(level);
  }

  /**
   * Retrieves the emoji icon associated with a given log level.
   *
   * @param level the log level for which to retrieve the emoji icon
   * @return the emoji icon associated with the specified log level, or null if no icon is found
   */
  private String getIconEmoji(final String level) {
    return logLevelIconEmojisMap.get(level);
  }
}
