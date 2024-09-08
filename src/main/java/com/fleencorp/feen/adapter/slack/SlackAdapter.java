package com.fleencorp.feen.adapter.slack;

import com.fleencorp.feen.adapter.slack.model.enums.SlackColor;
import com.fleencorp.feen.adapter.slack.model.request.SendMessageRequest;
import com.slack.api.Slack;
import com.slack.api.methods.MethodsClient;
import com.slack.api.methods.SlackApiException;
import com.slack.api.methods.request.chat.ChatPostMessageRequest;
import com.slack.api.model.Attachment;
import com.slack.api.model.Field;
import com.slack.api.webhook.Payload;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static java.util.Objects.nonNull;

/**
 * Adapter class for integrating with Slack API.
 * Provides methods for sending messages and interacting with Slack channels.
 *
 * @author Yusuf Alamu Musa
 * @version 1.0

 * @see <a href="https://shanepark.tistory.com/430">
 *   [SpringBoot] Send notification to Slack when an error occurs</a>
 *
 */
@Slf4j
@Component
public class SlackAdapter {

  private final Slack slackClient;
  private final MethodsClient methodsClient;

  /**
   * Constructs a new SlackAdapter instance with the given Slack client and Methods client.
   *
   * @param slackClient  The Slack client used for interacting with the Slack API.
   * @param methodsClient  The client providing methods for Slack API calls.
   */
  public SlackAdapter(
      final Slack slackClient,
      final MethodsClient methodsClient) {
    this.slackClient = slackClient;
    this.methodsClient = methodsClient;
  }


  /**
   * Sends a message to a specified Slack channel or webhook URL using the details provided in the {@link SendMessageRequest}.
   *
   * <p>This method prepares a message payload including attachments with fields based on the provided data,
   * and sends it to the specified Slack URL. If an error occurs during the sending process, it logs an error message.</p>
   *
   * @param sendMessageRequest An object containing the details of the message to be sent, including the target URL,
   *                           message title, data map, Slack color, and optional icon emoji.
   */
  public void sendMessage(final SendMessageRequest sendMessageRequest) {
    final String url = sendMessageRequest.getUrl();
    final String title = sendMessageRequest.getTitle();
    final Map<String, Object> data = sendMessageRequest.getData();
    final SlackColor slackColor = sendMessageRequest.getSlackColor();
    try {
      slackClient.send(url, createPayloadWithAttachmentsAndFields(title, data, sendMessageRequest.getIconEmoji(), slackColor));
    } catch (final Exception ex) {
      log.error("Error sending message to Slack. Reason: {}", ex.getMessage());
    }
  }

  /**
   * Creates a Slack {@link Payload} object that includes attachments with fields populated from the provided data.
   *
   * <p>This method generates a payload for sending a message to Slack, including text, attachments with fields,
   * and an optional icon emoji. It first creates a list of {@link Attachment} objects from the provided data map,
   * then sets the color of these attachments based on the specified {@link SlackColor}.</p>
   *
   * @param textOrTitle The text or title of the Slack message.
   * @param data A map containing the data to be included as fields in the attachment. The map's keys represent
   *             field titles, and the values represent field values.
   * @param iconEmoji An optional emoji to be displayed with the message.
   * @param slackColor The {@link SlackColor} to set for the attachments.
   * @return A {@link Payload} object configured with the specified text, attachments, and icon emoji.
   */
  private Payload createPayloadWithAttachmentsAndFields(final String textOrTitle, final Map<String, Object> data, final String iconEmoji, final SlackColor slackColor) {
    final List<Attachment> attachments = createAttachmentsWithFields(data);
    setAttachmentsColor(attachments, slackColor);
    return Payload.builder()
        .text(textOrTitle)
        .attachments(attachments)
        .iconEmoji(iconEmoji)
        .build();
  }

  /**
   * Creates a list of Slack {@link Attachment} objects populated with fields based on the provided data.
   *
   * <p>This method takes a map of data, where each entry represents a field to be included in the Slack attachment.
   * It creates a single {@link Attachment} object containing these fields and returns a list containing this attachment.
   * This is useful for adding structured information to Slack messages in a readable format.</p>
   *
   * @param data A map containing the data to be included as fields in the attachment. The map's keys represent
   *             field titles, and the values represent field values.
   * @return A list containing a single {@link Attachment} object with the fields set according to the provided data.
   */
  private List<Attachment> createAttachmentsWithFields(final Map<String, Object> data) {
    final Attachment attachment = Attachment
        .builder()
        .fields(createFields(data))
        .build();
    return List.of(attachment);
  }

  /**
   * Creates a Slack {@link Attachment} object based on the details of an HTTP request and the provided message request.
   *
   * <p>This method extracts information from the {@link SendMessageRequest} object, including the service icon, color,
   * title, exception message, and HTTP request details. It then populates an {@link Attachment} object with this
   * information, which can be used to enhance Slack messages with structured data and additional context.</p>
   *
   * @param sendMessageRequest The {@link SendMessageRequest} containing details about the message to be sent.
   * @return An {@link Attachment} object containing structured data about the HTTP request and message.
   *
   * @see <a href="https://velog.io/@devty/Slack-WebHook-%ED%99%9C%EC%9A%A9%ED%95%98%EA%B8%B0-Request-Caching">
   *   Using Slack WebHook (Request Caching)</a>
   */
  private Attachment createAttachmentByHttpRequest(final SendMessageRequest sendMessageRequest) {
    final Attachment attachment = new Attachment();
    attachment.setServiceIcon(sendMessageRequest.getIconEmoji());
    attachment.setColor(sendMessageRequest.getSlackColor().getLabel());
    attachment.setTitle(sendMessageRequest.getTitle());
    attachment.setText(sendMessageRequest.getException().getMessage());
    attachment.setTitleLink(sendMessageRequest.getHttpRequest().getContextPath());
    attachment.setFields(createFieldsByHttpRequest(sendMessageRequest.getHttpRequest()));

    return attachment;
  }

  /**
   * Creates a list of Slack message fields based on the details of an HTTP request.
   *
   * <p>This method extracts information from the provided {@link HttpServletRequest} object, such as the request URL,
   * HTTP method, request time, IP address, and User-Agent. It then creates a list of {@link Field} objects,
   * each representing this information in a structured format suitable for a Slack message.</p>
   *
   * @param req The {@link HttpServletRequest} object containing the details of the HTTP request.
   * @return A list of {@link Field} objects containing information about the HTTP request.
   *
   * @see <a href="https://velog.io/@gudtjr2949/Spring-Boot-%EA%B3%B5%ED%86%B5-%EC%97%90%EB%9F%AC-%EA%B4%80%EB%A6%AC-Slack-%EC%95%8C%EB%A6%BC-2">
   *   Spring Boot Common Error Management + Slack Notification (2)</a>
   */
  private List<Field> createFieldsByHttpRequest(final HttpServletRequest req) {
    return List.of(
        Field.builder().title("Request URL").value(req.getRequestURL().toString()).build(),
        Field.builder().title("Request Method").value(req.getMethod()).build(),
        Field.builder().title("Request Time").value(LocalDateTime.now().toString()).build(),
        Field.builder().title("Request IP").value(req.getRemoteAddr()).build(),
        Field.builder().title("Request User-AgentL").value(req.getHeader("User-Agent")).build()
    );
  }

  /**
   * Creates a list of Slack message fields from the provided data map.
   *
   * <p>This method takes a map where the keys represent field titles and the values represent field content.
   * For each entry in the map, it creates a {@link Field} using the key as the title and the value as the field's content.</p>
   *
   * @param data A map containing the field titles and their corresponding values.
   * @return A list of {@link Field} objects, each representing a field in a Slack message.
   *         If the input data is null, an empty list is returned.
   *
   * @see <a href="https://velog.io/@yyy96/spring-aop">
   *   AOP in Practice (feat. Slack Integration)</a>
   */
  private List<Field> createFields(final Map<String, Object> data) {
    if (nonNull(data)) {
      return data.entrySet()
          .stream()
          .map(entry -> createSlackField(entry.getKey(), Objects.toString(entry.getValue(), null)))
          .toList();
    }
    return List.of();
  }

  /**
   * Creates a new Slack message field with the specified title and value.
   *
   * <p>This method constructs a new {@link Field} object using the provided title and value.
   * The created field will have the property {@code valueShortEnough} set to {@code false}.</p>
   *
   * @param title The title of the field, typically a short label describing the value.
   * @param value The value of the field, typically detailed information related to the title.
   * @return A new {@link Field} object configured with the given title and value.
   *
   * @see <a href="https://velog.io/@ayoung0073/Slack-Bot">
   *   [SpringBoot] Creating a Slack Bot</a>
   */
  private Field createSlackField(final String title, final String value) {
    return Field.builder()
        .title(title)
        .value(value)
        .valueShortEnough(false) // Indicates whether the value is short enough to be displayed side-by-side with other fields
        .build();
  }

  /**
   * Sets the color for each attachment in the list.
   *
   * <p>This method iterates over the provided list of Slack attachments and sets their color property
   * to the specified color code, if both the list and the color are not null.</p>
   *
   * @param attachments A list of Slack attachments to modify.
   * @param slackColor  The color to set on each attachment.
   */
  private void setAttachmentsColor(final List<Attachment> attachments, final SlackColor slackColor) {
    // Check if attachments and slackColor are not null before proceeding
    if (nonNull(attachments) && nonNull(slackColor)) {
      // Iterate through each attachment and set its color to the provided slackColor
      for (final Attachment attachment : attachments) {
        attachment.setColor(slackColor.getColorCode());
      }
    }
  }

  /**
   * Asynchronously sends a message to the general report channel on Slack.
   *
   * <p>This method creates a message request and attempts to send it using the Slack API client.
   * If an exception occurs during the process, it logs an error message.</p>
   *
   * @param channelOrGroup The ID or name of the Slack channel or group where the message will be sent.
   * @param message The message content to be sent to the Slack channel.
   *
   * @see <a href="https://api.slack.com/methods/chat.postMessage">
   *   chat.postMessage</a>
   */
  public void sendMessage(final String channelOrGroup, final String message) {
    final ChatPostMessageRequest chatPostMessageRequest = createMessage(channelOrGroup, message);
    try {
      methodsClient.chatPostMessage(chatPostMessageRequest);
    } catch (final SlackApiException | IOException ex) {
      log.error("Error has occurred. Reason: {}", ex.getMessage());
    }
  }

  /**
   * Creates a ChatPostMessageRequest for sending a message to a Slack channel or group.
   *
   * @param groupOrChannel The name of the group or channel where the message will be posted.
   * @param message        The message content to be sent.
   * @return A ChatPostMessageRequest object containing the channel and message details.
   *
   * @see <a href="https://velog.io/@yujinaa/Slack-API-Client-Java%EB%A5%BC-%EC%82%AC%EC%9A%A9%ED%95%B4-Slack%EC%97%90-%EB%A9%94%EC%84%B8%EC%A7%80-%EB%B3%B4%EB%82%B4%EA%B8%B0">
   *   [Slack API] Sending messages to Slack using Java</a>
   */
  protected ChatPostMessageRequest createMessage(final String groupOrChannel, final String message) {
    return ChatPostMessageRequest.builder()
        .channel(groupOrChannel)
        .text(message)
        .build();
  }
}
