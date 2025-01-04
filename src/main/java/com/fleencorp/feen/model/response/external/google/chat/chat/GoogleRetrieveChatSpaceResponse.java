package com.fleencorp.feen.model.response.external.google.chat.chat;

import com.fleencorp.feen.model.response.external.google.chat.chat.base.GoogleChatSpaceResponse;
import com.google.chat.v1.Space;

public record GoogleRetrieveChatSpaceResponse(String name, String spaceUri, GoogleChatSpaceResponse chatSpaceResponse, Space chatSpace) {

  public static GoogleRetrieveChatSpaceResponse of(final String name, final GoogleChatSpaceResponse chatSpaceResponse, final Space space) {
    return new GoogleRetrieveChatSpaceResponse(name, chatSpaceResponse.getSpaceUri(), chatSpaceResponse, space);
  }

}
