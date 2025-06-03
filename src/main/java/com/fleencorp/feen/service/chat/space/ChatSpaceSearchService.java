package com.fleencorp.feen.service.chat.space;

import com.fleencorp.feen.exception.chat.space.member.ChatSpaceMemberNotFoundException;
import com.fleencorp.feen.model.request.search.chat.space.ChatSpaceMemberSearchRequest;
import com.fleencorp.feen.model.request.search.chat.space.ChatSpaceSearchRequest;
import com.fleencorp.feen.model.response.chat.space.RetrieveChatSpaceResponse;
import com.fleencorp.feen.model.search.chat.space.ChatSpaceSearchResult;
import com.fleencorp.feen.model.search.join.RemovedMemberSearchResult;
import com.fleencorp.feen.model.search.join.RequestToJoinSearchResult;
import com.fleencorp.feen.model.security.FleenUser;

public interface ChatSpaceSearchService {

  ChatSpaceSearchResult findSpaces(ChatSpaceSearchRequest searchRequest, FleenUser user);

  ChatSpaceSearchResult findMySpaces(ChatSpaceSearchRequest searchRequest, FleenUser user);

  ChatSpaceSearchResult findSpacesIBelongTo(ChatSpaceSearchRequest searchRequest, FleenUser user);

  RequestToJoinSearchResult findRequestToJoinSpace(Long chatSpaceId, ChatSpaceMemberSearchRequest searchRequest, FleenUser user);

  ChatSpaceSearchResult findChatSpacesMembershipWithAnotherUser(ChatSpaceSearchRequest searchRequest, FleenUser user);

  RetrieveChatSpaceResponse retrieveChatSpace(Long chatSpaceId, FleenUser user) throws ChatSpaceMemberNotFoundException;

  RemovedMemberSearchResult findRemovedMembers(Long chatSpaceId, ChatSpaceMemberSearchRequest searchRequest, FleenUser user);

  Long getTotalRequestToJoinForChatSpace(Long chatSpaceId);
}
