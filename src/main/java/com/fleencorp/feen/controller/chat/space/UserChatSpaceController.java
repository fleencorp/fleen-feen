package com.fleencorp.feen.controller.chat.space;

import com.fleencorp.base.resolver.SearchParam;
import com.fleencorp.feen.model.dto.chat.DowngradeChatSpaceAdminToMemberDto;
import com.fleencorp.feen.model.dto.chat.UpgradeChatSpaceMemberToAdminDto;
import com.fleencorp.feen.model.dto.chat.member.AddChatSpaceMemberDto;
import com.fleencorp.feen.model.dto.chat.member.ProcessRequestToJoinChatSpaceDto;
import com.fleencorp.feen.model.dto.chat.member.RemoveChatSpaceMemberDto;
import com.fleencorp.feen.model.request.search.chat.space.ChatSpaceMemberSearchRequest;
import com.fleencorp.feen.model.response.chat.space.member.AddChatSpaceMemberResponse;
import com.fleencorp.feen.model.response.chat.space.member.DowngradeChatSpaceAdminToMemberResponse;
import com.fleencorp.feen.model.response.chat.space.member.RemoveChatSpaceMemberResponse;
import com.fleencorp.feen.model.response.chat.space.member.UpgradeChatSpaceMemberToAdminResponse;
import com.fleencorp.feen.model.response.chat.space.membership.ProcessRequestToJoinChatSpaceResponse;
import com.fleencorp.feen.model.search.join.RequestToJoinSearchResult;
import com.fleencorp.feen.model.security.FleenUser;
import com.fleencorp.feen.service.chat.space.ChatSpaceSearchService;
import com.fleencorp.feen.service.chat.space.join.ChatSpaceJoinService;
import com.fleencorp.feen.service.chat.space.member.ChatSpaceMemberService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/user/chat-space")
public class UserChatSpaceController {

  private final ChatSpaceJoinService chatSpaceJoinService;
  private final ChatSpaceMemberService chatSpaceMemberService;
  private final ChatSpaceSearchService chatSpaceSearchService;

  public UserChatSpaceController(
      final ChatSpaceJoinService chatSpaceJoinService,
      final ChatSpaceMemberService chatSpaceMemberService,
      final ChatSpaceSearchService chatSpaceSearchService) {
    this.chatSpaceJoinService = chatSpaceJoinService;
    this.chatSpaceMemberService = chatSpaceMemberService;
    this.chatSpaceSearchService = chatSpaceSearchService;
  }

  @GetMapping(value = "/request-to-join/{chatSpaceId}")
  public RequestToJoinSearchResult findSpaceRequestToJoin(
      @PathVariable final Long chatSpaceId,
      @SearchParam final ChatSpaceMemberSearchRequest chatSpaceMemberSearchRequest,
      @AuthenticationPrincipal final FleenUser user) {
    return chatSpaceSearchService.findRequestToJoinSpace(chatSpaceId, chatSpaceMemberSearchRequest, user);
  }

  @PutMapping(value = "/upgrade-member/{chatSpaceId}")
  public UpgradeChatSpaceMemberToAdminResponse upgradeMember(
      @PathVariable(name = "chatSpaceId") final Long chatSpaceId,
      @Valid @RequestBody final UpgradeChatSpaceMemberToAdminDto upgradeChatSpaceMemberToAdminDto,
      @AuthenticationPrincipal final FleenUser user) {
    return chatSpaceMemberService.upgradeChatSpaceMemberToAdmin(chatSpaceId, upgradeChatSpaceMemberToAdminDto, user);
  }

  @PutMapping(value = "/downgrade-member/{chatSpaceId}")
  public DowngradeChatSpaceAdminToMemberResponse downgradeMember(
      @PathVariable(name = "chatSpaceId") final Long chatSpaceId,
      @Valid @RequestBody final DowngradeChatSpaceAdminToMemberDto downgradeChatSpaceAdminToMemberDto,
      @AuthenticationPrincipal final FleenUser user) {
    return chatSpaceMemberService.downgradeChatSpaceAdminToMember(chatSpaceId, downgradeChatSpaceAdminToMemberDto, user);
  }

  @PutMapping(value = "/process-join-request/{chatSpaceId}")
  public ProcessRequestToJoinChatSpaceResponse processRequestToJoin(
      @PathVariable(name = "chatSpaceId") final Long chatSpaceId,
      @Valid @RequestBody final ProcessRequestToJoinChatSpaceDto processRequestToJoinChatSpaceDto,
      @AuthenticationPrincipal final FleenUser user) {
    return chatSpaceJoinService.processRequestToJoinSpace(chatSpaceId, processRequestToJoinChatSpaceDto, user);
  }

  @PostMapping(value = "/add-member/{chatSpaceId}")
  public AddChatSpaceMemberResponse addMember(
      @PathVariable(name = "chatSpaceId") final Long chatSpaceId,
      @Valid @RequestBody final AddChatSpaceMemberDto addChatSpaceMemberDto,
      @AuthenticationPrincipal final FleenUser user) {
    return chatSpaceMemberService.addMember(chatSpaceId, addChatSpaceMemberDto, user);
  }

  @DeleteMapping(value = "/remove-member/{chatSpaceId}")
  public RemoveChatSpaceMemberResponse removeMember(
      @PathVariable(name = "chatSpaceId") final Long chatSpaceId,
      @Valid @RequestBody final RemoveChatSpaceMemberDto removeChatSpaceMemberDto,
      @AuthenticationPrincipal final FleenUser user) {
    return chatSpaceMemberService.removeMember(chatSpaceId, removeChatSpaceMemberDto, user);
  }
}
