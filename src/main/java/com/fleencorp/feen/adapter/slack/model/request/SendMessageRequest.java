package com.fleencorp.feen.adapter.slack.model.request;

import com.fleencorp.feen.adapter.slack.model.enums.SlackColor;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SendMessageRequest {

  private String url;
  private String title;
  private String text;
  private Map<String, Object> data;
  private SlackColor slackColor;
  private String iconEmoji;
  private HttpServletRequest httpRequest;
  private Exception exception;

  public static SendMessageRequest of(final String url, final String title, final Map<String, Object> data, final SlackColor slackColor) {
    final SendMessageRequest sendMessageRequest = new SendMessageRequest();
    sendMessageRequest.setUrl(url);
    sendMessageRequest.setTitle(title);
    sendMessageRequest.setData(data);
    sendMessageRequest.setSlackColor(slackColor);

    return sendMessageRequest;
  }

  public static SendMessageRequest of(final String url, final String title, final Map<String, Object> data, final SlackColor slackColor, final HttpServletRequest httpRequest) {
    final SendMessageRequest sendMessageRequest = of(url, title, data, slackColor);
    sendMessageRequest.setHttpRequest(httpRequest);

    return sendMessageRequest;
  }
}
