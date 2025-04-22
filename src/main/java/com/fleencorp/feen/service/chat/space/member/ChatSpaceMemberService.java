package com.fleencorp.feen.service.chat.space.member;

import com.fleencorp.feen.exception.base.FailedOperationException;
import com.fleencorp.feen.exception.chat.space.ChatSpaceNotFoundException;
import com.fleencorp.feen.exception.chat.space.core.NotAnAdminOfChatSpaceException;
import com.fleencorp.feen.exception.chat.space.member.ChatSpaceMemberNotFoundException;
import com.fleencorp.feen.exception.member.MemberNotFoundException;
import com.fleencorp.feen.model.domain.chat.ChatSpace;
import com.fleencorp.feen.model.domain.chat.ChatSpaceMember;
import com.fleencorp.feen.model.domain.stream.FleenStream;
import com.fleencorp.feen.model.domain.stream.StreamAttendee;
import com.fleencorp.feen.model.domain.user.Member;
import com.fleencorp.feen.model.dto.chat.member.AddChatSpaceMemberDto;
import com.fleencorp.feen.model.dto.chat.member.RemoveChatSpaceMemberDto;
import com.fleencorp.feen.model.dto.chat.member.RestoreChatSpaceMemberDto;
import com.fleencorp.feen.model.dto.chat.role.DowngradeChatSpaceAdminToMemberDto;
import com.fleencorp.feen.model.dto.chat.role.UpgradeChatSpaceMemberToAdminDto;
import com.fleencorp.feen.model.request.search.chat.space.ChatSpaceMemberSearchRequest;
import com.fleencorp.feen.model.response.chat.space.member.*;
import com.fleencorp.feen.model.search.chat.space.member.ChatSpaceMemberSearchResult;
import com.fleencorp.feen.model.security.FleenUser;

public interface ChatSpaceMemberService {

  ChatSpaceMemberSearchResult findChatSpaceMembers(Long chatSpaceId, ChatSpaceMemberSearchRequest searchRequest, FleenUser user);

  ChatSpaceMemberSearchResult findChatSpaceAdmins(Long chatSpaceId, ChatSpaceMemberSearchRequest searchRequest, FleenUser user);

  UpgradeChatSpaceMemberToAdminResponse upgradeChatSpaceMemberToAdmin(Long chatSpaceId, UpgradeChatSpaceMemberToAdminDto upgradeChatSpaceMemberToAdminDto, FleenUser user)
    throws ChatSpaceNotFoundException, ChatSpaceMemberNotFoundException, NotAnAdminOfChatSpaceException,
    FailedOperationException;

  DowngradeChatSpaceAdminToMemberResponse downgradeChatSpaceAdminToMember(Long chatSpaceId, DowngradeChatSpaceAdminToMemberDto downgradeChatSpaceAdminToMemberDto, FleenUser user)
    throws ChatSpaceNotFoundException, ChatSpaceMemberNotFoundException, NotAnAdminOfChatSpaceException,
      FailedOperationException;

  AddChatSpaceMemberResponse addMember(Long chatSpaceId, AddChatSpaceMemberDto addChatSpaceMemberDto, FleenUser user)
    throws ChatSpaceNotFoundException, MemberNotFoundException, NotAnAdminOfChatSpaceException,
      FailedOperationException;

  RestoreChatSpaceMemberResponse restoreRemovedMember(Long chatSpaceId, RestoreChatSpaceMemberDto restoreChatSpaceMemberDto, FleenUser user)
    throws ChatSpaceNotFoundException, ChatSpaceMemberNotFoundException, NotAnAdminOfChatSpaceException,
      FailedOperationException;

  RemoveChatSpaceMemberResponse removeMember(Long chatSpaceId, RemoveChatSpaceMemberDto removeChatSpaceMemberDto, FleenUser user)
    throws ChatSpaceNotFoundException, NotAnAdminOfChatSpaceException, ChatSpaceMemberNotFoundException,
      FailedOperationException;

  void leaveChatSpace(ChatSpace chatSpace, ChatSpaceMember chatSpaceMember) throws ChatSpaceMemberNotFoundException, FailedOperationException;

  ChatSpaceMember findByChatSpaceAndChatSpaceMemberId(ChatSpace chatSpace, Long chatSpaceMemberId) throws ChatSpaceMemberNotFoundException;

  ChatSpaceMember getExistingOrCreateNewChatSpaceMember(ChatSpace chatSpace, FleenUser user) throws FailedOperationException;

  ChatSpaceMember findByChatSpaceAndMember(ChatSpace chatSpace, Member member) throws ChatSpaceMemberNotFoundException;

  void addMemberToChatSpaceExternally(ChatSpaceMember chatSpaceMember, ChatSpace chatSpace, Member member);

  boolean checkIfStreamHasChatSpaceAndAttendeeIsAMemberOfChatSpace(FleenStream stream, StreamAttendee streamAttendee);
}
