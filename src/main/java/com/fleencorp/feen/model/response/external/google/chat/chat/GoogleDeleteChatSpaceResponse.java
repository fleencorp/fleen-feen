package com.fleencorp.feen.model.response.external.google.chat.chat;

public record GoogleDeleteChatSpaceResponse(String spaceIdOrName) {

  public static GoogleDeleteChatSpaceResponse of(final String spaceIdOrName) {
    return new GoogleDeleteChatSpaceResponse(spaceIdOrName);
  }
}
