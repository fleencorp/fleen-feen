package com.fleencorp.feen.mapper.chat.member;

import com.fleencorp.feen.constant.chat.space.ChatSpaceRequestToJoinStatus;
import com.fleencorp.feen.constant.chat.space.member.ChatSpaceMemberRole;
import com.fleencorp.feen.constant.common.JoinStatus;
import com.fleencorp.feen.model.domain.chat.ChatSpace;
import com.fleencorp.feen.model.domain.chat.ChatSpaceMember;
import com.fleencorp.feen.model.info.JoinStatusInfo;
import com.fleencorp.feen.model.info.chat.space.member.ChatSpaceMemberRoleInfo;
import com.fleencorp.feen.model.info.chat.space.member.ChatSpaceRequestToJoinStatusInfo;
import com.fleencorp.feen.model.info.chat.space.membership.*;
import com.fleencorp.feen.model.response.chat.space.base.ChatSpaceResponse;
import com.fleencorp.feen.model.response.chat.space.member.base.ChatSpaceMemberResponse;

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
