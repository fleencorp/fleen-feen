package com.fleencorp.feen.service.external.slack;

import com.fleencorp.feen.adapter.slack.SlackAdapter;
import com.fleencorp.feen.adapter.slack.model.enums.SlackColor;
import com.fleencorp.feen.adapter.slack.model.request.SendMessageRequest;
import com.fleencorp.feen.configuration.external.slack.SlackProperties;
import com.fleencorp.feen.constant.base.ReportMessageType;
import com.fleencorp.feen.service.report.ReporterService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;


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
@Qualifier("slack")
public class SlackReporter implements ReporterService {

  private final SlackAdapter slackAdapter;
  private final SlackProperties slackProperties;

  /**
   * Constructs a new {@code SlackReporter} instance.
   *
   * @param slackAdapter The {@link SlackAdapter} used for sending messages to Slack.
   * @param slackProperties The {@link SlackProperties} containing configuration details such as webhook URLs and channel IDs.
   */
  public SlackReporter(
      SlackAdapter slackAdapter,
      SlackProperties slackProperties) {
    this.slackAdapter = slackAdapter;
    this.slackProperties = slackProperties;
  }

  /**
   * Sends a message to a general reporting channel asynchronously.
   * The message is sent using the configured Slack adapter.
   *
   * @param message The content of the message to be sent.
   */
  @Override
  @Async
  public void sendMessage(String message) {
    slackAdapter.sendMessage(getChannelId(ReportMessageType.GENERAL), message);
  }

  /**
   * Asynchronously sends a message to the specified Slack channel based on the report message type.
   *
   * @param message The message content to be sent.
   * @param reportMessageType The type of report message, which determines the Slack channel and color coding.
   *
   * @see <a href="https://velog.io/@blacksooooo/Node.js-%EC%8A%AC%EB%9E%99-%EB%B4%87%EC%9D%84-%EB%A7%8C%EB%93%A4%EC%96%B4-%EB%A9%94%EC%84%B8%EC%A7%80-%EC%9E%90%EB%8F%99%ED%99%94-%ED%95%98%EA%B8%B0">
   *   [Node.js] Create a Slack bot to automate sending messages to a specific channel</a>
   */
  @Override
  @Async
  public void sendMessage(String message, ReportMessageType reportMessageType) {
    String channelId = getChannelId(reportMessageType);
    SlackColor slackColor = getColor(reportMessageType);
    slackAdapter.sendMessage(SendMessageRequest.of(channelId, message, null, slackColor));
  }

  /**
   * Retrieves the Slack channel ID associated with a specific report message type.
   *
   * @param reportMessageType The type of report message, used to select the appropriate Slack channel ID.
   * @return The Slack channel ID corresponding to the specified report message type.
   *
   * @see <a href="https://jsonobject.tistory.com/518">
   *   Sending messages to a Slack channel with Spring Boot, Kotlin</a>
   */
  protected String getChannelId(ReportMessageType reportMessageType) {
    // Determine the Slack channel ID based on the type of report message
    return switch (reportMessageType) {
      case ERROR -> slackProperties.getChannelErrorReportId();
      case INFO -> slackProperties.getChannelInfoReportId();
      case WARN -> slackProperties.getChannelWarnReportId();
      case GENERAL -> slackProperties.getChannelGeneralReportId();
      case GOOGLE_CALENDAR -> slackProperties.getChannelGoogleCalendarReportId();
      case GOOGLE_OAUTH2 -> slackProperties.getChannelGoogleOauth2ReportId();
      case PROFILE_VERIFICATION -> slackProperties.getChannelVerificationReportId();
      case YOUTUBE -> slackProperties.getChannelYoutubeReportId();
    };
  }

  /**
   * Determines the Slack color associated with a given report message type.
   *
   * @param reportMessageType The type of report message, used to select the appropriate color.
   * @return The SlackColor corresponding to the specified report message type.
   *
   * @see <a href="https://velog.io/@yun8565/Redis-DTO%EB%A5%BC-%EC%A0%80%EC%9E%A5%ED%95%98%EA%B3%A0-%EC%A1%B0%ED%9A%8C%EC%82%AD%EC%A0%9C%ED%95%98%EA%B8%B0-Slack-Webhook-%ED%99%9C%EC%9A%A9%ED%95%98%EA%B8%B0">
   *   [Redis] Save and view/delete DTO + use Slack Webhook</a>
   */
  protected SlackColor getColor(ReportMessageType reportMessageType) {
    // Determine the Slack color based on the type of report message
    return switch (reportMessageType) {
      case ERROR -> SlackColor.RED;
      case INFO -> SlackColor.GREEN;
      case WARN -> SlackColor.ORANGE;
      case GENERAL, GOOGLE_CALENDAR, GOOGLE_OAUTH2, PROFILE_VERIFICATION, YOUTUBE -> SlackColor.BLUE;
    };
  }

}
