package com.fleencorp.feen.model.request.chat.space;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import static com.fleencorp.feen.util.external.google.GoogleApiUtil.getChatSpaceIdOrNameRequiredPattern;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RetrieveChatSpaceRequest extends ChatSpaceRequest {

  private String spaceIdOrName;

  public static RetrieveChatSpaceRequest of(final String spaceIdOrName) {
    return RetrieveChatSpaceRequest.builder()
      .spaceIdOrName(getChatSpaceIdOrNameRequiredPattern(spaceIdOrName))
      .build();
  }
}
