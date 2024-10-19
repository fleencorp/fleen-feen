package com.fleencorp.feen.model.projection;

import com.fleencorp.feen.constant.chat.space.ChatSpaceRequestToJoinStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChatSpaceMemberSelect {

  private Long chatSpaceId;
  private ChatSpaceRequestToJoinStatus requestToJoinStatus;
}
