package com.fleencorp.feen.chat.space.controller;

import com.fleencorp.feen.common.exception.FailedOperationException;
import com.fleencorp.feen.chat.space.exception.core.ChatSpaceNotFoundException;
import com.fleencorp.feen.chat.space.exception.core.ChatSpaceAlreadyDeletedException;
import com.fleencorp.feen.chat.space.exception.core.ChatSpaceNotActiveException;
import com.fleencorp.feen.chat.space.exception.core.NotAnAdminOfChatSpaceException;
import com.fleencorp.feen.chat.space.exception.request.AlreadyJoinedChatSpaceException;
import com.fleencorp.feen.chat.space.exception.request.CannotJoinPrivateChatSpaceWithoutApprovalException;
import com.fleencorp.feen.chat.space.exception.request.RequestToJoinChatSpacePendingException;
import com.fleencorp.feen.chat.space.exception.member.ChatSpaceMemberNotFoundException;
import com.fleencorp.feen.chat.space.model.dto.core.CreateChatSpaceDto;
import com.fleencorp.feen.chat.space.model.dto.core.UpdateChatSpaceDto;
import com.fleencorp.feen.chat.space.model.dto.core.UpdateChatSpaceStatusDto;
import com.fleencorp.feen.chat.space.model.dto.join.request.JoinChatSpaceDto;
import com.fleencorp.feen.chat.space.model.dto.join.request.RequestToJoinChatSpaceDto;
import com.fleencorp.feen.stream.model.dto.event.CreateChatSpaceEventDto;
import com.fleencorp.feen.chat.space.model.response.CreateChatSpaceResponse;
import com.fleencorp.feen.chat.space.model.response.DeleteChatSpaceResponse;
import com.fleencorp.feen.chat.space.model.response.member.LeaveChatSpaceResponse;
import com.fleencorp.feen.chat.space.model.response.membership.JoinChatSpaceResponse;
import com.fleencorp.feen.chat.space.model.response.membership.RequestToJoinChatSpaceResponse;
import com.fleencorp.feen.chat.space.model.response.update.UpdateChatSpaceResponse;
import com.fleencorp.feen.chat.space.model.response.update.UpdateChatSpaceStatusResponse;
import com.fleencorp.feen.stream.model.response.base.CreateStreamResponse;
import com.fleencorp.feen.chat.space.service.core.ChatSpaceService;
import com.fleencorp.feen.chat.space.service.event.ChatSpaceEventService;
import com.fleencorp.feen.chat.space.service.join.ChatSpaceJoinService;
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

  @Operation(summary = "Create a new chat space",
    description = "Creates a new chat space with the specified configuration. The authenticated user becomes " +
                 "the owner and administrator of the created chat space. The space can be configured as public " +
                 "or private, affecting how other users can join."
  )
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Chat space successfully created",
      content = @Content(schema = @Schema(implementation = CreateChatSpaceResponse.class))),
    @ApiResponse(responseCode = "400", description = "Invalid chat space configuration or validation failed",
      content = @Content(schema = @Schema(implementation = FailedOperationException.class))),
    @ApiResponse(responseCode = "401", description = "User not authenticated",
      content = @Content(schema = @Schema(implementation = InvalidAuthenticationException.class))),
    @ApiResponse(responseCode = "403", description = "User not authorized to create chat spaces",
      content = @Content(schema = @Schema(implementation = NotAnAdminOfChatSpaceException.class)))
  })
  @PostMapping(value = "/create")
  public CreateChatSpaceResponse create(
      @Parameter(description = "Chat space configuration details", required = true)
        @Valid @RequestBody final CreateChatSpaceDto createChatSpaceDto,
      @Parameter(hidden = true)
        @AuthenticationPrincipal final RegisteredUser user) {
    return chatSpaceService.createChatSpace(createChatSpaceDto, user);
  }

  @Operation(summary = "Create a new event in a chat space",
    description = "Creates a new event within the specified chat space. Only chat space administrators " +
                 "or members with appropriate permissions can create events. The event can be configured " +
                 "with various settings including schedule, visibility, and participation requirements."
  )
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Event successfully created in the chat space",
      content = @Content(schema = @Schema(implementation = CreateStreamResponse.class))),
    @ApiResponse(responseCode = "400", description = "Invalid event configuration or validation failed",
      content = @Content(schema = @Schema(implementation = FailedOperationException.class))),
    @ApiResponse(responseCode = "401", description = "User not authenticated",
      content = @Content(schema = @Schema(implementation = InvalidAuthenticationException.class))),
    @ApiResponse(responseCode = "403", description = "User not authorized to create events in this chat space",
      content = @Content(schema = @Schema(implementation = NotAnAdminOfChatSpaceException.class))),
    @ApiResponse(responseCode = "404", description = "Chat space not found",
      content = @Content(schema = @Schema(implementation = ChatSpaceNotFoundException.class)))
  })
  @PostMapping(value = "/create-event/{chatSpaceId}")
  public CreateStreamResponse createEvent(
      @Parameter(description = "ID of the chat space where the event will be created", required = true)
        @PathVariable(value = "chatSpaceId") final Long chatSpaceId,
      @Parameter(description = "Event configuration details", required = true)
        @Valid @RequestBody final CreateChatSpaceEventDto createChatSpaceEventDto,
      @Parameter(hidden = true)
        @AuthenticationPrincipal final RegisteredUser user) {
    return chatSpaceEventService.createChatSpaceEvent(chatSpaceId, createChatSpaceEventDto, user);
  }

  @Operation(summary = "Update an existing chat space",
    description = "Updates the configuration and settings of an existing chat space. Only chat space " +
                 "administrators can modify chat space settings. Updates can include changes to name, " +
                 "description, visibility, and other configurable properties."
  )
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Chat space successfully updated",
      content = @Content(schema = @Schema(implementation = UpdateChatSpaceResponse.class))),
    @ApiResponse(responseCode = "400", description = "Invalid update configuration or validation failed",
      content = @Content(schema = @Schema(implementation = FailedOperationException.class))),
    @ApiResponse(responseCode = "401", description = "User not authenticated",
      content = @Content(schema = @Schema(implementation = InvalidAuthenticationException.class))),
    @ApiResponse(responseCode = "403", description = "User not authorized to update this chat space",
      content = @Content(schema = @Schema(implementation = NotAnAdminOfChatSpaceException.class))),
    @ApiResponse(responseCode = "404", description = "Chat space not found",
      content = @Content(schema = @Schema(implementation = ChatSpaceNotFoundException.class)))
  })
  @PutMapping(value = "/update/{chatSpaceId}")
  public UpdateChatSpaceResponse update(
      @Parameter(description = "ID of the chat space to update", required = true)
        @PathVariable(name = "chatSpaceId") final Long chatSpaceId,
      @Parameter(description = "Updated chat space configuration", required = true)
        @Valid @RequestBody final UpdateChatSpaceDto updateChatSpaceDto,
      @Parameter(hidden = true)
        @AuthenticationPrincipal final RegisteredUser user) {
    return chatSpaceService.updateChatSpace(chatSpaceId, updateChatSpaceDto, user);
  }

  @Operation(summary = "Delete a chat space",
    description = "Deletes an existing chat space. This operation can only be performed by the chat space owner " +
                 "or administrators. All associated data including events, messages, and memberships will be removed."
  )
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Chat space successfully deleted",
      content = @Content(schema = @Schema(implementation = DeleteChatSpaceResponse.class))),
    @ApiResponse(responseCode = "401", description = "User not authenticated",
      content = @Content(schema = @Schema(implementation = InvalidAuthenticationException.class))),
    @ApiResponse(responseCode = "403", description = "User not authorized to delete this chat space",
      content = @Content(schema = @Schema(implementation = NotAnAdminOfChatSpaceException.class))),
    @ApiResponse(responseCode = "404", description = "Chat space not found",
      content = @Content(schema = @Schema(implementation = ChatSpaceNotFoundException.class)))
  })
  @PutMapping(value = "/delete/{chatSpaceId}")
  public DeleteChatSpaceResponse delete(
      @Parameter(description = "ID of the chat space to delete", required = true)
        @PathVariable(name = "chatSpaceId") final Long chatSpaceId,
      @Parameter(hidden = true)
        @AuthenticationPrincipal final RegisteredUser user) {
    return chatSpaceService.deleteChatSpace(chatSpaceId, user);
  }

  @Operation(summary = "Delete a chat space (Admin operation)",
    description = "Administrative endpoint to delete any chat space, regardless of ownership. This operation " +
                 "can only be performed by system administrators. All associated data including events, " +
                 "messages, and memberships will be removed."
  )
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Chat space successfully deleted by admin",
      content = @Content(schema = @Schema(implementation = DeleteChatSpaceResponse.class))),
    @ApiResponse(responseCode = "401", description = "User not authenticated",
      content = @Content(schema = @Schema(implementation = InvalidAuthenticationException.class))),
    @ApiResponse(responseCode = "403", description = "User not authorized as system administrator",
      content = @Content(schema = @Schema(implementation = NotAnAdminOfChatSpaceException.class))),
    @ApiResponse(responseCode = "404", description = "Chat space not found",
      content = @Content(schema = @Schema(implementation = ChatSpaceNotFoundException.class)))
  })
  @PutMapping(value = "/admin/delete/{chatSpaceId}")
  public DeleteChatSpaceResponse deleteByAdmin(
      @Parameter(description = "ID of the chat space to delete", required = true)
        @PathVariable(name = "chatSpaceId") final Long chatSpaceId,
      @Parameter(hidden = true)
        @AuthenticationPrincipal final RegisteredUser user) {
    return chatSpaceService.deleteChatSpaceByAdmin(chatSpaceId, user);
  }

  @Operation(summary = "Enable or disable a chat space",
    description = "Activates or Deactivate a chat space, making it accessible or inaccessible to members. This operation can only " +
      "be performed by chat space administrators. When enabled, members can access the chat space " +
      "and its content according to their permissions. When disabled, members cannot access the chat space " +
      "and its content according to their permissions"
  )
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Chat space successfully enabled",
      content = @Content(schema = @Schema(implementation = UpdateChatSpaceStatusResponse.class))),
    @ApiResponse(responseCode = "401", description = "User not authenticated",
      content = @Content(schema = @Schema(implementation = InvalidAuthenticationException.class))),
    @ApiResponse(responseCode = "403", description = "User not authorized to enable this chat space",
      content = @Content(schema = @Schema(implementation = NotAnAdminOfChatSpaceException.class))),
    @ApiResponse(responseCode = "404", description = "Chat space not found",
      content = @Content(schema = @Schema(implementation = ChatSpaceNotFoundException.class))),
    @ApiResponse(responseCode = "400", description = "Chat space is already deleted",
      content = @Content(schema = @Schema(implementation = ChatSpaceAlreadyDeletedException.class)))
  })
  @PutMapping(value = "/update-status/{chatSpaceId}")
  public UpdateChatSpaceStatusResponse updateStatus(
    @Parameter(description = "ID of the chat space to enable", required = true)
      @PathVariable(name = "chatSpaceId") final Long chatSpaceId,
    @Parameter(description = "Update status details", required = true)
      @Valid @RequestBody final UpdateChatSpaceStatusDto updateChatSpaceStatusDto,
    @Parameter(hidden = true)
      @AuthenticationPrincipal final RegisteredUser user) {
    return chatSpaceService.updateChatSpaceStatus(chatSpaceId, updateChatSpaceStatusDto, user);
  }

  @Operation(summary = "Join a chat space",
    description = "Allows a user to join a public chat space directly. For private chat spaces, users must use " +
                 "the request-to-join endpoint instead. Successfully joining grants immediate access to the " +
                 "chat space's content based on member permissions."
  )
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Successfully joined the chat space",
      content = @Content(schema = @Schema(implementation = JoinChatSpaceResponse.class))),
    @ApiResponse(responseCode = "400", description = "Invalid join request or cannot join private space directly",
      content = @Content(schema = @Schema(implementation = CannotJoinPrivateChatSpaceWithoutApprovalException.class))),
    @ApiResponse(responseCode = "401", description = "User not authenticated",
      content = @Content(schema = @Schema(implementation = InvalidAuthenticationException.class))),
    @ApiResponse(responseCode = "403", description = "User banned or not allowed to join",
      content = @Content(schema = @Schema(implementation = ChatSpaceNotActiveException.class))),
    @ApiResponse(responseCode = "404", description = "Chat space not found",
      content = @Content(schema = @Schema(implementation = ChatSpaceNotFoundException.class))),
    @ApiResponse(responseCode = "409", description = "User is already a member",
      content = @Content(schema = @Schema(implementation = AlreadyJoinedChatSpaceException.class)))
  })
  @PostMapping(value = "/join/{chatSpaceId}")
  public JoinChatSpaceResponse join(
      @Parameter(description = "ID of the chat space to join", required = true)
        @PathVariable(name = "chatSpaceId") final Long chatSpaceId,
      @Parameter(description = "Join request details", required = true)
        @Valid @RequestBody final JoinChatSpaceDto joinChatSpaceDto,
      @Parameter(hidden = true)
        @AuthenticationPrincipal final RegisteredUser user) {
    return chatSpaceJoinService.joinSpace(chatSpaceId, joinChatSpaceDto, user);
  }

  @Operation(summary = "Request to join a private chat space",
    description = "Submits a request to join a private chat space. The request must be approved by a chat " +
                 "space administrator before access is granted. This endpoint should be used when attempting " +
                 "to join private chat spaces."
  )
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Join request successfully submitted",
      content = @Content(schema = @Schema(implementation = RequestToJoinChatSpaceResponse.class))),
    @ApiResponse(responseCode = "400", description = "Invalid request or space is not private",
      content = @Content(schema = @Schema(implementation = FailedOperationException.class))),
    @ApiResponse(responseCode = "401", description = "User not authenticated",
      content = @Content(schema = @Schema(implementation = InvalidAuthenticationException.class))),
    @ApiResponse(responseCode = "403", description = "User banned or not allowed to request joining",
      content = @Content(schema = @Schema(implementation = ChatSpaceNotActiveException.class))),
    @ApiResponse(responseCode = "404", description = "Chat space not found",
      content = @Content(schema = @Schema(implementation = ChatSpaceNotFoundException.class))),
    @ApiResponse(responseCode = "409", description = "Request already pending",
      content = @Content(schema = @Schema(implementation = RequestToJoinChatSpacePendingException.class))),
    @ApiResponse(responseCode = "409", description = "User is already a member",
      content = @Content(schema = @Schema(implementation = AlreadyJoinedChatSpaceException.class)))
  })
  @PostMapping(value = "/request-to-join/{chatSpaceId}")
  public RequestToJoinChatSpaceResponse requestToJoin(
      @Parameter(description = "ID of the chat space to request joining", required = true)
        @PathVariable(name = "chatSpaceId") final Long chatSpaceId,
      @Parameter(description = "Join request details including optional message", required = true)
        @Valid @RequestBody final RequestToJoinChatSpaceDto requestToJoinChatSpaceDto,
      @Parameter(hidden = true)
        @AuthenticationPrincipal final RegisteredUser user) {
    return chatSpaceJoinService.requestToJoinSpace(chatSpaceId, requestToJoinChatSpaceDto, user);
  }

  @Operation(summary = "Leave a chat space",
    description = "Allows a member to voluntarily leave a chat space. This action removes all member " +
                 "permissions and access to the chat space content. Chat space owners cannot leave " +
                 "without first transferring ownership."
  )
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Successfully left the chat space",
      content = @Content(schema = @Schema(implementation = LeaveChatSpaceResponse.class))),
    @ApiResponse(responseCode = "400", description = "Invalid leave request or owner cannot leave",
      content = @Content(schema = @Schema(implementation = FailedOperationException.class))),
    @ApiResponse(responseCode = "401", description = "User not authenticated",
      content = @Content(schema = @Schema(implementation = InvalidAuthenticationException.class))),
    @ApiResponse(responseCode = "403", description = "User not a member of the chat space",
      content = @Content(schema = @Schema(implementation = ChatSpaceMemberNotFoundException.class))),
    @ApiResponse(responseCode = "404", description = "Chat space not found",
      content = @Content(schema = @Schema(implementation = ChatSpaceNotFoundException.class)))
  })
  @PostMapping(value = "/leave/{chatSpaceId}")
  public LeaveChatSpaceResponse leave(
      @Parameter(description = "ID of the chat space to leave", required = true)
        @PathVariable(name = "chatSpaceId") final Long chatSpaceId,
      @Parameter(hidden = true)
        @AuthenticationPrincipal final RegisteredUser user) {
    return chatSpaceJoinService.leaveChatSpace(chatSpaceId, user);
  }

}
