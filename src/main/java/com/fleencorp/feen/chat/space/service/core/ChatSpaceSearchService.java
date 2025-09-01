package com.fleencorp.feen.chat.space.service.core;

import com.fleencorp.feen.chat.space.exception.member.ChatSpaceMemberNotFoundException;
import com.fleencorp.feen.chat.space.model.request.core.ChatSpaceMemberSearchRequest;
import com.fleencorp.feen.chat.space.model.request.core.ChatSpaceSearchRequest;
import com.fleencorp.feen.chat.space.model.response.RetrieveChatSpaceResponse;
import com.fleencorp.feen.chat.space.model.search.core.ChatSpaceSearchResult;
import com.fleencorp.feen.chat.space.model.search.core.RemovedMemberSearchResult;
import com.fleencorp.feen.chat.space.model.search.core.RequestToJoinSearchResult;
import com.fleencorp.feen.chat.space.model.search.mutual.MutualChatSpaceMembershipSearchResult;
import com.fleencorp.feen.shared.security.RegisteredUser;

public interface ChatSpaceSearchService {

  ChatSpaceSearchResult findSpaces(ChatSpaceSearchRequest searchRequest, RegisteredUser user);

  ChatSpaceSearchResult findMySpaces(ChatSpaceSearchRequest searchRequest, RegisteredUser user);

  ChatSpaceSearchResult findSpacesIBelongTo(ChatSpaceSearchRequest searchRequest, RegisteredUser user);

  RequestToJoinSearchResult findRequestToJoinSpace(Long chatSpaceId, ChatSpaceMemberSearchRequest searchRequest, RegisteredUser user);

  MutualChatSpaceMembershipSearchResult findChatSpacesMembershipWithAnotherUser(ChatSpaceSearchRequest searchRequest, RegisteredUser user);

  RetrieveChatSpaceResponse retrieveChatSpace(Long chatSpaceId, RegisteredUser user) throws ChatSpaceMemberNotFoundException;

  RemovedMemberSearchResult findRemovedMembers(Long chatSpaceId, ChatSpaceMemberSearchRequest searchRequest, RegisteredUser user);

  Integer getTotalRequestToJoinForChatSpace(Long chatSpaceId);
}
