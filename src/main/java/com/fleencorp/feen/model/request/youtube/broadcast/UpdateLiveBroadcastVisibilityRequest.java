package com.fleencorp.feen.model.request.youtube.broadcast;

import com.fleencorp.feen.constant.external.google.youtube.LiveBroadcastPrivacyStatus;
import com.fleencorp.feen.model.request.youtube.broadcast.base.LiveBroadcastRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import static com.fleencorp.feen.model.request.youtube.broadcast.CreateLiveBroadcastRequest.getVisibility;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateLiveBroadcastVisibilityRequest extends LiveBroadcastRequest {

  private String broadcastId;
  private LiveBroadcastPrivacyStatus privacyStatus;

  public static UpdateLiveBroadcastVisibilityRequest of(final String broadcastId, final String visibility) {
    return UpdateLiveBroadcastVisibilityRequest.builder()
            .broadcastId(broadcastId)
            .privacyStatus(LiveBroadcastPrivacyStatus.of(getVisibility(visibility)))
            .build();
  }
}