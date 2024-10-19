package com.fleencorp.feen.model.request.chat.space;

import lombok.*;

import static com.fleencorp.feen.util.external.google.GoogleApiUtil.getChatSpaceIdOrNameRequiredPattern;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DeleteChatSpaceRequest {

  private String spaceIdOrName;

  public static DeleteChatSpaceRequest of(final String spaceIdOrName) {
    return DeleteChatSpaceRequest.builder()
      .spaceIdOrName(getChatSpaceIdOrNameRequiredPattern(spaceIdOrName))
      .build();
  }
}
