package com.fleencorp.feen.model.response.external.google.chat.chat;

import com.fleencorp.feen.model.response.external.google.chat.chat.base.GoogleChatSpaceResponse;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GoogleUpdateChatSpaceResponse {

  private String name;
  private String displayName;
  private String spaceUri;
  private GoogleChatSpaceResponse response;

  public static GoogleUpdateChatSpaceResponse of(final String name, final GoogleChatSpaceResponse response) {
    return GoogleUpdateChatSpaceResponse.builder()
      .name(name)
      .displayName(response.getDisplayName())
      .spaceUri(response.getSpaceUri())
      .response(response)
      .build();
  }
}
