package com.fleencorp.feen.model.response.external.google.youtube;

import com.fleencorp.feen.model.response.external.google.youtube.base.YouTubeLiveBroadcastResponse;
import lombok.Builder;

@Builder
public class RescheduleYouTubeLiveBroadcastResponse {

  private String liveBroadcastId;
  private String liveStreamLink;
  private YouTubeLiveBroadcastResponse liveBroadcast;

  public static RescheduleYouTubeLiveBroadcastResponse of(final String liveBroadcastId, final String liveStreamLink, final YouTubeLiveBroadcastResponse liveBroadcast) {
    return RescheduleYouTubeLiveBroadcastResponse.builder()
      .liveBroadcastId(liveBroadcastId)
      .liveStreamLink(liveStreamLink)
      .liveBroadcast(liveBroadcast)
      .build();
  }
}
