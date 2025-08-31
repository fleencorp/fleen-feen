package com.fleencorp.feen.chat.space.model.request.external.membership;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static com.fleencorp.feen.common.util.external.google.GoogleApiUtil.getChatSpaceIdOrNameAndMemberRequiredPattern;
import static com.fleencorp.feen.common.util.external.google.GoogleApiUtil.getChatSpaceIdOrNameRequiredPattern;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RemoveChatSpaceMemberRequest extends ChatSpaceMemberRequest {

  private String memberSpaceIdOrName;

  public static RemoveChatSpaceMemberRequest of(final String spaceIdOrName, final String memberSpaceIdOrName) {
    final RemoveChatSpaceMemberRequest request = new RemoveChatSpaceMemberRequest();
    request.setSpaceIdOrName(getChatSpaceIdOrNameRequiredPattern(spaceIdOrName));
    request.setMemberSpaceIdOrName(getChatSpaceIdOrNameAndMemberRequiredPattern(spaceIdOrName, memberSpaceIdOrName));

    return request;
  }
}
