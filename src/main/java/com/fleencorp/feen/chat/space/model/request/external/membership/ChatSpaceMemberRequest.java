package com.fleencorp.feen.chat.space.model.request.external.membership;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChatSpaceMemberRequest {

  protected String spaceIdOrName;
}
