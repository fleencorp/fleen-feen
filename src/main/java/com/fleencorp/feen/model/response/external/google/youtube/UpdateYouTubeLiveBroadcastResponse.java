package com.fleencorp.feen.model.response.external.google.youtube;

import com.fleencorp.feen.model.response.external.google.youtube.base.YouTubeLiveBroadcastResponse;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class UpdateYouTubeLiveBroadcastResponse {

  private String liveBroadcastId;
  private String liveStreamLink;
  private YouTubeLiveBroadcastResponse liveBroadcast;

  public static UpdateYouTubeLiveBroadcastResponse of(final String liveBroadcastId, final String liveStreamLink, final YouTubeLiveBroadcastResponse liveBroadcast) {
    return UpdateYouTubeLiveBroadcastResponse.builder()
      .liveBroadcastId(liveBroadcastId)
      .liveStreamLink(liveStreamLink)
      .liveBroadcast(liveBroadcast)
      .build();
  }
}
