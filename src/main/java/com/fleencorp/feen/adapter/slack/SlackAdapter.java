package com.fleencorp.feen.adapter.slack;

import com.fleencorp.feen.adapter.slack.model.enums.SlackColor;
import com.fleencorp.feen.adapter.slack.model.request.SendMessageRequest;
import com.slack.api.Slack;
import com.slack.api.model.Attachment;
import com.slack.api.model.Field;
import com.slack.api.webhook.Payload;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static java.util.Objects.nonNull;

/**
 *
 * @see <a href="https://shanepark.tistory.com/430">
 *   [SpringBoot] Send notification to Slack when an error occurs</a>
 * @see <a href="https://velog.io/@gudtjr2949/Spring-Boot-%EA%B3%B5%ED%86%B5-%EC%97%90%EB%9F%AC-%EA%B4%80%EB%A6%AC-Slack-%EC%95%8C%EB%A6%BC-2">
 *   Spring Boot Common Error Management + Slack Notification (2)</a>
 * @see <a href="https://velog.io/@yyy96/spring-aop">
 *   AOP in Practice (feat. Slack Integration)</a>
 * @see <a href="https://velog.io/@ayoung0073/Slack-Bot">
 *   [SpringBoot] Creating a Slack Bot</a>
 *
 * @see <a href="https://velog.io/@devty/Slack-WebHook-%ED%99%9C%EC%9A%A9%ED%95%98%EA%B8%B0-Request-Caching">
 *   Using Slack WebHook (Request Caching)</a>
 */
@Slf4j
@Component
public class SlackAdapter {

  private final Slack slackClient;

  public SlackAdapter(Slack slackClient) {
    this.slackClient = slackClient;
  }

  public void sendMessage(SendMessageRequest sendMessageRequest) {
    String url = sendMessageRequest.getUrl();
    String title = sendMessageRequest.getTitle();
    Map<String, Object> data = sendMessageRequest.getData();
    SlackColor slackColor = sendMessageRequest.getSlackColor();
    try {
      slackClient.send(url, createPayloadWithAttachmentsAndFields(title, data, sendMessageRequest.getIconEmoji(), slackColor));
    } catch (Exception ex) {
      log.error("Error sending message to Slack. Reason: {}", ex.getMessage());
    }
  }

  private Payload createPayloadWithAttachmentsAndFields(String textOrTitle, Map<String, Object> data, String iconEmoji, SlackColor slackColor) {
    List<Attachment> attachments = createAttachmentsWithFields(data);
    setAttachmentsColor(attachments, slackColor);
    return Payload.builder()
        .text(textOrTitle)
        .attachments(attachments)
        .iconEmoji(iconEmoji)
        .build();
  }

  private List<Attachment> createAttachmentsWithFields(Map<String, Object> data) {
    Attachment attachment = Attachment
        .builder()
        .fields(createFields(data))
        .build();
    return List.of(attachment);
  }

  private Attachment createAttachmentByHttpRequest(SendMessageRequest sendMessageRequest) {
    Attachment attachment = new Attachment();
    attachment.setServiceIcon(sendMessageRequest.getIconEmoji());
    attachment.setColor(sendMessageRequest.getSlackColor().getLabel());
    attachment.setTitle(sendMessageRequest.getTitle());
    attachment.setText(sendMessageRequest.getException().getMessage());
    attachment.setTitleLink(sendMessageRequest.getHttpRequest().getContextPath());
    attachment.setFields(createFieldsByHttpRequest(sendMessageRequest.getHttpRequest()));

    return attachment;
  }

  private List<Field> createFieldsByHttpRequest(HttpServletRequest req) {
    return List.of(
        Field.builder().title("Request URL").value(req.getRequestURL().toString()).build(),
        Field.builder().title("Request Method").value(req.getMethod()).build(),
        Field.builder().title("Request Time").value(LocalDateTime.now().toString()).build(),
        Field.builder().title("Request IP").value(req.getRemoteAddr()).build(),
        Field.builder().title("Request User-AgentL").value(req.getHeader("User-Agent")).build()
    );
  }

  private List<Field> createFields(Map<String, Object> data) {
    if (nonNull(data)) {
      return data.entrySet()
          .stream()
          .map(entry -> createSlackField(entry.getKey(), Objects.toString(entry.getValue(), null)))
          .toList();
    }
    return List.of();
  }

  private Field createSlackField(String title, String value) {
    return Field.builder()
        .title(title)
        .value(value)
        .valueShortEnough(false)
        .build();
  }

  private void setAttachmentsColor(List<Attachment> attachments, SlackColor slackColor) {
    if (nonNull(attachments) && nonNull(slackColor)) {
      for (Attachment attachment : attachments) {
        attachment.setColor(slackColor.getColorCode());
      }
    }
  }
}
