package com.fleencorp.feen.chat.space.model.request.external.core;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import static com.fleencorp.feen.common.util.external.google.GoogleApiUtil.getChatSpaceIdOrNameRequiredPattern;

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
