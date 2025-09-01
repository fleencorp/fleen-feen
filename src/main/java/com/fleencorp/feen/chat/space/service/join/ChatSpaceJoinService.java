package com.fleencorp.feen.chat.space.service.join;

import com.fleencorp.feen.chat.space.exception.core.ChatSpaceAlreadyDeletedException;
import com.fleencorp.feen.chat.space.exception.core.ChatSpaceNotActiveException;
import com.fleencorp.feen.chat.space.exception.core.ChatSpaceNotFoundException;
import com.fleencorp.feen.chat.space.exception.member.ChatSpaceMemberNotFoundException;
import com.fleencorp.feen.chat.space.exception.member.ChatSpaceMemberRemovedException;
import com.fleencorp.feen.chat.space.exception.request.AlreadyJoinedChatSpaceException;
import com.fleencorp.feen.chat.space.exception.request.CannotJoinPrivateChatSpaceWithoutApprovalException;
import com.fleencorp.feen.chat.space.model.dto.join.request.JoinChatSpaceDto;
import com.fleencorp.feen.chat.space.model.dto.join.request.ProcessRequestToJoinChatSpaceDto;
import com.fleencorp.feen.chat.space.model.dto.join.request.RequestToJoinChatSpaceDto;
import com.fleencorp.feen.chat.space.model.response.member.LeaveChatSpaceResponse;
import com.fleencorp.feen.chat.space.model.response.membership.JoinChatSpaceResponse;
import com.fleencorp.feen.chat.space.model.response.membership.ProcessRequestToJoinChatSpaceResponse;
import com.fleencorp.feen.chat.space.model.response.membership.RequestToJoinChatSpaceResponse;
import com.fleencorp.feen.common.exception.FailedOperationException;
import com.fleencorp.feen.user.exception.member.MemberNotFoundException;
import com.fleencorp.feen.shared.security.RegisteredUser;

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

  LeaveChatSpaceResponse leaveChatSpace(Long chatSpaceId, RegisteredUser user)
    throws ChatSpaceMemberNotFoundException, FailedOperationException;
}
