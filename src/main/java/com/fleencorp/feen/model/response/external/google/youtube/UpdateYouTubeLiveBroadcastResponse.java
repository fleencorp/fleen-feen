package com.fleencorp.feen.model.response.external.google.youtube;

import com.fleencorp.feen.model.response.external.google.youtube.base.YouTubeLiveBroadcastResponse;
import lombok.Builder;

@Builder
public class UpdateYouTubeLiveBroadcastResponse {

  private String liveBroadcastId;
  private String liveStreamLink;
  private YouTubeLiveBroadcastResponse liveBroadcast;
}
