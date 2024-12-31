package com.fleencorp.feen.model.response.external.google.youtube;

import com.fleencorp.feen.model.response.external.google.youtube.base.YouTubeLiveBroadcastResponse;

public record RescheduleYouTubeLiveBroadcastResponse(String liveBroadcastId, String liveStreamLink, YouTubeLiveBroadcastResponse liveBroadcastResponse) {

  public static RescheduleYouTubeLiveBroadcastResponse of(final String liveBroadcastId, final String liveStreamLink, final YouTubeLiveBroadcastResponse liveBroadcastResponse) {
    return new RescheduleYouTubeLiveBroadcastResponse(liveBroadcastId, liveStreamLink, liveBroadcastResponse);
  }
}
