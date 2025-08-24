package com.fleencorp.feen.chat.space.mapper;

import com.fleencorp.feen.chat.space.constant.core.ChatSpaceRequestToJoinStatus;
import com.fleencorp.feen.chat.space.constant.member.ChatSpaceMemberRole;
import com.fleencorp.feen.chat.space.model.domain.ChatSpace;
import com.fleencorp.feen.chat.space.model.domain.ChatSpaceMember;
import com.fleencorp.feen.chat.space.model.info.member.ChatSpaceMemberRoleInfo;
import com.fleencorp.feen.chat.space.model.info.member.ChatSpaceRequestToJoinStatusInfo;
import com.fleencorp.feen.chat.space.model.info.membership.*;
import com.fleencorp.feen.chat.space.model.response.core.ChatSpaceResponse;
import com.fleencorp.feen.chat.space.model.response.member.base.ChatSpaceMemberResponse;
import com.fleencorp.feen.common.constant.common.JoinStatus;
import com.fleencorp.feen.common.model.info.JoinStatusInfo;

import java.util.List;

public interface ChatSpaceMemberMapper {

  ChatSpaceMemberResponse toChatSpaceMemberResponse(ChatSpaceMember entry, ChatSpace chatSpace);

  ChatSpaceMembershipInfo getMembershipInfo(ChatSpaceMember entry, ChatSpace chatSpace);

  List<ChatSpaceMemberResponse> toChatSpaceMemberResponses(List<ChatSpaceMember> entries, ChatSpace chatSpace);

  List<ChatSpaceMemberResponse> toChatSpaceMemberResponsesPublic(List<ChatSpaceMember> entries);

  ChatSpaceMemberRoleInfo toMemberRoleInfo(ChatSpaceMemberRole chatSpaceMemberRole);

  ChatSpaceRequestToJoinStatusInfo toRequestToJoinStatusInfo(ChatSpaceResponse chatSpace, ChatSpaceRequestToJoinStatus requestToJoinStatus);

  JoinStatusInfo toJoinStatusInfo(ChatSpaceResponse chatSpace, JoinStatus joinStatus);

  IsAChatSpaceMemberInfo toIsAChatSpaceMemberInfo(boolean isAMember);

  IsAChatSpaceAdminInfo toIsAChatSpaceAdminInfo(boolean isAdmin);

  IsChatSpaceMemberRemovedInfo toIsChatSpaceMemberRemovedInfo(boolean isRemoved);

  IsChatSpaceMemberLeftInfo toIsChatSpaceMemberLeftInfo(boolean hasLeft);
}
