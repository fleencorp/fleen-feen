package com.fleencorp.feen.model.response.external.google.chat.chat;

import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GoogleDeleteChatSpaceResponse {

  private String spaceIdOrName;

  public static GoogleDeleteChatSpaceResponse of(final String spaceIdOrName) {
    return GoogleDeleteChatSpaceResponse.builder()
      .spaceIdOrName(spaceIdOrName)
      .build();
  }
}
