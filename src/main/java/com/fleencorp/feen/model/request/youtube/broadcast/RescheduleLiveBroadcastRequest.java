package com.fleencorp.feen.model.request.youtube.broadcast;

import lombok.*;

import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RescheduleLiveBroadcastRequest {

  private String broadcastId;
  private LocalDateTime scheduledStartDateTime;
  private LocalDateTime scheduledEndDateTime;
  private String timezone;
  private String accessTokenForHttpRequest;

  public static RescheduleLiveBroadcastRequest of(final String accessTokenForHttpRequest,
      final LocalDateTime startDateTime, final LocalDateTime endDateTime, final String timezone, final String broadcastId) {
    return RescheduleLiveBroadcastRequest.builder()
        .scheduledStartDateTime(startDateTime)
        .scheduledEndDateTime(endDateTime)
        .timezone(timezone)
        .broadcastId(broadcastId)
        .accessTokenForHttpRequest(accessTokenForHttpRequest)
        .build();
  }
}
