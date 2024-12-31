package com.fleencorp.feen.model.response.external.google.youtube;

import com.fleencorp.feen.model.response.external.google.youtube.base.YouTubeLiveBroadcastResponse;

public record CreateYouTubeLiveBroadcastResponse(String liveBroadcastId, String liveStreamLink, YouTubeLiveBroadcastResponse liveBroadcastResponse) {

  public static CreateYouTubeLiveBroadcastResponse of(final String liveBroadcastId, final String liveStreamLink, final YouTubeLiveBroadcastResponse liveBroadcastResponse) {
    return new CreateYouTubeLiveBroadcastResponse(liveBroadcastId, liveStreamLink, liveBroadcastResponse);
  }
}
