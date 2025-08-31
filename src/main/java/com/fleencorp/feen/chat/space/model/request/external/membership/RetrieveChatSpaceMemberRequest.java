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
public class RetrieveChatSpaceMemberRequest extends ChatSpaceMemberRequest {

  private String memberSpaceIdOrName;

  public static RetrieveChatSpaceMemberRequest of(final String spaceIdOrName, final String memberSpaceIdOrName) {
    final RetrieveChatSpaceMemberRequest request = new RetrieveChatSpaceMemberRequest();
    request.setSpaceIdOrName(getChatSpaceIdOrNameRequiredPattern(spaceIdOrName));
    request.setMemberSpaceIdOrName(getChatSpaceIdOrNameAndMemberRequiredPattern(spaceIdOrName, memberSpaceIdOrName));

    return request;
  }
}
