package com.fleencorp.feen.service.chat.space.join;

import com.fleencorp.feen.model.dto.chat.member.JoinChatSpaceDto;
import com.fleencorp.feen.model.dto.chat.member.ProcessRequestToJoinChatSpaceDto;
import com.fleencorp.feen.model.dto.chat.member.RequestToJoinChatSpaceDto;
import com.fleencorp.feen.model.response.chat.space.member.LeaveChatSpaceResponse;
import com.fleencorp.feen.model.response.chat.space.membership.JoinChatSpaceResponse;
import com.fleencorp.feen.model.response.chat.space.membership.ProcessRequestToJoinChatSpaceResponse;
import com.fleencorp.feen.model.response.chat.space.membership.RequestToJoinChatSpaceResponse;
import com.fleencorp.feen.model.security.FleenUser;

public interface ChatSpaceJoinService {

  JoinChatSpaceResponse joinSpace(Long chatSpaceId, JoinChatSpaceDto joinChatSpaceDto, FleenUser user);

  RequestToJoinChatSpaceResponse requestToJoinSpace(Long chatSpaceId, RequestToJoinChatSpaceDto requestToJoinChatSpaceDto, FleenUser user);

  ProcessRequestToJoinChatSpaceResponse processRequestToJoinSpace(Long chatSpaceId, ProcessRequestToJoinChatSpaceDto processRequestToJoinChatSpaceDto, FleenUser user);

  LeaveChatSpaceResponse leaveChatSpace(Long chatSpaceId, FleenUser user);
}
