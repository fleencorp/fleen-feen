package com.fleencorp.feen.model.request.youtube.broadcast;

import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DeleteLiveBroadcastRequest {

  private String broadcastId;
  private String accessTokenForHttpRequest;

  public static DeleteLiveBroadcastRequest of(final String broadcastId, final String accessToken) {
    return DeleteLiveBroadcastRequest.builder()
            .broadcastId(broadcastId)
            .accessTokenForHttpRequest(accessToken)
            .build();
  }

}
