package com.fleencorp.feen.controller.chat.space;

import com.fleencorp.base.resolver.SearchParam;
import com.fleencorp.feen.model.dto.chat.DowngradeChatSpaceAdminToMemberDto;
import com.fleencorp.feen.model.dto.chat.UpgradeChatSpaceMemberToAdminDto;
import com.fleencorp.feen.model.dto.chat.member.AddChatSpaceMemberDto;
import com.fleencorp.feen.model.dto.chat.member.ProcessRequestToJoinChatSpaceDto;
import com.fleencorp.feen.model.dto.chat.member.RemoveChatSpaceMemberDto;
import com.fleencorp.feen.model.request.search.chat.space.ChatSpaceMemberSearchRequest;
import com.fleencorp.feen.model.request.search.chat.space.ChatSpaceSearchRequest;
import com.fleencorp.feen.model.response.chat.space.ProcessRequestToJoinChatSpaceResponse;
import com.fleencorp.feen.model.response.chat.space.member.AddChatSpaceMemberResponse;
import com.fleencorp.feen.model.response.chat.space.member.DowngradeChatSpaceAdminToMemberResponse;
import com.fleencorp.feen.model.response.chat.space.member.RemoveChatSpaceMemberResponse;
import com.fleencorp.feen.model.response.chat.space.member.UpgradeChatSpaceMemberToAdminResponse;
import com.fleencorp.feen.model.search.broadcast.request.RequestToJoinSearchResult;
import com.fleencorp.feen.model.search.chat.space.ChatSpaceSearchResult;
import com.fleencorp.feen.model.search.chat.space.member.ChatSpaceMemberSearchResult;
import com.fleencorp.feen.model.security.FleenUser;
import com.fleencorp.feen.service.chat.space.ChatSpaceService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/user/chat-space")
public class UserChatSpaceController {

  private final ChatSpaceService chatSpaceService;

  public UserChatSpaceController(final ChatSpaceService chatSpaceService) {
    this.chatSpaceService = chatSpaceService;
  }

  @GetMapping(value = "/created")
  public ChatSpaceSearchResult findSpaceCreated(
      @SearchParam final ChatSpaceSearchRequest chatSpaceSearchRequest,
      @AuthenticationPrincipal final FleenUser user) {
    return chatSpaceService.findSpacesCreated(chatSpaceSearchRequest, user);
  }

  @GetMapping(value = "/belong-to")
  public ChatSpaceSearchResult findSpaceIBelong(
      final ChatSpaceSearchRequest createdSpaceSearchRequest,
      @AuthenticationPrincipal final FleenUser user) {
    return chatSpaceService.findSpacesIBelongTo(createdSpaceSearchRequest, user);
  }

  @GetMapping(value = "/find-members/{chatSpaceId}")
  public ChatSpaceMemberSearchResult findSpaceMembers(
      @PathVariable(name = "chatSpaceId") final Long chatSpaceId,
      @SearchParam final ChatSpaceMemberSearchRequest chatSpaceMemberSearchRequest,
      @AuthenticationPrincipal final FleenUser user) {
    return chatSpaceService.findChatSpaceMembers(chatSpaceId, chatSpaceMemberSearchRequest, user);
  }

  @GetMapping(value = "/request-to-join/{chatSpaceId}")
  public RequestToJoinSearchResult findSpaceRequestToJoin(
      @PathVariable final Long chatSpaceId,
      @SearchParam final ChatSpaceMemberSearchRequest chatSpaceMemberSearchRequest,
      @AuthenticationPrincipal final FleenUser user) {
    return chatSpaceService.findRequestToJoinSpace(chatSpaceId, chatSpaceMemberSearchRequest, user);
  }

  @PutMapping(value = "/upgrade-member/{chatSpaceId}")
  public UpgradeChatSpaceMemberToAdminResponse upgradeMember(
    @PathVariable(name = "chatSpaceId") final Long chatSpaceId,
    @Valid @RequestBody final UpgradeChatSpaceMemberToAdminDto upgradeChatSpaceMemberToAdminDto,
    @AuthenticationPrincipal final FleenUser user) {
    return chatSpaceService.upgradeChatSpaceMemberToAdmin(chatSpaceId, upgradeChatSpaceMemberToAdminDto, user);
  }

  @PutMapping(value = "/downgrade-member/{chatSpaceId}")
  public DowngradeChatSpaceAdminToMemberResponse downgradeMember(
      @PathVariable(name = "chatSpaceId") final Long chatSpaceId,
      @Valid @RequestBody final DowngradeChatSpaceAdminToMemberDto downgradeChatSpaceAdminToMemberDto,
      @AuthenticationPrincipal final FleenUser user) {
    return chatSpaceService.downgradeChatSpaceAdminToMember(chatSpaceId, downgradeChatSpaceAdminToMemberDto, user);
  }

  @PutMapping(value = "/process-join-request/{chatSpaceId}")
  public ProcessRequestToJoinChatSpaceResponse processRequestToJoin(
      @PathVariable(name = "chatSpaceId") final Long chatSpaceId,
      @Valid @RequestBody final ProcessRequestToJoinChatSpaceDto processRequestToJoinChatSpaceDto,
      @AuthenticationPrincipal final FleenUser user) {
    return chatSpaceService.processRequestToJoinSpace(chatSpaceId, processRequestToJoinChatSpaceDto, user);
  }

  @PostMapping(value = "/add-member/{chatSpaceId}")
  public AddChatSpaceMemberResponse addMember(
      @PathVariable(name = "chatSpaceId") final Long chatSpaceId,
      @Valid @RequestBody final AddChatSpaceMemberDto addChatSpaceMemberDto,
      @AuthenticationPrincipal final FleenUser user) {
    return chatSpaceService.addMember(chatSpaceId, addChatSpaceMemberDto, user);
  }

  @DeleteMapping(value = "/remove-member/{chatSpaceId}")
  public RemoveChatSpaceMemberResponse removeMember(
      @PathVariable(name = "chatSpaceId") final Long chatSpaceId,
      @Valid @RequestBody final RemoveChatSpaceMemberDto removeChatSpaceMemberDto,
      @AuthenticationPrincipal final FleenUser user) {
    return chatSpaceService.removeMember(chatSpaceId, removeChatSpaceMemberDto, user);
  }
}
