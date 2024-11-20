package com.fleencorp.feen.model.projection;

import com.fleencorp.feen.constant.chat.space.ChatSpaceRequestToJoinStatus;
import com.fleencorp.feen.constant.chat.space.ChatSpaceVisibility;
import com.fleencorp.feen.constant.stream.JoinStatus;
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
  private ChatSpaceVisibility visibility;

  public JoinStatus getJoinStatus() {
    return JoinStatus.getJoinStatus(requestToJoinStatus, visibility);
  }
}
