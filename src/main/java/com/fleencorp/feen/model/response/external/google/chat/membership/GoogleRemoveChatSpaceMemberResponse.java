package com.fleencorp.feen.model.response.external.google.chat.membership;

import static com.fleencorp.feen.common.util.external.google.GoogleApiUtil.getSpaceIdOrNameFrom;
import static com.fleencorp.feen.common.util.external.google.GoogleApiUtil.getSpaceMemberIdOrNameFrom;

public record GoogleRemoveChatSpaceMemberResponse(String memberSpaceIdOrName, String spaceIdOrName) {

  public static GoogleRemoveChatSpaceMemberResponse of(final String memberSpaceId, final String spaceIdOrName) {
    return new GoogleRemoveChatSpaceMemberResponse(getSpaceMemberIdOrNameFrom(memberSpaceId), getSpaceIdOrNameFrom(spaceIdOrName));
  }
}
