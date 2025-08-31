package com.fleencorp.feen.chat.space.model.request.external.core;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateChatSpaceRequest extends CreateChatSpaceRequest {

  private String spaceIdOrName;

  public static UpdateChatSpaceRequest of(final String spaceIdOrName, final String displayName, final String description, final String guidelinesOrRules) {
    final UpdateChatSpaceRequest request = new UpdateChatSpaceRequest();
    request.setSpaceIdOrName(spaceIdOrName);
    request.setDisplayName(displayName);
    request.setDescription(description);
    request.setGuidelinesOrRules(guidelinesOrRules);

    return request;
  }
}
