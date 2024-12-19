package com.fleencorp.feen.service.chat.space.member;

import com.fleencorp.feen.model.domain.chat.ChatSpace;
import com.fleencorp.feen.model.domain.chat.ChatSpaceMember;
import com.fleencorp.feen.model.domain.stream.FleenStream;
import com.fleencorp.feen.model.domain.stream.StreamAttendee;
import com.fleencorp.feen.model.dto.chat.DowngradeChatSpaceAdminToMemberDto;
import com.fleencorp.feen.model.dto.chat.UpgradeChatSpaceMemberToAdminDto;
import com.fleencorp.feen.model.dto.chat.member.AddChatSpaceMemberDto;
import com.fleencorp.feen.model.dto.chat.member.RemoveChatSpaceMemberDto;
import com.fleencorp.feen.model.request.search.chat.space.ChatSpaceMemberSearchRequest;
import com.fleencorp.feen.model.response.chat.space.member.AddChatSpaceMemberResponse;
import com.fleencorp.feen.model.response.chat.space.member.DowngradeChatSpaceAdminToMemberResponse;
import com.fleencorp.feen.model.response.chat.space.member.RemoveChatSpaceMemberResponse;
import com.fleencorp.feen.model.response.chat.space.member.UpgradeChatSpaceMemberToAdminResponse;
import com.fleencorp.feen.model.search.chat.space.member.ChatSpaceMemberSearchResult;
import com.fleencorp.feen.model.security.FleenUser;

public interface ChatSpaceMemberService {

  ChatSpaceMemberSearchResult findChatSpaceMembers(Long chatSpaceId, ChatSpaceMemberSearchRequest searchRequest, FleenUser user);

  UpgradeChatSpaceMemberToAdminResponse upgradeChatSpaceMemberToAdmin(Long chatSpaceId, UpgradeChatSpaceMemberToAdminDto upgradeChatSpaceMemberToAdminDto, FleenUser user);

  DowngradeChatSpaceAdminToMemberResponse downgradeChatSpaceAdminToMember(Long chatSpaceId, DowngradeChatSpaceAdminToMemberDto downgradeChatSpaceAdminToMemberDto, FleenUser user);

  AddChatSpaceMemberResponse addMember(Long chatSpaceId, AddChatSpaceMemberDto addChatSpaceMemberDto, FleenUser user);

  RemoveChatSpaceMemberResponse removeMember(Long chatSpaceId, RemoveChatSpaceMemberDto removeChatSpaceMemberDto, FleenUser user);

  ChatSpaceMember leaveChatSpaceOrRemoveChatSpaceMember(ChatSpace chatSpace, Long memberId);

  boolean checkIfStreamHasChatSpaceAndAttendeeIsAMemberOfChatSpace(FleenStream stream, StreamAttendee streamAttendee);
}
