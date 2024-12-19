package com.fleencorp.feen.model.response.external.google.youtube;

public record DeleteYouTubeLiveBroadcastResponse(String liveBroadcastId) {

  public static DeleteYouTubeLiveBroadcastResponse of(final String liveBroadcastId) {
    return new DeleteYouTubeLiveBroadcastResponse(liveBroadcastId);
  }

}
