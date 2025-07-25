package com.fleencorp.feen.model.projection.chat.space;

import com.fleencorp.feen.constant.chat.space.ChatSpaceRequestToJoinStatus;
import com.fleencorp.feen.constant.chat.space.ChatSpaceVisibility;
import com.fleencorp.feen.constant.chat.space.member.ChatSpaceMemberRole;
import com.fleencorp.feen.constant.common.JoinStatus;
import com.fleencorp.feen.model.contract.HasId;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChatSpaceMemberSelect implements HasId {

  private Long chatSpaceId;
  private ChatSpaceRequestToJoinStatus requestToJoinStatus;
  private ChatSpaceVisibility visibility;
  private Boolean left;
  private Boolean removed;
  private ChatSpaceMemberRole role;
  private Boolean liked;

  @Override
  public Long getNumberId() {
    return chatSpaceId;
  }

  public JoinStatus getJoinStatus() {
    return JoinStatus.getJoinStatus(requestToJoinStatus, visibility, isAMember(), hasLeft(), isRemoved());
  }

  public boolean isAMember() {
    return !left && !removed;
  }

  public boolean isAdmin() {
    return ChatSpaceMemberRole.isAdmin(role);
  }

  public boolean hasLeft() {
    return left;
  }

  public boolean isRemoved() {
    return removed;
  }
}
