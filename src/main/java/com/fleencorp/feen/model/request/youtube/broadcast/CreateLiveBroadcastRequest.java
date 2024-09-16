package com.fleencorp.feen.model.request.youtube.broadcast;

import com.fleencorp.feen.constant.external.google.youtube.LiveBroadcastClosedCaptionType;
import com.fleencorp.feen.constant.external.google.youtube.LiveBroadcastPrivacyStatus;
import com.fleencorp.feen.model.dto.livebroadcast.CreateLiveBroadcastDto;
import com.fleencorp.feen.model.request.youtube.broadcast.base.LiveBroadcastRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

import static java.lang.Boolean.parseBoolean;

@SuperBuilder
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

  public static CreateLiveBroadcastRequest by(final CreateLiveBroadcastDto dto) {
    return CreateLiveBroadcastRequest.builder()
            .title(dto.getTitle())
            .description(dto.getDescription())
            .thumbnailUrl(dto.getThumbnailUrl())
            .scheduledStartDateTime(dto.getActualStartDateTime())
            .scheduledEndDateTime(dto.getActualEndDateTime())
            .madeForKids(parseBoolean(dto.getIsForKids()))
            .privacyStatus(LiveBroadcastPrivacyStatus.of(getVisibility(dto.getVisibility())))
            .enableLowLatencyStreamingOrLowDataStreaming(true)
            .enableAutoStart(false)
            .closedCaptionsType(LiveBroadcastClosedCaptionType.CLOSED_CAPTIONS_DISABLED)
            .categoryId(dto.getCategoryId())
            .build();
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
