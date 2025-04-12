package com.fleencorp.feen.mapper.chat;

import com.fleencorp.feen.constant.chat.space.ChatSpaceRequestToJoinStatus;
import com.fleencorp.feen.constant.chat.space.ChatSpaceStatus;
import com.fleencorp.feen.constant.chat.space.member.ChatSpaceMemberRole;
import com.fleencorp.feen.constant.common.JoinStatus;
import com.fleencorp.feen.model.domain.chat.ChatSpace;
import com.fleencorp.feen.model.info.chat.space.ChatSpaceStatusInfo;
import com.fleencorp.feen.model.response.chat.space.base.ChatSpaceResponse;

import java.util.List;

public interface ChatSpaceMapper {

  ChatSpaceResponse toChatSpaceResponse(ChatSpace entry);

  ChatSpaceResponse toChatSpaceResponseByAdminUpdate(ChatSpace entry);

  List<ChatSpaceResponse> toChatSpaceResponses(List<ChatSpace> entries);

  void setMembershipInfo(ChatSpaceResponse chatSpace, ChatSpaceRequestToJoinStatus requestToJoinStatus, JoinStatus joinStatus, ChatSpaceMemberRole memberRole, boolean isAMember, boolean isAdmin, boolean hasLeft, boolean isRemoved);

  ChatSpaceStatusInfo toChatSpaceStatusInfo(ChatSpaceStatus status);
}
