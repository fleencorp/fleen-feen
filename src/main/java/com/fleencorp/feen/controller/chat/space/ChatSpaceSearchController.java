package com.fleencorp.feen.controller.chat.space;

import com.fleencorp.base.resolver.SearchParam;
import com.fleencorp.feen.model.request.search.chat.space.ChatSpaceMemberSearchRequest;
import com.fleencorp.feen.model.request.search.chat.space.ChatSpaceSearchRequest;
import com.fleencorp.feen.model.response.chat.space.RetrieveChatSpaceResponse;
import com.fleencorp.feen.model.search.chat.space.ChatSpaceSearchResult;
import com.fleencorp.feen.model.search.chat.space.event.ChatSpaceEventSearchResult;
import com.fleencorp.feen.model.search.chat.space.member.ChatSpaceMemberSearchResult;
import com.fleencorp.feen.model.security.FleenUser;
import com.fleencorp.feen.service.chat.space.ChatSpaceSearchService;
import com.fleencorp.feen.service.chat.space.event.ChatSpaceEventService;
import com.fleencorp.feen.service.chat.space.member.ChatSpaceMemberService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/chat-space")
public class ChatSpaceSearchController {

  private final ChatSpaceEventService chatSpaceEventService;
  private final ChatSpaceMemberService chatSpaceMemberService;
  private final ChatSpaceSearchService chatSpaceSearchService;

  public ChatSpaceSearchController(
      final ChatSpaceEventService chatSpaceEventService,
      final ChatSpaceMemberService chatSpaceMemberService,
      final ChatSpaceSearchService chatSpaceSearchService) {
    this.chatSpaceEventService = chatSpaceEventService;
    this.chatSpaceMemberService = chatSpaceMemberService;
    this.chatSpaceSearchService = chatSpaceSearchService;
  }

  @GetMapping(value = "/entries")
  public ChatSpaceSearchResult findSpaces(
      @SearchParam final ChatSpaceSearchRequest chatSpaceSearchRequest,
      @AuthenticationPrincipal final FleenUser user) {
    return chatSpaceSearchService.findSpaces(chatSpaceSearchRequest, user);
  }

  @GetMapping(value = "/find-events/{chatSpaceId}")
  public ChatSpaceEventSearchResult findChatSpaceEvents(
      @PathVariable(name = "chatSpaceId") final Long chatSpaceId,
      @SearchParam final ChatSpaceSearchRequest chatSpaceSearchRequest,
      @AuthenticationPrincipal final FleenUser user) {
    return chatSpaceEventService.findChatSpaceEvents(chatSpaceId, chatSpaceSearchRequest, user);
  }

  @GetMapping(value = "/detail/{chatSpaceId}")
  public RetrieveChatSpaceResponse detail(
      @PathVariable(name = "chatSpaceId") final Long chatSpaceId,
      @AuthenticationPrincipal final FleenUser user) {
    return chatSpaceSearchService.retrieveChatSpace(chatSpaceId, user);
  }

  @GetMapping(value = "/find-members/{chatSpaceId}")
  public ChatSpaceMemberSearchResult findSpaceMembers(
      @PathVariable(name = "chatSpaceId") final Long chatSpaceId,
      @SearchParam final ChatSpaceMemberSearchRequest chatSpaceMemberSearchRequest,
      @AuthenticationPrincipal final FleenUser user) {
    return chatSpaceMemberService.findChatSpaceMembers(chatSpaceId, chatSpaceMemberSearchRequest, user);
  }

  @GetMapping(value = "/belong-to")
  public ChatSpaceSearchResult findSpaceIBelong(
      final ChatSpaceSearchRequest createdSpaceSearchRequest,
      @AuthenticationPrincipal final FleenUser user) {
    return chatSpaceSearchService.findSpacesIBelongTo(createdSpaceSearchRequest, user);
  }

  @GetMapping(value = "/created")
  public ChatSpaceSearchResult findSpaceCreated(
      @SearchParam final ChatSpaceSearchRequest chatSpaceSearchRequest,
      @AuthenticationPrincipal final FleenUser user) {
    return chatSpaceSearchService.findSpacesCreated(chatSpaceSearchRequest, user);
  }
}
