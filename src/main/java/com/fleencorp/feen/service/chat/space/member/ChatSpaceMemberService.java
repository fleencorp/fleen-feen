package com.fleencorp.feen.service.chat.space.member;

import com.fleencorp.feen.exception.base.FailedOperationException;
import com.fleencorp.feen.exception.chat.space.ChatSpaceNotFoundException;
import com.fleencorp.feen.exception.chat.space.core.NotAnAdminOfChatSpaceException;
import com.fleencorp.feen.exception.chat.space.member.ChatSpaceMemberNotFoundException;
import com.fleencorp.feen.model.domain.chat.ChatSpace;
import com.fleencorp.feen.model.domain.chat.ChatSpaceMember;
import com.fleencorp.feen.model.domain.stream.FleenStream;
import com.fleencorp.feen.model.domain.stream.StreamAttendee;
import com.fleencorp.feen.model.dto.chat.member.AddChatSpaceMemberDto;
import com.fleencorp.feen.model.dto.chat.member.RemoveChatSpaceMemberDto;
import com.fleencorp.feen.model.dto.chat.member.RestoreChatSpaceMemberDto;
import com.fleencorp.feen.model.dto.chat.role.DowngradeChatSpaceAdminToMemberDto;
import com.fleencorp.feen.model.dto.chat.role.UpgradeChatSpaceMemberToAdminDto;
import com.fleencorp.feen.model.request.search.chat.space.ChatSpaceMemberSearchRequest;
import com.fleencorp.feen.model.response.chat.space.member.*;
import com.fleencorp.feen.model.search.chat.space.member.ChatSpaceMemberSearchResult;
import com.fleencorp.feen.user.exception.member.MemberNotFoundException;
import com.fleencorp.feen.user.model.domain.Member;
import com.fleencorp.feen.user.model.security.RegisteredUser;

public interface ChatSpaceMemberService {

  ChatSpaceMemberSearchResult findChatSpaceMembers(Long chatSpaceId, ChatSpaceMemberSearchRequest searchRequest, RegisteredUser user);

  ChatSpaceMemberSearchResult findChatSpaceAdmins(Long chatSpaceId, ChatSpaceMemberSearchRequest searchRequest, RegisteredUser user);

  UpgradeChatSpaceMemberToAdminResponse upgradeChatSpaceMemberToAdmin(Long chatSpaceId, UpgradeChatSpaceMemberToAdminDto upgradeChatSpaceMemberToAdminDto, RegisteredUser user)
    throws ChatSpaceNotFoundException, ChatSpaceMemberNotFoundException, NotAnAdminOfChatSpaceException,
    FailedOperationException;

  DowngradeChatSpaceAdminToMemberResponse downgradeChatSpaceAdminToMember(Long chatSpaceId, DowngradeChatSpaceAdminToMemberDto downgradeChatSpaceAdminToMemberDto, RegisteredUser user)
    throws ChatSpaceNotFoundException, ChatSpaceMemberNotFoundException, NotAnAdminOfChatSpaceException,
      FailedOperationException;

  AddChatSpaceMemberResponse addMember(Long chatSpaceId, AddChatSpaceMemberDto addChatSpaceMemberDto, RegisteredUser user)
    throws ChatSpaceNotFoundException, MemberNotFoundException, NotAnAdminOfChatSpaceException,
      FailedOperationException;

  RestoreChatSpaceMemberResponse restoreRemovedMember(Long chatSpaceId, RestoreChatSpaceMemberDto restoreChatSpaceMemberDto, RegisteredUser user)
    throws ChatSpaceNotFoundException, ChatSpaceMemberNotFoundException, NotAnAdminOfChatSpaceException,
      FailedOperationException;

  RemoveChatSpaceMemberResponse removeMember(Long chatSpaceId, RemoveChatSpaceMemberDto removeChatSpaceMemberDto, RegisteredUser user)
    throws ChatSpaceNotFoundException, NotAnAdminOfChatSpaceException, ChatSpaceMemberNotFoundException,
      FailedOperationException;

  void leaveChatSpace(ChatSpace chatSpace, ChatSpaceMember chatSpaceMember) throws ChatSpaceMemberNotFoundException, FailedOperationException;

  ChatSpaceMember findByChatSpaceAndChatSpaceMemberId(ChatSpace chatSpace, Long chatSpaceMemberId) throws ChatSpaceMemberNotFoundException;

  ChatSpaceMember getExistingOrCreateNewChatSpaceMember(ChatSpace chatSpace, RegisteredUser user) throws FailedOperationException;

  ChatSpaceMember findByChatSpaceAndMember(ChatSpace chatSpace, Member member) throws ChatSpaceMemberNotFoundException;

  void addMemberToChatSpaceExternally(ChatSpaceMember chatSpaceMember, ChatSpace chatSpace, Member member);

  boolean checkIfStreamHasChatSpaceAndAttendeeIsAMemberOfChatSpace(FleenStream stream, StreamAttendee streamAttendee);
}
