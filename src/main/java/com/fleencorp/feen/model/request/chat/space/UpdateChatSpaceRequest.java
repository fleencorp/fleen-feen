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
public class UpdateChatSpaceRequest extends CreateChatSpaceRequest {

  private String spaceIdOrName;

  public static UpdateChatSpaceRequest of(final String spaceIdOrName, final String displayName, final String description, final String guidelinesOrRules) {
    return UpdateChatSpaceRequest.builder()
      .spaceIdOrName(getChatSpaceIdOrNameRequiredPattern(spaceIdOrName))
      .displayName(displayName)
      .description(description)
      .guidelinesOrRules(guidelinesOrRules)
      .build();
  }
}
