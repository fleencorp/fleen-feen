package com.fleencorp.feen.mapper.chat.member;

import com.fleencorp.feen.constant.chat.space.member.ChatSpaceMemberRole;
import com.fleencorp.feen.model.domain.chat.ChatSpace;
import com.fleencorp.feen.model.domain.chat.ChatSpaceMember;
import com.fleencorp.feen.model.info.chat.space.member.ChatSpaceMemberRoleInfo;
import com.fleencorp.feen.model.response.chat.space.member.base.ChatSpaceMemberResponse;

import java.util.List;

public interface ChatSpaceMemberMapper {

  ChatSpaceMemberResponse toChatSpaceMemberResponse(ChatSpaceMember entry, ChatSpace chatSpace);

  List<ChatSpaceMemberResponse> toChatSpaceMemberResponses(List<ChatSpaceMember> entries, ChatSpace chatSpace);

  ChatSpaceMemberRoleInfo toRole(ChatSpaceMemberRole chatSpaceMemberRole);
}
