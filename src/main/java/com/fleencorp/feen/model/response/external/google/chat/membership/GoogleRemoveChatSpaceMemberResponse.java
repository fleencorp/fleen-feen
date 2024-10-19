package com.fleencorp.feen.model.response.external.google.chat.membership;

import lombok.*;

import static com.fleencorp.feen.util.external.google.GoogleApiUtil.getSpaceIdOrNameFrom;
import static com.fleencorp.feen.util.external.google.GoogleApiUtil.getSpaceMemberIdOrNameFrom;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GoogleRemoveChatSpaceMemberResponse {

  private String memberSpaceIdOrName;
  private String spaceIdOrName;

  public static GoogleRemoveChatSpaceMemberResponse of(final String memberSpaceId, final String spaceIdOrName) {
    return GoogleRemoveChatSpaceMemberResponse.builder()
      .memberSpaceIdOrName(getSpaceMemberIdOrNameFrom(memberSpaceId))
      .spaceIdOrName(getSpaceIdOrNameFrom(spaceIdOrName))
      .build();
  }
}
