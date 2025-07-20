package com.fleencorp.feen.stream.model.request.external.broadcast;

import com.fleencorp.feen.common.constant.external.google.youtube.LiveBroadcastClosedCaptionType;
import com.fleencorp.feen.common.constant.external.google.youtube.LiveBroadcastPrivacyStatus;
import com.fleencorp.feen.stream.model.dto.livebroadcast.CreateLiveBroadcastDto;
import com.fleencorp.feen.stream.model.request.broadcast.core.LiveBroadcastRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

import static java.lang.Boolean.parseBoolean;
import static java.util.Objects.nonNull;

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

  public String getClosedCaptionsType() {
    return nonNull(closedCaptionsType) ? closedCaptionsType.getValue() : null;
  }

  public String getPrivacyStatus() {
    return nonNull(privacyStatus) ? privacyStatus.getValue() : null;
  }

  public static CreateLiveBroadcastRequest by(final CreateLiveBroadcastDto dto) {
    return CreateLiveBroadcastRequest.builder()
            .title(dto.getTitle())
            .description(dto.getDescription())
            .thumbnailUrl(dto.getThumbnailUrl())
            .scheduledStartDateTime(dto.getStartDateTime())
            .scheduledEndDateTime(dto.getEndDateTime())
            .madeForKids(parseBoolean(dto.getForKids()))
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
