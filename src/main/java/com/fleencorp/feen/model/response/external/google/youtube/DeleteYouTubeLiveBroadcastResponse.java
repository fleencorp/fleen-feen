package com.fleencorp.feen.model.response.external.google.youtube;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class DeleteYouTubeLiveBroadcastResponse {

  private String liveBroadcastId;

  public static DeleteYouTubeLiveBroadcastResponse of(final String liveBroadcastId) {
    return DeleteYouTubeLiveBroadcastResponse.builder()
      .liveBroadcastId(liveBroadcastId)
      .build();
  }

}
