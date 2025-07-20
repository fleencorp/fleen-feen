package com.fleencorp.feen.model.response.external.google.chat.membership;

import com.fleencorp.feen.model.response.external.google.chat.membership.base.GoogleChatSpaceMemberResponse;

import static com.fleencorp.feen.common.util.external.google.GoogleApiUtil.getSpaceIdOrNameFrom;
import static com.fleencorp.feen.common.util.external.google.GoogleApiUtil.getSpaceMemberIdOrNameFrom;

public record GoogleAddChatSpaceMemberResponse(String spaceIdOrName, String memberIdOrName, GoogleChatSpaceMemberResponse memberResponse) {

  public static GoogleAddChatSpaceMemberResponse of(final String spaceIdOrName, final String name, final GoogleChatSpaceMemberResponse memberResponse) {
    return new GoogleAddChatSpaceMemberResponse(getSpaceIdOrNameFrom(spaceIdOrName), getSpaceMemberIdOrNameFrom(name), memberResponse);
  }
}
