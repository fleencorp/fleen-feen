package com.fleencorp.feen.model.projection.chat.space;

import com.fleencorp.feen.constant.chat.space.ChatSpaceRequestToJoinStatus;
import com.fleencorp.feen.constant.chat.space.ChatSpaceVisibility;
import com.fleencorp.feen.constant.common.JoinStatus;
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
  private Boolean admin;
  private Boolean left;
  private Boolean removed;

  public JoinStatus getJoinStatus() {
    return JoinStatus.getJoinStatus(requestToJoinStatus, visibility, isAMember(), removed);
  }

  public boolean isAMember() {
    return !left && !removed;
  }

  public boolean isAdmin() {
    return admin;
  }
}
