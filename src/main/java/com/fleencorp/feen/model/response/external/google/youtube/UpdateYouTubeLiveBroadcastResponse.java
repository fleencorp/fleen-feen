package com.fleencorp.feen.model.response.external.google.youtube;

import com.fleencorp.feen.model.response.external.google.youtube.base.YouTubeLiveBroadcastResponse;

public record UpdateYouTubeLiveBroadcastResponse(String liveBroadcastId, String liveStreamLink, YouTubeLiveBroadcastResponse liveBroadcastResponse) {

  public static UpdateYouTubeLiveBroadcastResponse of(final String liveBroadcastId, final String liveStreamLink, final YouTubeLiveBroadcastResponse liveBroadcast) {
    return new UpdateYouTubeLiveBroadcastResponse(liveBroadcastId, liveStreamLink, liveBroadcast);
  }
}
