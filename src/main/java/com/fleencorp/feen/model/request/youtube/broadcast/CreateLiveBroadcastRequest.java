package com.fleencorp.feen.model.request.youtube.broadcast;

import com.fleencorp.feen.constant.external.google.youtube.LiveBroadcastClosedCaptionType;
import com.fleencorp.feen.constant.external.google.youtube.LiveBroadcastPrivacyStatus;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateLiveBroadcastRequest {

  private String title;
  private String description;
  private LocalDateTime scheduledStartDateTime;
  private LocalDateTime scheduledEndDateTime;
  private String channelId;
  private String thumbnailUrl;
  private String broadcastType;
  private LiveBroadcastClosedCaptionType closedCaptionsType;
  private LiveBroadcastPrivacyStatus privacyStatus;
  private Boolean madeForKids;
  private Boolean enableLowLatencyStreamingOrLowDataStreaming;
  private Boolean enableAutoStart;
  private String accessTokenForHttpRequest;
}
