package com.fleencorp.feen.controller.chat.space;

import com.fleencorp.base.resolver.SearchParam;
import com.fleencorp.feen.model.dto.chat.CreateChatSpaceDto;
import com.fleencorp.feen.model.dto.chat.UpdateChatSpaceDto;
import com.fleencorp.feen.model.dto.chat.member.JoinChatSpaceDto;
import com.fleencorp.feen.model.dto.chat.member.RequestToJoinChatSpaceDto;
import com.fleencorp.feen.model.dto.event.CreateChatSpaceEventDto;
import com.fleencorp.feen.model.request.search.chat.space.ChatSpaceSearchRequest;
import com.fleencorp.feen.model.response.chat.space.*;
import com.fleencorp.feen.model.response.chat.space.member.LeaveChatSpaceResponse;
import com.fleencorp.feen.model.response.event.CreateEventResponse;
import com.fleencorp.feen.model.search.chat.space.ChatSpaceSearchResult;
import com.fleencorp.feen.model.search.chat.space.event.ChatSpaceEventSearchResult;
import com.fleencorp.feen.model.security.FleenUser;
import com.fleencorp.feen.service.chat.space.ChatSpaceService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/chat-space")
public class ChatSpaceController {

  private final ChatSpaceService chatSpaceService;

  public ChatSpaceController(final ChatSpaceService chatSpaceService) {
    this.chatSpaceService = chatSpaceService;
  }

  @GetMapping(value = "/find-spaces")
  public ChatSpaceSearchResult findSpaces(
      @SearchParam final ChatSpaceSearchRequest chatSpaceSearchRequest,
      @AuthenticationPrincipal final FleenUser user) {
    return chatSpaceService.findSpaces(chatSpaceSearchRequest, user);
  }

  @GetMapping(value = "/find-events/{chatSpaceId}")
  public ChatSpaceEventSearchResult findChatSpaceEvents(
      @PathVariable(name = "chatSpaceId") final Long chatSpaceId,
      @SearchParam final ChatSpaceSearchRequest chatSpaceSearchRequest,
      @AuthenticationPrincipal final FleenUser user) {
    return chatSpaceService.findChatSpaceEvents(chatSpaceId, chatSpaceSearchRequest, user);
  }

  @PostMapping(value = "/create")
  public CreateChatSpaceResponse create(
      @Valid @RequestBody final CreateChatSpaceDto createChatSpaceDto,
      @AuthenticationPrincipal final FleenUser user) {
    return chatSpaceService.createChatSpace(createChatSpaceDto, user);
  }

  @PostMapping(value = "/create-event/{chatSpaceId}")
  public CreateEventResponse createEvent(
      @PathVariable(value = "chatSpaceId") final Long chatSpaceId,
      @Valid @RequestBody final CreateChatSpaceEventDto createChatSpaceEventDto,
      @AuthenticationPrincipal final FleenUser user) {
    return chatSpaceService.createChatSpaceEvent(chatSpaceId, createChatSpaceEventDto, user);
  }

  @GetMapping(value = "/detail/{chatSpaceId}")
  public RetrieveChatSpaceResponse detail(
      @PathVariable(name = "chatSpaceId") final Long chatSpaceId,
      @AuthenticationPrincipal final FleenUser user) {
    return chatSpaceService.retrieveChatSpace(chatSpaceId, user);
  }

  @PutMapping(value = "/update/{chatSpaceId}")
  public UpdateChatSpaceResponse update(
      @PathVariable(name = "chatSpaceId") final Long chatSpaceId,
      @Valid @RequestBody final UpdateChatSpaceDto updateChatSpaceDto,
      @AuthenticationPrincipal final FleenUser user) {
    return chatSpaceService.updateChatSpace(chatSpaceId, updateChatSpaceDto, user);
  }

  @DeleteMapping(value = "/delete/{chatSpaceId}")
  public DeleteChatSpaceResponse delete(
      @PathVariable(name = "chatSpaceId") final Long chatSpaceId,
      @AuthenticationPrincipal final FleenUser user) {
    return chatSpaceService.deleteChatSpace(chatSpaceId, user);
  }

  @DeleteMapping(value = "/admin/delete/{chatSpaceId}")
  public DeleteChatSpaceResponse deleteByAdmin(
      @PathVariable(name = "chatSpaceId") final Long chatSpaceId,
      @AuthenticationPrincipal final FleenUser user) {
    return chatSpaceService.deleteChatSpaceByAdmin(chatSpaceId, user);
  }

  @PutMapping(value = "/enable/{chatSpaceId}")
  public EnableChatSpaceResponse enable(
      @PathVariable(name = "chatSpaceId") final Long chatSpaceId,
      @AuthenticationPrincipal final FleenUser user) {
    return chatSpaceService.enableChatSpace(chatSpaceId, user);
  }

  @PutMapping(value = "/disable/{chatSpaceId}")
  public DisableChatSpaceResponse disable(
      @PathVariable(name = "chatSpaceId") final Long chatSpaceId,
      @AuthenticationPrincipal final FleenUser user) {
    return chatSpaceService.disableChatSpace(chatSpaceId, user);
  }

  @PostMapping(value = "/join/{chatSpaceId}")
  public JoinChatSpaceResponse join(
      @PathVariable(name = "chatSpaceId") final Long chatSpaceId,
      @Valid @RequestBody final JoinChatSpaceDto joinChatSpaceDto,
      @AuthenticationPrincipal final FleenUser user) {
    return chatSpaceService.joinSpace(chatSpaceId, joinChatSpaceDto, user);
  }

  @PostMapping(value = "/request-to-join/{chatSpaceId}")
  public RequestToJoinChatSpaceResponse requestToJoin(
      @PathVariable(name = "chatSpaceId") final Long chatSpaceId,
      @Valid @RequestBody final RequestToJoinChatSpaceDto requestToJoinChatSpaceDto,
      @AuthenticationPrincipal final FleenUser user) {
    return chatSpaceService.requestToJoinSpace(chatSpaceId, requestToJoinChatSpaceDto, user);
  }

  @PostMapping(value = "/leave/{chatSpaceId}")
  public LeaveChatSpaceResponse leave(
      @PathVariable(name = "chatSpaceId") final Long chatSpaceId,
      @AuthenticationPrincipal final FleenUser user) {
    return chatSpaceService.leaveChatSpace(chatSpaceId, user);
  }

}
