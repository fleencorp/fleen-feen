package com.fleencorp.feen.model.response.external.google.chat.membership;

import com.fleencorp.feen.model.response.external.google.chat.membership.base.GoogleChatSpaceMemberResponse;
import lombok.*;

import static com.fleencorp.feen.util.external.google.GoogleApiUtil.getSpaceIdOrNameFrom;
import static com.fleencorp.feen.util.external.google.GoogleApiUtil.getSpaceMemberIdOrNameFrom;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GoogleAddChatSpaceMemberResponse {

  private String spaceIdOrName;
  private String memberIdOrName;
  private GoogleChatSpaceMemberResponse response;

  public static GoogleAddChatSpaceMemberResponse of(final String spaceIdOrName, final String name, final GoogleChatSpaceMemberResponse response) {
    return GoogleAddChatSpaceMemberResponse.builder()
      .spaceIdOrName(getSpaceIdOrNameFrom(spaceIdOrName))
      .memberIdOrName(getSpaceMemberIdOrNameFrom(name))
      .response(response)
      .build();
  }
}
