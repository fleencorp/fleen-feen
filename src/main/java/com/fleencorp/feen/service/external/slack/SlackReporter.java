package com.fleencorp.feen.service.external.slack;

import com.fleencorp.feen.service.report.ReporterService;
import com.slack.api.methods.MethodsClient;
import com.slack.api.methods.SlackApiException;
import com.slack.api.methods.request.chat.ChatPostMessageRequest;
import lombok.extern.slf4j.Slf4j;
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
 */
@Slf4j
@Component
public class SlackReporter implements ReporterService {

  private final MethodsClient methodsClient;

  /**
   * Constructs a SlackReporter with the specified {@link MethodsClient}.
   *
   * @param methodsClient the Slack API client to use for sending messages
   */
  public SlackReporter(MethodsClient methodsClient) {
    this.methodsClient = methodsClient;
  }


  /**
   * Sends a message to a specified Slack channel or group.
   *
   * @param groupOrChannel the ID of the channel or group to send the message to
   * @param message        the message to send
   */
  @Override
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
   */
  protected ChatPostMessageRequest createMessage(String groupOrChannel, String message) {
    return ChatPostMessageRequest.builder()
        .channel(groupOrChannel)
        .text(message)
        .build();
  }
}
