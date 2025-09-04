package com.fleencorp.feen.chat.space.service.member;

import com.fleencorp.feen.chat.space.exception.core.ChatSpaceNotAnAdminException;
import com.fleencorp.feen.chat.space.exception.core.ChatSpaceNotFoundException;
import com.fleencorp.feen.chat.space.exception.member.ChatSpaceMemberNotFoundException;
import com.fleencorp.feen.chat.space.model.domain.ChatSpace;
import com.fleencorp.feen.chat.space.model.domain.ChatSpaceMember;
import com.fleencorp.feen.chat.space.model.dto.member.AddChatSpaceMemberDto;
import com.fleencorp.feen.chat.space.model.dto.member.RemoveChatSpaceMemberDto;
import com.fleencorp.feen.chat.space.model.dto.member.RestoreChatSpaceMemberDto;
import com.fleencorp.feen.chat.space.model.dto.role.DowngradeChatSpaceAdminToMemberDto;
import com.fleencorp.feen.chat.space.model.dto.role.UpgradeChatSpaceMemberToAdminDto;
import com.fleencorp.feen.chat.space.model.request.core.ChatSpaceMemberSearchRequest;
import com.fleencorp.feen.chat.space.model.response.member.*;
import com.fleencorp.feen.chat.space.model.search.member.ChatSpaceMemberSearchResult;
import com.fleencorp.feen.common.exception.FailedOperationException;
import com.fleencorp.feen.shared.security.RegisteredUser;
import com.fleencorp.feen.user.exception.member.MemberNotFoundException;
import com.fleencorp.feen.user.model.domain.Member;

public interface ChatSpaceMemberService {

  ChatSpaceMemberSearchResult findChatSpaceMembers(Long chatSpaceId, ChatSpaceMemberSearchRequest searchRequest, RegisteredUser user);

  ChatSpaceMemberSearchResult findChatSpaceAdmins(Long chatSpaceId, ChatSpaceMemberSearchRequest searchRequest, RegisteredUser user);

  UpgradeChatSpaceMemberToAdminResponse upgradeChatSpaceMemberToAdmin(Long chatSpaceId, UpgradeChatSpaceMemberToAdminDto upgradeChatSpaceMemberToAdminDto, RegisteredUser user)
    throws ChatSpaceNotFoundException, ChatSpaceMemberNotFoundException, ChatSpaceNotAnAdminException,
    FailedOperationException;

  DowngradeChatSpaceAdminToMemberResponse downgradeChatSpaceAdminToMember(Long chatSpaceId, DowngradeChatSpaceAdminToMemberDto downgradeChatSpaceAdminToMemberDto, RegisteredUser user)
    throws ChatSpaceNotFoundException, ChatSpaceMemberNotFoundException, ChatSpaceNotAnAdminException,
      FailedOperationException;

  AddChatSpaceMemberResponse addMember(Long chatSpaceId, AddChatSpaceMemberDto addChatSpaceMemberDto, RegisteredUser user)
    throws ChatSpaceNotFoundException, MemberNotFoundException, ChatSpaceNotAnAdminException,
      FailedOperationException;

  RestoreChatSpaceMemberResponse restoreRemovedMember(Long chatSpaceId, RestoreChatSpaceMemberDto restoreChatSpaceMemberDto, RegisteredUser user)
    throws ChatSpaceNotFoundException, ChatSpaceMemberNotFoundException, ChatSpaceNotAnAdminException,
      FailedOperationException;

  RemoveChatSpaceMemberResponse removeMember(Long chatSpaceId, RemoveChatSpaceMemberDto removeChatSpaceMemberDto, RegisteredUser user)
    throws ChatSpaceNotFoundException, ChatSpaceNotAnAdminException, ChatSpaceMemberNotFoundException,
      FailedOperationException;

  void leaveChatSpace(ChatSpace chatSpace, ChatSpaceMember chatSpaceMember) throws ChatSpaceMemberNotFoundException, FailedOperationException;

  ChatSpaceMember findByChatSpaceAndChatSpaceMemberId(ChatSpace chatSpace, Long chatSpaceMemberId) throws ChatSpaceMemberNotFoundException;

  ChatSpaceMember getExistingOrCreateNewChatSpaceMember(ChatSpace chatSpace, RegisteredUser user) throws FailedOperationException;

  ChatSpaceMember findByChatSpaceAndMember(ChatSpace chatSpace, Member member) throws ChatSpaceMemberNotFoundException;

  void addMemberToChatSpaceExternally(ChatSpaceMember chatSpaceMember, ChatSpace chatSpace, Member member);
}
