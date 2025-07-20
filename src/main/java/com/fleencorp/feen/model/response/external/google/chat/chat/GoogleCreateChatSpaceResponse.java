package com.fleencorp.feen.model.response.external.google.chat.chat;

import com.fleencorp.feen.model.response.external.google.chat.chat.base.GoogleChatSpaceResponse;

import static com.fleencorp.feen.common.util.external.google.GoogleApiUtil.getSpaceIdOrNameFrom;

public record GoogleCreateChatSpaceResponse(String name, String displayName, String spaceUri, GoogleChatSpaceResponse chatSpace) {

  public static GoogleCreateChatSpaceResponse of(final String spaceIdOrName, final GoogleChatSpaceResponse chatSpaceResponse) {
    return new GoogleCreateChatSpaceResponse(getSpaceIdOrNameFrom(spaceIdOrName), chatSpaceResponse.getDisplayName(), chatSpaceResponse.getSpaceUri(), chatSpaceResponse);
  }
}
