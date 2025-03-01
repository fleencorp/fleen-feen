package com.fleencorp.feen.controller.chat.space;

import com.fleencorp.feen.model.dto.chat.CreateChatSpaceDto;
import com.fleencorp.feen.model.dto.chat.UpdateChatSpaceDto;
import com.fleencorp.feen.model.dto.chat.member.JoinChatSpaceDto;
import com.fleencorp.feen.model.dto.chat.member.RequestToJoinChatSpaceDto;
import com.fleencorp.feen.model.dto.event.CreateChatSpaceEventDto;
import com.fleencorp.feen.model.response.chat.space.CreateChatSpaceResponse;
import com.fleencorp.feen.model.response.chat.space.DeleteChatSpaceResponse;
import com.fleencorp.feen.model.response.chat.space.member.LeaveChatSpaceResponse;
import com.fleencorp.feen.model.response.chat.space.membership.JoinChatSpaceResponse;
import com.fleencorp.feen.model.response.chat.space.membership.RequestToJoinChatSpaceResponse;
import com.fleencorp.feen.model.response.chat.space.update.DisableChatSpaceResponse;
import com.fleencorp.feen.model.response.chat.space.update.EnableChatSpaceResponse;
import com.fleencorp.feen.model.response.chat.space.update.UpdateChatSpaceResponse;
import com.fleencorp.feen.model.response.stream.base.CreateStreamResponse;
import com.fleencorp.feen.model.security.FleenUser;
import com.fleencorp.feen.service.chat.space.ChatSpaceService;
import com.fleencorp.feen.service.chat.space.event.ChatSpaceEventService;
import com.fleencorp.feen.service.chat.space.join.ChatSpaceJoinService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/chat-space")
public class ChatSpaceController {

  private final ChatSpaceService chatSpaceService;
  private final ChatSpaceEventService chatSpaceEventService;
  private final ChatSpaceJoinService chatSpaceJoinService;

  public ChatSpaceController(
      final ChatSpaceService chatSpaceService,
      final ChatSpaceEventService chatSpaceEventService,
      final ChatSpaceJoinService chatSpaceJoinService) {
    this.chatSpaceService = chatSpaceService;
    this.chatSpaceEventService = chatSpaceEventService;
    this.chatSpaceJoinService = chatSpaceJoinService;
  }

  @PostMapping(value = "/create")
  public CreateChatSpaceResponse create(
      @Valid @RequestBody final CreateChatSpaceDto createChatSpaceDto,
      @AuthenticationPrincipal final FleenUser user) {
    return chatSpaceService.createChatSpace(createChatSpaceDto, user);
  }

  @PostMapping(value = "/create-event/{chatSpaceId}")
  public CreateStreamResponse createEvent(
      @PathVariable(value = "chatSpaceId") final Long chatSpaceId,
      @Valid @RequestBody final CreateChatSpaceEventDto createChatSpaceEventDto,
      @AuthenticationPrincipal final FleenUser user) {
    return chatSpaceEventService.createChatSpaceEvent(chatSpaceId, createChatSpaceEventDto, user);
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
    return chatSpaceJoinService.joinSpace(chatSpaceId, joinChatSpaceDto, user);
  }

  @PostMapping(value = "/request-to-join/{chatSpaceId}")
  public RequestToJoinChatSpaceResponse requestToJoin(
      @PathVariable(name = "chatSpaceId") final Long chatSpaceId,
      @Valid @RequestBody final RequestToJoinChatSpaceDto requestToJoinChatSpaceDto,
      @AuthenticationPrincipal final FleenUser user) {
    return chatSpaceJoinService.requestToJoinSpace(chatSpaceId, requestToJoinChatSpaceDto, user);
  }

  @PostMapping(value = "/leave/{chatSpaceId}")
  public LeaveChatSpaceResponse leave(
      @PathVariable(name = "chatSpaceId") final Long chatSpaceId,
      @AuthenticationPrincipal final FleenUser user) {
    return chatSpaceJoinService.leaveChatSpace(chatSpaceId, user);
  }

}
