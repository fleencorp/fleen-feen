package com.fleencorp.feen.model.request.chat.space.membership;

import com.fleencorp.feen.model.request.chat.space.membership.base.ChatSpaceMemberRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import static com.fleencorp.feen.util.external.google.GoogleApiUtil.getChatSpaceIdOrNameAndMemberRequiredPattern;
import static com.fleencorp.feen.util.external.google.GoogleApiUtil.getChatSpaceIdOrNameRequiredPattern;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RetrieveChatSpaceMemberRequest extends ChatSpaceMemberRequest {

  private String memberSpaceIdOrName;

  public static RetrieveChatSpaceMemberRequest of(final String spaceIdOrName, final String memberSpaceIdOrName) {
    return RetrieveChatSpaceMemberRequest.builder()
      .spaceIdOrName(getChatSpaceIdOrNameRequiredPattern(spaceIdOrName))
      .memberSpaceIdOrName(getChatSpaceIdOrNameAndMemberRequiredPattern(spaceIdOrName, memberSpaceIdOrName))
      .build();
  }
}