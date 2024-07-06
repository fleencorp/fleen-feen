package com.fleencorp.feen.model.request.youtube.broadcast;

import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateLiveBroadcastRequest {

  private String title;
  private String description;
  private String channelId;
  private String broadcastId;
  private String accessTokenForHttpRequest;

  public static UpdateLiveBroadcastRequest of(final String accessTokenForHttpRequest, final String title, final String description) {
    return UpdateLiveBroadcastRequest.builder()
        .title(title)
        .description(description)
        .accessTokenForHttpRequest(accessTokenForHttpRequest)
        .build();
  }
}
