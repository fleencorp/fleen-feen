package com.fleencorp.feen.stream.model.request.external.broadcast;

import com.fleencorp.feen.common.constant.external.google.youtube.LiveBroadcastClosedCaptionType;
import com.fleencorp.feen.common.constant.external.google.youtube.LiveBroadcastPrivacyStatus;
import com.fleencorp.feen.stream.model.dto.livebroadcast.CreateLiveBroadcastDto;
import com.fleencorp.feen.stream.model.request.external.broadcast.core.LiveBroadcastRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

import static java.util.Objects.nonNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateLiveBroadcastRequest extends LiveBroadcastRequest {

  private String title;
  private String description;
  private LocalDateTime scheduledStartDateTime;
  private LocalDateTime scheduledEndDateTime;
  private String channelId;
  private String categoryId;
  private String thumbnailUrl;
  private String broadcastType;
  private LiveBroadcastClosedCaptionType closedCaptionsType;
  private LiveBroadcastPrivacyStatus privacyStatus;
  private Boolean madeForKids;
  private Boolean enableLowLatencyStreamingOrLowDataStreaming;
  private Boolean enableAutoStart;

  public String getClosedCaptionsType() {
    return nonNull(closedCaptionsType) ? closedCaptionsType.getValue() : null;
  }

  public String getPrivacyStatus() {
    return nonNull(privacyStatus) ? privacyStatus.getValue() : null;
  }

  public static CreateLiveBroadcastRequest by(final CreateLiveBroadcastDto dto) {
    final CreateLiveBroadcastRequest request = new CreateLiveBroadcastRequest();
    request.setTitle(dto.getTitle());
    request.setDescription(dto.getDescription());
    request.setThumbnailUrl(dto.getThumbnailUrl());
    request.setScheduledStartDateTime(dto.getStartDateTime());
    request.setScheduledEndDateTime(dto.getEndDateTime());
    request.setMadeForKids(dto.getForKids());
    request.setPrivacyStatus(LiveBroadcastPrivacyStatus.of(getVisibility(dto.getVisibility())));
    request.setEnableLowLatencyStreamingOrLowDataStreaming(true);
    request.setEnableAutoStart(false);
    request.setClosedCaptionsType(LiveBroadcastClosedCaptionType.CLOSED_CAPTIONS_DISABLED);
    request.setCategoryId(dto.getCategoryId());

    return request;
  }

  public String getLiveStreamFormat() {
    return "1080p";
  }

  public String getIngestionType() {
    return "rtmp";
  }

  public String getLiveStreamResolution() {
    return "1080p";
  }

  public String getLiveStreamFrameRate() {
    return "60fps";
  }

  public String getStreamKind() {
    return "youtube#liveStream";
  }


  /**
   * Updates the access token used for HTTP requests.
   *
   * @param accessTokenForHttpRequest the new access token to be set
   */
  public void updateToken(final String accessTokenForHttpRequest) {
    this.accessTokenForHttpRequest = accessTokenForHttpRequest;
  }
}
