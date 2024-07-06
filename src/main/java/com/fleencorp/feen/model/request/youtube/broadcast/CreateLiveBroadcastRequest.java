package com.fleencorp.feen.model.request.youtube.broadcast;

import com.fleencorp.feen.constant.external.google.youtube.LiveBroadcastClosedCaptionType;
import com.fleencorp.feen.constant.external.google.youtube.LiveBroadcastPrivacyStatus;
import com.fleencorp.feen.constant.stream.StreamVisibility;
import com.fleencorp.feen.constant.external.google.youtube.base.YouTubeLiveBroadcastVisibility;
import com.fleencorp.feen.model.dto.livebroadcast.CreateLiveBroadcastDto;
import lombok.*;

import java.time.LocalDateTime;

import static java.lang.Boolean.parseBoolean;

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
  private String categoryId;
  private String thumbnailUrl;
  private String broadcastType;
  private LiveBroadcastClosedCaptionType closedCaptionsType;
  private LiveBroadcastPrivacyStatus privacyStatus;
  private Boolean madeForKids;
  private Boolean enableLowLatencyStreamingOrLowDataStreaming;
  private Boolean enableAutoStart;
  private String accessTokenForHttpRequest;

  public static CreateLiveBroadcastRequest by(final CreateLiveBroadcastDto createLiveBroadcastDto) {
    return CreateLiveBroadcastRequest.builder()
            .title(createLiveBroadcastDto.getTitle())
            .description(createLiveBroadcastDto.getDescription())
            .thumbnailUrl(createLiveBroadcastDto.getThumbnailUrl())
            .scheduledStartDateTime(createLiveBroadcastDto.getStartDateTime())
            .scheduledEndDateTime(createLiveBroadcastDto.getEndDateTime())
            .madeForKids(parseBoolean(createLiveBroadcastDto.getIsForKids()))
            .privacyStatus(LiveBroadcastPrivacyStatus.valueOf(getVisibility(createLiveBroadcastDto.getVisibility())))
            .enableLowLatencyStreamingOrLowDataStreaming(true)
            .enableAutoStart(false)
            .closedCaptionsType(LiveBroadcastClosedCaptionType.CLOSED_CAPTIONS_DISABLED)
            .build();
  }


  /**
  * Converts a visibility value from application-specific StreamVisibility to YouTubeLiveBroadcastVisibility format.
  *
  * <p>This method is useful for mapping visibility settings between different systems, ensuring consistent handling
  * of privacy settings across platforms.</p>
  *
  * @param visibility the visibility value to convert
  * @return the corresponding YouTube visibility value
  */
  public static String getVisibility(final String visibility) {
    if (StreamVisibility.PRIVATE.getValue().equalsIgnoreCase(visibility)) {
      return YouTubeLiveBroadcastVisibility.PRIVATE.getValue();
    } else if (StreamVisibility.PROTECTED.getValue().equalsIgnoreCase(visibility)) {
      return YouTubeLiveBroadcastVisibility.UNLISTED.getValue();
    } else if (StreamVisibility.PUBLIC.getValue().equalsIgnoreCase(visibility)) {
      return visibility;
    }
    return StreamVisibility.PUBLIC.getValue();
  }
}
