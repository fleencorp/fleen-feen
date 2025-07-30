package com.fleencorp.feen.stream.model.request.external.broadcast;

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

  public static UpdateLiveBroadcastRequest of(final String accessTokenForHttpRequest, final String title, final String description, final String broadcastId) {
    return UpdateLiveBroadcastRequest.builder()
        .title(title)
        .description(description)
        .broadcastId(broadcastId)
        .accessTokenForHttpRequest(accessTokenForHttpRequest)
        .build();
  }
}
