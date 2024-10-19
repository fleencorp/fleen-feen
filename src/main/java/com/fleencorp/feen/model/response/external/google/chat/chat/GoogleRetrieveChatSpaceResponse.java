package com.fleencorp.feen.model.response.external.google.chat.chat;

import com.fleencorp.feen.model.response.external.google.chat.chat.base.GoogleChatSpaceResponse;
import com.google.chat.v1.Space;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GoogleRetrieveChatSpaceResponse {

  private String name;
  private String spaceUri;
  private GoogleChatSpaceResponse response;
  private Space space;

  public static GoogleRetrieveChatSpaceResponse of(final String name, final GoogleChatSpaceResponse response, final Space space) {
    return GoogleRetrieveChatSpaceResponse.builder()
      .name(name)
      .spaceUri(response.getSpaceUri())
      .response(response)
      .space(space)
      .build();
  }

  public static GoogleRetrieveChatSpaceResponse of() {
    return new GoogleRetrieveChatSpaceResponse();
  }

}
