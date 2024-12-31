package com.fleencorp.feen.model.response.external.google.chat.chat;

import com.fleencorp.feen.model.response.external.google.chat.chat.base.GoogleChatSpaceResponse;

public record GoogleUpdateChatSpaceResponse(String name, String displayName, String spaceUri, GoogleChatSpaceResponse chatSpaceResponse) {

  public static GoogleUpdateChatSpaceResponse of(final String name, final GoogleChatSpaceResponse chatSpaceResponse) {
    return new GoogleUpdateChatSpaceResponse(name, chatSpaceResponse.getDisplayName(), chatSpaceResponse.getSpaceUri(), chatSpaceResponse);
  }
}
