package com.fleencorp.feen.model.response.external.google.chat.membership;

import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GoogleUpdateChatSpaceMemberResponse {

  private String memberSpaceId;
  private String username;
}
