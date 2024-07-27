package com.fleencorp.feen.service.external.slack;

import com.fleencorp.feen.adapter.slack.SlackAdapter;
import com.fleencorp.feen.adapter.slack.model.enums.SlackColor;
import com.fleencorp.feen.adapter.slack.model.request.SendMessageRequest;
import com.fleencorp.feen.configuration.external.slack.SlackProperties;
import com.fleencorp.feen.constant.base.MessageLevel;
import com.fleencorp.feen.service.report.ReporterService;
import com.slack.api.methods.MethodsClient;
import com.slack.api.methods.SlackApiException;
import com.slack.api.methods.request.chat.ChatPostMessageRequest;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.IOException;


/**
 * SlackReporter is a service component responsible for sending messages to Slack channels or groups.
 * It uses the Slack API client to post messages.
 *
 * <p>This class is annotated with {@link Slf4j} to enable logging and {@link Component} to be managed
 * by the Spring framework.</p>
 *
 * @author Yusuf Alamu Musa
 * @version 1.0
 *
 * @see <a href="https://velog.io/@gmlstjq123/Slack%EC%9C%BC%EB%A1%9C-%EC%8A%A4%EB%A7%88%ED%8A%B8%ED%8F%B0%EC%97%90-%EB%A9%94%EC%8B%9C%EC%A7%80-%EB%B3%B4%EB%82%B4%EA%B8%B0">
 *   Send messages to your smartphone with Slack</a>
 */
@Slf4j
@Component
public class SlackReporter implements ReporterService {

  private final MethodsClient methodsClient;
  private final SlackProperties slackProperties;
  private final SlackAdapter slackAdapter;

  /**
   * Constructs a SlackReporter with the specified {@link MethodsClient}.
   *
   * @param methodsClient the Slack API client to use for sending messages
   * @param slackProperties the properties like webhook urls or channel ID used in Slack API communication
   * @param slackAdapter the adapter for interacting with slack
   */
  public SlackReporter(
      MethodsClient methodsClient,
      SlackProperties slackProperties,
      SlackAdapter slackAdapter) {
    this.methodsClient = methodsClient;
    this.slackProperties = slackProperties;
    this.slackAdapter = slackAdapter;
  }

  /**
   * Sends a message to a specified Slack channel or group.
   *
   * @param groupOrChannel the ID of the channel or group to send the message to
   * @param message        the message to send
   *
   * @see <a href="https://api.slack.com/methods/chat.postMessage">
   *   chat.postMessage</a>
   * @see <a href="https://velog.io/@blacksooooo/Node.js-%EC%8A%AC%EB%9E%99-%EB%B4%87%EC%9D%84-%EB%A7%8C%EB%93%A4%EC%96%B4-%EB%A9%94%EC%84%B8%EC%A7%80-%EC%9E%90%EB%8F%99%ED%99%94-%ED%95%98%EA%B8%B0">
   *   [Node.js] Create a Slack bot to automate sending messages to a specific channel</a>
   */
  @Override
  @Async
  public void sendMessage(String groupOrChannel, String message) {
    ChatPostMessageRequest chatPostMessageRequest = createMessage(groupOrChannel, message);
    try {
      methodsClient.chatPostMessage(chatPostMessageRequest);
    } catch (SlackApiException | IOException ex) {
      log.error("Error has occurred. Reason: {}", ex.getMessage());
    }
  }

  /**
   * Creates a {@link ChatPostMessageRequest} with the specified channel and message text.
   *
   * @param groupOrChannel the Slack channel or group where the message will be sent
   * @param message the text of the message to send
   * @return a {@link ChatPostMessageRequest} object configured with the provided channel and message
   *
   * @see <a href="https://velog.io/@yujinaa/Slack-API-Client-Java%EB%A5%BC-%EC%82%AC%EC%9A%A9%ED%95%B4-Slack%EC%97%90-%EB%A9%94%EC%84%B8%EC%A7%80-%EB%B3%B4%EB%82%B4%EA%B8%B0">
   *   [Slack API] Sending messages to Slack using Java</a>
   * @see <a href="https://velog.io/@yun8565/Redis-DTO%EB%A5%BC-%EC%A0%80%EC%9E%A5%ED%95%98%EA%B3%A0-%EC%A1%B0%ED%9A%8C%EC%82%AD%EC%A0%9C%ED%95%98%EA%B8%B0-Slack-Webhook-%ED%99%9C%EC%9A%A9%ED%95%98%EA%B8%B0">
   *   [Redis] Save and view/delete DTO + use Slack Webhook</a>
   */
  protected ChatPostMessageRequest createMessage(String groupOrChannel, String message) {
    return ChatPostMessageRequest.builder()
        .channel(groupOrChannel)
        .text(message)
        .build();
  }

  @Override
  public void sendMessage(String groupOrChannelOrUrl, String message, MessageLevel messageLevel) {
    String webhookUrl = getWebhookUrl(messageLevel);
    SlackColor slackColor = getColor(messageLevel);
    slackAdapter.sendMessage(SendMessageRequest.of(webhookUrl, message, null, SlackColor.RED));
  }

  public void sendMessage(HttpServletRequest request, Exception ex) {

  }

  /**
   *
   * @param messageLevel
   * @return
   *
   * @see <a href="https://jsonobject.tistory.com/518">
   *   Sending messages to a Slack channel with Spring Boot, Kotlin</a>
   */
  protected String getWebhookUrl(MessageLevel messageLevel) {
    return switch (messageLevel) {
      case ERROR -> slackProperties.getChannelErrorReportId();
      case INFO -> slackProperties.getChannelInfoReportId();
      case WARN -> slackProperties.getChannelWarnReportId();
    };
  }

  protected SlackColor getColor(MessageLevel messageLevel) {
    return switch (messageLevel) {
      case ERROR -> SlackColor.RED;
      case INFO -> SlackColor.GREEN;
      case WARN -> SlackColor.ORANGE;
    };
  }

}
