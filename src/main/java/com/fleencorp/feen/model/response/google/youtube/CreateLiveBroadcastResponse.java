package com.fleencorp.feen.model.response.google.youtube;

import com.fleencorp.feen.model.response.google.youtube.base.LiveBroadcastResponse;
import lombok.Builder;

@Builder
public class CreateLiveBroadcastResponse {

  private String liveBroadcastId;
  private LiveBroadcastResponse liveBroadcast;
}
