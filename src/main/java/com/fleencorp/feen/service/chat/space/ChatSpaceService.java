package com.fleencorp.feen.service.chat.space;

import com.fleencorp.base.model.request.search.SearchRequest;
import com.fleencorp.feen.model.dto.chat.CreateChatSpaceDto;
import com.fleencorp.feen.model.dto.chat.DowngradeChatSpaceAdminToMemberDto;
import com.fleencorp.feen.model.dto.chat.UpdateChatSpaceDto;
import com.fleencorp.feen.model.dto.chat.UpgradeChatSpaceMemberToAdminDto;
import com.fleencorp.feen.model.dto.chat.member.*;
import com.fleencorp.feen.model.dto.event.CreateChatSpaceEventDto;
import com.fleencorp.feen.model.request.search.chat.space.ChatSpaceMemberSearchRequest;
import com.fleencorp.feen.model.request.search.chat.space.ChatSpaceSearchRequest;
import com.fleencorp.feen.model.response.chat.space.*;
import com.fleencorp.feen.model.response.chat.space.member.*;
import com.fleencorp.feen.model.response.event.CreateEventResponse;
import com.fleencorp.feen.model.search.broadcast.request.RequestToJoinSearchResult;
import com.fleencorp.feen.model.search.chat.space.ChatSpaceSearchResult;
import com.fleencorp.feen.model.search.chat.space.event.ChatSpaceEventSearchResult;
import com.fleencorp.feen.model.search.chat.space.member.ChatSpaceMemberSearchResult;
import com.fleencorp.feen.model.security.FleenUser;

public interface ChatSpaceService {

  ChatSpaceSearchResult findSpaces(ChatSpaceSearchRequest searchRequest, FleenUser user);

  ChatSpaceSearchResult findSpacesCreated(ChatSpaceSearchRequest searchRequest, FleenUser user);

  ChatSpaceSearchResult findSpacesIBelongTo(ChatSpaceSearchRequest searchRequest, FleenUser user);

  ChatSpaceMemberSearchResult findChatSpaceMembers(Long chatSpaceId, ChatSpaceMemberSearchRequest searchRequest, FleenUser user);

  RequestToJoinSearchResult findRequestToJoinSpace(Long chatSpaceId, ChatSpaceMemberSearchRequest searchRequest, FleenUser user);

  ChatSpaceEventSearchResult findChatSpaceEvents(Long chatSpaceId, SearchRequest searchRequest, FleenUser user);

  CreateChatSpaceResponse createChatSpace(CreateChatSpaceDto createChatSpaceDto, FleenUser user);

  CreateEventResponse createChatSpaceEvent(Long chatSpaceId, CreateChatSpaceEventDto createChatSpaceEventDto, FleenUser user);

  UpdateChatSpaceResponse updateChatSpace(Long chatSpaceId, UpdateChatSpaceDto updateChatSpaceDto, FleenUser user);

  RetrieveChatSpaceResponse retrieveChatSpace(Long chatSpaceId, FleenUser user);

  DeleteChatSpaceResponse deleteChatSpace(Long chatSpaceId, FleenUser user);

  DeleteChatSpaceResponse deleteChatSpaceByAdmin(Long chatSpaceId, FleenUser user);

  EnableChatSpaceResponse enableChatSpace(Long chatSpaceId, FleenUser user);

  DisableChatSpaceResponse disableChatSpace(Long chatSpaceId, FleenUser user);

  UpgradeChatSpaceMemberToAdminResponse upgradeChatSpaceMemberToAdmin(Long chatSpaceId, UpgradeChatSpaceMemberToAdminDto upgradeChatSpaceMemberToAdminDto, FleenUser user);

  DowngradeChatSpaceAdminToMemberResponse downgradeChatSpaceAdminToMember(Long chatSpaceId, DowngradeChatSpaceAdminToMemberDto downgradeChatSpaceAdminToMemberDto, FleenUser user);

  JoinChatSpaceResponse joinSpace(Long chatSpaceId, JoinChatSpaceDto joinChatSpaceDto, FleenUser user);

  RequestToJoinChatSpaceResponse requestToJoinSpace(Long chatSpaceId, RequestToJoinChatSpaceDto requestToJoinChatSpaceDto, FleenUser user);

  ProcessRequestToJoinChatSpaceResponse processRequestToJoinSpace(Long chatSpaceId, ProcessRequestToJoinChatSpaceDto processRequestToJoinChatSpaceDto, FleenUser user);

  AddChatSpaceMemberResponse addMember(Long chatSpaceId, AddChatSpaceMemberDto addChatSpaceMemberDto, FleenUser user);

  RemoveChatSpaceMemberResponse removeMember(Long chatSpaceId, RemoveChatSpaceMemberDto removeChatSpaceMemberDto, FleenUser user);

  LeaveChatSpaceResponse leaveChatSpace(Long chatSpaceId, FleenUser user);
}
