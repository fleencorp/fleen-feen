package com.fleencorp.feen.chat.space.model.request.external.membership;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import static com.fleencorp.feen.common.util.external.google.GoogleApiUtil.getChatSpaceIdOrNameAndMemberRequiredPattern;
import static com.fleencorp.feen.common.util.external.google.GoogleApiUtil.getChatSpaceIdOrNameRequiredPattern;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RemoveChatSpaceMemberRequest extends ChatSpaceMemberRequest {

  private String memberSpaceIdOrName;

  public static RemoveChatSpaceMemberRequest of(final String spaceIdOrName, final String memberSpaceIdOrName) {
    return RemoveChatSpaceMemberRequest.builder()
      .spaceIdOrName(getChatSpaceIdOrNameRequiredPattern(spaceIdOrName))
      .memberSpaceIdOrName(getChatSpaceIdOrNameAndMemberRequiredPattern(spaceIdOrName, memberSpaceIdOrName))
      .build();
  }
}
