package com.fleencorp.feen.service.chat.space;

import com.fleencorp.feen.exception.chat.space.member.ChatSpaceMemberNotFoundException;
import com.fleencorp.feen.model.request.search.chat.space.ChatSpaceMemberSearchRequest;
import com.fleencorp.feen.model.request.search.chat.space.ChatSpaceSearchRequest;
import com.fleencorp.feen.model.response.chat.space.RetrieveChatSpaceResponse;
import com.fleencorp.feen.model.search.chat.space.ChatSpaceSearchResult;
import com.fleencorp.feen.model.search.chat.space.mutual.MutualChatSpaceMembershipSearchResult;
import com.fleencorp.feen.model.search.join.RemovedMemberSearchResult;
import com.fleencorp.feen.model.search.join.RequestToJoinSearchResult;
import com.fleencorp.feen.user.security.RegisteredUser;

public interface ChatSpaceSearchService {

  ChatSpaceSearchResult findSpaces(ChatSpaceSearchRequest searchRequest, RegisteredUser user);

  ChatSpaceSearchResult findMySpaces(ChatSpaceSearchRequest searchRequest, RegisteredUser user);

  ChatSpaceSearchResult findSpacesIBelongTo(ChatSpaceSearchRequest searchRequest, RegisteredUser user);

  RequestToJoinSearchResult findRequestToJoinSpace(Long chatSpaceId, ChatSpaceMemberSearchRequest searchRequest, RegisteredUser user);

  MutualChatSpaceMembershipSearchResult findChatSpacesMembershipWithAnotherUser(ChatSpaceSearchRequest searchRequest, RegisteredUser user);

  RetrieveChatSpaceResponse retrieveChatSpace(Long chatSpaceId, RegisteredUser user) throws ChatSpaceMemberNotFoundException;

  RemovedMemberSearchResult findRemovedMembers(Long chatSpaceId, ChatSpaceMemberSearchRequest searchRequest, RegisteredUser user);

  Long getTotalRequestToJoinForChatSpace(Long chatSpaceId);
}
