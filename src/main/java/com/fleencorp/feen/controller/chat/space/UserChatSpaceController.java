package com.fleencorp.feen.controller.chat.space;

import com.fleencorp.feen.exception.base.FailedOperationException;
import com.fleencorp.feen.exception.chat.space.ChatSpaceNotFoundException;
import com.fleencorp.feen.exception.chat.space.core.NotAnAdminOfChatSpaceException;
import com.fleencorp.feen.exception.chat.space.member.ChatSpaceMemberNotFoundException;
import com.fleencorp.feen.model.dto.chat.member.AddChatSpaceMemberDto;
import com.fleencorp.feen.model.dto.chat.member.ProcessRequestToJoinChatSpaceDto;
import com.fleencorp.feen.model.dto.chat.member.RemoveChatSpaceMemberDto;
import com.fleencorp.feen.model.dto.chat.member.RestoreChatSpaceMemberDto;
import com.fleencorp.feen.model.dto.chat.role.DowngradeChatSpaceAdminToMemberDto;
import com.fleencorp.feen.model.dto.chat.role.UpgradeChatSpaceMemberToAdminDto;
import com.fleencorp.feen.model.response.chat.space.member.*;
import com.fleencorp.feen.model.response.chat.space.membership.ProcessRequestToJoinChatSpaceResponse;
import com.fleencorp.feen.service.chat.space.join.ChatSpaceJoinService;
import com.fleencorp.feen.service.chat.space.member.ChatSpaceMemberService;
import com.fleencorp.feen.user.exception.authentication.InvalidAuthenticationException;
import com.fleencorp.feen.user.model.security.RegisteredUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/chat-space")
public class UserChatSpaceController {

  private final ChatSpaceJoinService chatSpaceJoinService;
  private final ChatSpaceMemberService chatSpaceMemberService;

  public UserChatSpaceController(
      final ChatSpaceJoinService chatSpaceJoinService,
      final ChatSpaceMemberService chatSpaceMemberService) {
    this.chatSpaceJoinService = chatSpaceJoinService;
    this.chatSpaceMemberService = chatSpaceMemberService;
  }

  @Operation(summary = "Promote a member to administrator role",
    description = "Upgrades a regular member to an administrator role in the specified chat space. " +
                 "Only existing administrators can promote members. The target member must be an " +
                 "active member of the chat space."
  )
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Member successfully promoted to administrator",
      content = @Content(schema = @Schema(implementation = UpgradeChatSpaceMemberToAdminResponse.class))),
    @ApiResponse(responseCode = "400", description = "Invalid request or member already an administrator",
      content = @Content(schema = @Schema(implementation = FailedOperationException.class))),
    @ApiResponse(responseCode = "401", description = "User not authenticated",
      content = @Content(schema = @Schema(implementation = InvalidAuthenticationException.class))),
    @ApiResponse(responseCode = "403", description = "User not authorized to promote members",
      content = @Content(schema = @Schema(implementation = NotAnAdminOfChatSpaceException.class))),
    @ApiResponse(responseCode = "404", description = "Chat space not found",
      content = @Content(schema = @Schema(implementation = ChatSpaceNotFoundException.class))),
    @ApiResponse(responseCode = "404", description = "Member not found",
      content = @Content(schema = @Schema(implementation = ChatSpaceMemberNotFoundException.class)))
  })
  @PutMapping(value = "/upgrade-member/{chatSpaceId}")
  public UpgradeChatSpaceMemberToAdminResponse upgradeMember(
      @Parameter(description = "ID of the chat space where the member will be promoted", required = true)
        @PathVariable(name = "chatSpaceId") final Long chatSpaceId,
      @Parameter(description = "Details of the member to be promoted", required = true)
        @Valid @RequestBody final UpgradeChatSpaceMemberToAdminDto upgradeChatSpaceMemberToAdminDto,
      @Parameter(hidden = true)
        @AuthenticationPrincipal final RegisteredUser user) {
    return chatSpaceMemberService.upgradeChatSpaceMemberToAdmin(chatSpaceId, upgradeChatSpaceMemberToAdminDto, user);
  }

  @Operation(summary = "Demote an administrator to regular member",
    description = "Downgrades an administrator to a regular member role in the specified chat space. " +
                 "Only chat space owners or higher-level administrators can demote other administrators. " +
                 "The last administrator cannot be demoted without first appointing a replacement."
  )
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Administrator successfully demoted to member",
      content = @Content(schema = @Schema(implementation = DowngradeChatSpaceAdminToMemberResponse.class))),
    @ApiResponse(responseCode = "400", description = "Invalid request or cannot demote last administrator",
      content = @Content(schema = @Schema(implementation = FailedOperationException.class))),
    @ApiResponse(responseCode = "401", description = "User not authenticated",
      content = @Content(schema = @Schema(implementation = InvalidAuthenticationException.class))),
    @ApiResponse(responseCode = "403", description = "User not authorized to demote administrators",
      content = @Content(schema = @Schema(implementation = NotAnAdminOfChatSpaceException.class))),
    @ApiResponse(responseCode = "404", description = "Chat space not found",
      content = @Content(schema = @Schema(implementation = ChatSpaceNotFoundException.class))),
    @ApiResponse(responseCode = "404", description = "Administrator not found",
      content = @Content(schema = @Schema(implementation = ChatSpaceMemberNotFoundException.class)))
  })
  @PutMapping(value = "/downgrade-member/{chatSpaceId}")
  public DowngradeChatSpaceAdminToMemberResponse downgradeMember(
      @Parameter(description = "ID of the chat space where the administrator will be demoted", required = true)
        @PathVariable(name = "chatSpaceId") final Long chatSpaceId,
      @Parameter(description = "Details of the administrator to be demoted", required = true)
        @Valid @RequestBody final DowngradeChatSpaceAdminToMemberDto downgradeChatSpaceAdminToMemberDto,
      @Parameter(hidden = true)
        @AuthenticationPrincipal final RegisteredUser user) {
    return chatSpaceMemberService.downgradeChatSpaceAdminToMember(chatSpaceId, downgradeChatSpaceAdminToMemberDto, user);
  }


  @Operation(summary = "Process a pending join request",
    description = "Approves or rejects a request to join the chat space. Only chat space administrators " +
                 "can process join requests. When approved, the user becomes a member with default " +
                 "permissions. When rejected, the user must submit a new request to join."
  )
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Join request successfully processed",
      content = @Content(schema = @Schema(implementation = ProcessRequestToJoinChatSpaceResponse.class))),
    @ApiResponse(responseCode = "400", description = "Invalid request or request already processed",
      content = @Content(schema = @Schema(implementation = FailedOperationException.class))),
    @ApiResponse(responseCode = "401", description = "User not authenticated",
      content = @Content(schema = @Schema(implementation = InvalidAuthenticationException.class))),
    @ApiResponse(responseCode = "403", description = "User not authorized to process join requests",
      content = @Content(schema = @Schema(implementation = NotAnAdminOfChatSpaceException.class))),
    @ApiResponse(responseCode = "404", description = "Chat space not found",
      content = @Content(schema = @Schema(implementation = ChatSpaceNotFoundException.class))),
    @ApiResponse(responseCode = "404", description = "Join request not found",
      content = @Content(schema = @Schema(implementation = ChatSpaceMemberNotFoundException.class)))
  })
  @PutMapping(value = "/process-join-request/{chatSpaceId}")
  public ProcessRequestToJoinChatSpaceResponse processRequestToJoin(
      @Parameter(description = "ID of the chat space where the join request will be processed", required = true)
        @PathVariable(name = "chatSpaceId") final Long chatSpaceId,
      @Parameter(description = "Processing details including approval/rejection decision", required = true)
        @Valid @RequestBody final ProcessRequestToJoinChatSpaceDto processRequestToJoinChatSpaceDto,
      @Parameter(hidden = true)
        @AuthenticationPrincipal final RegisteredUser user) {
    return chatSpaceJoinService.processRequestToJoinSpace(chatSpaceId, processRequestToJoinChatSpaceDto, user);
  }

  @Operation(summary = "Add a member directly to a chat space",
    description = "Directly adds a user as a member to the chat space, bypassing the join request process. " +
                 "This operation can only be performed by chat space administrators. The added user will " +
                 "receive default member permissions."
  )
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Member successfully added to chat space",
      content = @Content(schema = @Schema(implementation = AddChatSpaceMemberResponse.class))),
    @ApiResponse(responseCode = "400", description = "Invalid request or user already a member",
      content = @Content(schema = @Schema(implementation = FailedOperationException.class))),
    @ApiResponse(responseCode = "401", description = "User not authenticated",
      content = @Content(schema = @Schema(implementation = InvalidAuthenticationException.class))),
    @ApiResponse(responseCode = "403", description = "User not authorized to add members",
      content = @Content(schema = @Schema(implementation = NotAnAdminOfChatSpaceException.class))),
    @ApiResponse(responseCode = "404", description = "Chat space not found",
      content = @Content(schema = @Schema(implementation = ChatSpaceNotFoundException.class))),
    @ApiResponse(responseCode = "404", description = "Target user not found",
      content = @Content(schema = @Schema(implementation = ChatSpaceMemberNotFoundException.class)))
  })
  @PostMapping(value = "/add-member/{chatSpaceId}")
  public AddChatSpaceMemberResponse addMember(
      @Parameter(description = "ID of the chat space where the member will be added", required = true)
        @PathVariable(name = "chatSpaceId") final Long chatSpaceId,
      @Parameter(description = "Details of the user to be added as member", required = true)
        @Valid @RequestBody final AddChatSpaceMemberDto addChatSpaceMemberDto,
      @Parameter(hidden = true)
        @AuthenticationPrincipal final RegisteredUser user) {
    return chatSpaceMemberService.addMember(chatSpaceId, addChatSpaceMemberDto, user);
  }

  @Operation(summary = "Restore a removed member to a chat space",
    description = "Restores a member who was previously removed back into the specified chat space. " +
      "This operation typically requires administrative privileges within the chat space."
  )
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Member restored to the chat space successfully",
      content = @Content(schema = @Schema(implementation = RestoreChatSpaceMemberResponse.class))),
    @ApiResponse(responseCode = "400", description = "Invalid restore parameters",
      content = @Content(schema = @Schema(implementation = FailedOperationException.class))),
    @ApiResponse(responseCode = "401", description = "User not authenticated",
      content = @Content(schema = @Schema(implementation = InvalidAuthenticationException.class))),
    @ApiResponse(responseCode = "403", description = "User not authorized to restore members in this chat space",
      content = @Content(schema = @Schema(implementation = NotAnAdminOfChatSpaceException.class))),
    @ApiResponse(responseCode = "404", description = "Chat space or member not found",
      content = @Content(schema = @Schema(oneOf = {ChatSpaceNotFoundException.class, ChatSpaceMemberNotFoundException.class})))
  })
  @PutMapping(value = "/restore-member/{chatSpaceId}")
  public RestoreChatSpaceMemberResponse restoreMember(
      @Parameter(description = "ID of the chat space where the member will be restored", required = true)
        @PathVariable(name = "chatSpaceId") final Long chatSpaceId,
      @Parameter(description = "Details of the chat space member to be restore", required = true)
        @Valid @RequestBody final RestoreChatSpaceMemberDto restoreChatSpaceMemberDto,
      @Parameter(hidden = true)
        @AuthenticationPrincipal final RegisteredUser user) {
    return chatSpaceMemberService.restoreRemovedMember(chatSpaceId, restoreChatSpaceMemberDto, user);
  }

  @Operation(summary = "Remove a member from a chat space",
    description = "Removes a member from the chat space, revoking their access and permissions. This " +
                 "operation can only be performed by chat space administrators. Administrators cannot be " +
                 "removed using this endpoint; they must first be demoted to regular members."
  )
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Member successfully removed from chat space",
      content = @Content(schema = @Schema(implementation = RemoveChatSpaceMemberResponse.class))),
    @ApiResponse(responseCode = "400", description = "Invalid request or cannot remove administrator",
      content = @Content(schema = @Schema(implementation = FailedOperationException.class))),
    @ApiResponse(responseCode = "401", description = "User not authenticated",
      content = @Content(schema = @Schema(implementation = InvalidAuthenticationException.class))),
    @ApiResponse(responseCode = "403", description = "User not authorized to remove members",
      content = @Content(schema = @Schema(implementation = NotAnAdminOfChatSpaceException.class))),
    @ApiResponse(responseCode = "404", description = "Chat space not found",
      content = @Content(schema = @Schema(implementation = ChatSpaceNotFoundException.class))),
    @ApiResponse(responseCode = "404", description = "Member not found",
      content = @Content(schema = @Schema(implementation = ChatSpaceMemberNotFoundException.class)))
  })
  @PutMapping(value = "/remove-member/{chatSpaceId}")
  public RemoveChatSpaceMemberResponse removeMember(
      @Parameter(description = "ID of the chat space where the member will be removed from", required = true)
        @PathVariable(name = "chatSpaceId") final Long chatSpaceId,
      @Parameter(description = "Details of the member to be removed", required = true)
        @Valid @RequestBody final RemoveChatSpaceMemberDto removeChatSpaceMemberDto,
      @Parameter(hidden = true)
        @AuthenticationPrincipal final RegisteredUser user) {
    return chatSpaceMemberService.removeMember(chatSpaceId, removeChatSpaceMemberDto, user);
  }
}
