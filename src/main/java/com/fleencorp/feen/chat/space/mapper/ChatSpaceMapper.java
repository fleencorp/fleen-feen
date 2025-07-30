package com.fleencorp.feen.chat.space.mapper;

import com.fleencorp.feen.chat.space.constant.core.ChatSpaceRequestToJoinStatus;
import com.fleencorp.feen.chat.space.constant.core.ChatSpaceStatus;
import com.fleencorp.feen.chat.space.constant.member.ChatSpaceMemberRole;
import com.fleencorp.feen.common.constant.common.JoinStatus;
import com.fleencorp.feen.chat.space.model.domain.ChatSpace;
import com.fleencorp.feen.chat.space.model.info.core.ChatSpaceStatusInfo;
import com.fleencorp.feen.chat.space.model.response.core.ChatSpaceResponse;

import java.util.List;

public interface ChatSpaceMapper {

  ChatSpaceResponse toChatSpaceResponse(ChatSpace entry);

  ChatSpaceResponse toChatSpaceResponseByAdminUpdate(ChatSpace entry);

  List<ChatSpaceResponse> toChatSpaceResponses(List<ChatSpace> entries);

  void setMembershipInfo(ChatSpaceResponse chatSpace, ChatSpaceRequestToJoinStatus requestToJoinStatus, JoinStatus joinStatus, ChatSpaceMemberRole memberRole, boolean isAMember, boolean isAdmin, boolean hasLeft, boolean isRemoved);

  ChatSpaceStatusInfo toChatSpaceStatusInfo(ChatSpaceStatus status);
}
