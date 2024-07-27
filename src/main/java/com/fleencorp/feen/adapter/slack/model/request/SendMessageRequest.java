package com.fleencorp.feen.adapter.slack.model.request;

import com.fleencorp.feen.adapter.slack.model.enums.SlackColor;
import jakarta.servlet.http.HttpServletRequest;
import lombok.*;

import java.util.Map;

@Builder
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

  public static SendMessageRequest of(String url, String title, Map<String, Object> data, SlackColor slackColor) {
    return SendMessageRequest.builder()
        .url(url)
        .title(title)
        .data(data)
        .slackColor(slackColor)
        .build();
  }

  public static SendMessageRequest of(String url, String title, Map<String, Object> data, SlackColor slackColor, HttpServletRequest httpRequest) {
    return SendMessageRequest.builder()
        .url(url)
        .title(title)
        .data(data)
        .slackColor(slackColor)
        .httpRequest(httpRequest)
        .build();
  }
}
