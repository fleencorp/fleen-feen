package com.fleencorp.feen.service.chat.space.join;

import com.fleencorp.feen.exception.base.FailedOperationException;
import com.fleencorp.feen.exception.chat.space.ChatSpaceNotFoundException;
import com.fleencorp.feen.exception.chat.space.core.ChatSpaceAlreadyDeletedException;
import com.fleencorp.feen.exception.chat.space.core.ChatSpaceNotActiveException;
import com.fleencorp.feen.exception.chat.space.join.request.AlreadyJoinedChatSpaceException;
import com.fleencorp.feen.exception.chat.space.join.request.CannotJoinPrivateChatSpaceWithoutApprovalException;
import com.fleencorp.feen.exception.chat.space.member.ChatSpaceMemberNotFoundException;
import com.fleencorp.feen.exception.chat.space.member.ChatSpaceMemberRemovedException;
import com.fleencorp.feen.user.exception.MemberNotFoundException;
import com.fleencorp.feen.model.dto.chat.member.JoinChatSpaceDto;
import com.fleencorp.feen.model.dto.chat.member.ProcessRequestToJoinChatSpaceDto;
import com.fleencorp.feen.model.dto.chat.member.RequestToJoinChatSpaceDto;
import com.fleencorp.feen.model.response.chat.space.member.LeaveChatSpaceResponse;
import com.fleencorp.feen.model.response.chat.space.membership.JoinChatSpaceResponse;
import com.fleencorp.feen.model.response.chat.space.membership.ProcessRequestToJoinChatSpaceResponse;
import com.fleencorp.feen.model.response.chat.space.membership.RequestToJoinChatSpaceResponse;
import com.fleencorp.feen.user.security.RegisteredUser;

public interface ChatSpaceJoinService {

  JoinChatSpaceResponse joinSpace(Long chatSpaceId, JoinChatSpaceDto joinChatSpaceDto, RegisteredUser user)
    throws ChatSpaceNotFoundException, ChatSpaceNotActiveException, CannotJoinPrivateChatSpaceWithoutApprovalException,
    FailedOperationException;

  RequestToJoinChatSpaceResponse requestToJoinSpace(Long chatSpaceId, RequestToJoinChatSpaceDto requestToJoinChatSpaceDto, RegisteredUser user)
    throws ChatSpaceNotFoundException, ChatSpaceNotActiveException, ChatSpaceMemberRemovedException,
      FailedOperationException;

  ProcessRequestToJoinChatSpaceResponse processRequestToJoinSpace(Long chatSpaceId, ProcessRequestToJoinChatSpaceDto processRequestToJoinChatSpaceDto, RegisteredUser user)
      throws ChatSpaceNotFoundException, ChatSpaceAlreadyDeletedException, MemberNotFoundException,
    ChatSpaceMemberNotFoundException, AlreadyJoinedChatSpaceException, FailedOperationException;

  LeaveChatSpaceResponse leaveChatSpace(Long chatSpaceId, RegisteredUser user) throws ChatSpaceMemberNotFoundException, FailedOperationException;
}
