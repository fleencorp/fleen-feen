package com.fleencorp.feen.controller.chat.space;

import com.fleencorp.base.resolver.SearchParam;
import com.fleencorp.feen.exception.auth.InvalidAuthenticationException;
import com.fleencorp.feen.exception.base.FailedOperationException;
import com.fleencorp.feen.exception.chat.space.ChatSpaceNotFoundException;
import com.fleencorp.feen.exception.chat.space.core.NotAnAdminOfChatSpaceException;
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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(value = "/api/chat-space/search")
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

  @Operation(summary = "Search for chat spaces",
    description = "Searches for chat spaces based on specified criteria. Returns a paginated list of " +
                 "chat spaces that match the search parameters. Results include both public spaces and " +
                 "private spaces that the user has access to."
  )
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Search completed successfully",
      content = @Content(schema = @Schema(implementation = ChatSpaceSearchResult.class))),
    @ApiResponse(responseCode = "400", description = "Invalid search parameters",
      content = @Content(schema = @Schema(implementation = FailedOperationException.class))),
    @ApiResponse(responseCode = "401", description = "User not authenticated",
      content = @Content(schema = @Schema(implementation = InvalidAuthenticationException.class)))
  })
  @GetMapping(value = "")
  public ChatSpaceSearchResult findSpaces(
      @Parameter(description = "Search criteria and pagination parameters", required = true)
        @SearchParam final ChatSpaceSearchRequest chatSpaceSearchRequest,
      @Parameter(hidden = true)
        @AuthenticationPrincipal final FleenUser user) {
    return chatSpaceSearchService.findSpaces(chatSpaceSearchRequest, user);
  }

  @Operation(summary = "Search for events in a chat space",
    description = "Retrieves a paginated list of events within a specific chat space. Results are filtered " +
                 "based on search criteria and user's access permissions to the chat space."
  )
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Search completed successfully",
      content = @Content(schema = @Schema(implementation = ChatSpaceEventSearchResult.class))),
    @ApiResponse(responseCode = "400", description = "Invalid search parameters",
      content = @Content(schema = @Schema(implementation = FailedOperationException.class))),
    @ApiResponse(responseCode = "401", description = "User not authenticated",
      content = @Content(schema = @Schema(implementation = InvalidAuthenticationException.class))),
    @ApiResponse(responseCode = "403", description = "User not authorized to view events in this chat space",
      content = @Content(schema = @Schema(implementation = NotAnAdminOfChatSpaceException.class))),
    @ApiResponse(responseCode = "404", description = "Chat space not found",
      content = @Content(schema = @Schema(implementation = ChatSpaceNotFoundException.class)))
  })
  @GetMapping(value = "/event/{chatSpaceId}")
  public ChatSpaceEventSearchResult findChatSpaceEvents(
      @Parameter(description = "ID of the chat space to search events in", required = true)
        @PathVariable(name = "chatSpaceId") final Long chatSpaceId,
      @Parameter(description = "Search criteria and pagination parameters", required = true)
        @SearchParam final ChatSpaceSearchRequest chatSpaceSearchRequest,
      @Parameter(hidden = true)
        @AuthenticationPrincipal final FleenUser user) {
    return chatSpaceEventService.findChatSpaceEvents(chatSpaceId, chatSpaceSearchRequest, user);
  }

  @Operation(summary = "Get detailed information about a chat space",
    description = "Retrieves comprehensive information about a specific chat space, including its " +
                 "configuration, statistics, and the user's relationship to it. Access to private " +
                 "chat space details is restricted to members only."
  )
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Chat space details retrieved successfully",
      content = @Content(schema = @Schema(implementation = RetrieveChatSpaceResponse.class))),
    @ApiResponse(responseCode = "401", description = "User not authenticated",
      content = @Content(schema = @Schema(implementation = InvalidAuthenticationException.class))),
    @ApiResponse(responseCode = "403", description = "User not authorized to view this chat space",
      content = @Content(schema = @Schema(implementation = NotAnAdminOfChatSpaceException.class))),
    @ApiResponse(responseCode = "404", description = "Chat space not found",
      content = @Content(schema = @Schema(implementation = ChatSpaceNotFoundException.class)))
  })
  @GetMapping(value = "/detail/{chatSpaceId}")
  public RetrieveChatSpaceResponse detail(
      @Parameter(description = "ID of the chat space to retrieve details for", required = true)
        @PathVariable(name = "chatSpaceId") final Long chatSpaceId,
      @Parameter(hidden = true)
        @AuthenticationPrincipal final FleenUser user) {
    return chatSpaceSearchService.retrieveChatSpace(chatSpaceId, user);
  }

  @Operation(summary = "Search for members in a chat space",
    description = "Retrieves a paginated list of members in a specific chat space. Results can be filtered " +
                 "based on various criteria such as role, join date, or activity status. Access to member " +
                 "information is restricted based on the user's permissions in the chat space."
  )
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Member search completed successfully",
      content = @Content(schema = @Schema(implementation = ChatSpaceMemberSearchResult.class))),
    @ApiResponse(responseCode = "400", description = "Invalid search parameters",
      content = @Content(schema = @Schema(implementation = FailedOperationException.class))),
    @ApiResponse(responseCode = "401", description = "User not authenticated",
      content = @Content(schema = @Schema(implementation = InvalidAuthenticationException.class))),
    @ApiResponse(responseCode = "403", description = "User not authorized to view members in this chat space",
      content = @Content(schema = @Schema(implementation = NotAnAdminOfChatSpaceException.class))),
    @ApiResponse(responseCode = "404", description = "Chat space not found",
      content = @Content(schema = @Schema(implementation = ChatSpaceNotFoundException.class)))
  })
  @GetMapping(value = "/find-members/{chatSpaceId}")
  public ChatSpaceMemberSearchResult findSpaceMembers(
      @Parameter(description = "ID of the chat space to search members in", required = true)
        @PathVariable(name = "chatSpaceId") final Long chatSpaceId,
      @Parameter(description = "Member search criteria and pagination parameters", required = true)
        @SearchParam final ChatSpaceMemberSearchRequest chatSpaceMemberSearchRequest,
      @Parameter(hidden = true)
        @AuthenticationPrincipal final FleenUser user) {
    return chatSpaceMemberService.findChatSpaceMembers(chatSpaceId, chatSpaceMemberSearchRequest, user);
  }

  @Operation(summary = "Find chat spaces where user is a member",
    description = "Retrieves a paginated list of chat spaces where the authenticated user is a member. " +
                 "This includes spaces where the user has any role (member, moderator, administrator). " +
                 "Results can be filtered and sorted based on various criteria."
  )
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Search completed successfully",
      content = @Content(schema = @Schema(implementation = ChatSpaceSearchResult.class))),
    @ApiResponse(responseCode = "400", description = "Invalid search parameters",
      content = @Content(schema = @Schema(implementation = FailedOperationException.class))),
    @ApiResponse(responseCode = "401", description = "User not authenticated",
      content = @Content(schema = @Schema(implementation = InvalidAuthenticationException.class)))
  })
  @GetMapping(value = "/belong-to")
  public ChatSpaceSearchResult findSpaceIBelong(
      @Parameter(description = "Search criteria and pagination parameters", required = true)
        @SearchParam final ChatSpaceSearchRequest createdSpaceSearchRequest,
      @Parameter(hidden = true)
        @AuthenticationPrincipal final FleenUser user) {
    return chatSpaceSearchService.findSpacesIBelongTo(createdSpaceSearchRequest, user);
  }

  @Operation(summary = "Find chat spaces created by user",
    description = "Retrieves a paginated list of chat spaces that were created by the authenticated user. " +
                 "This includes all spaces where the user is the owner, regardless of their current status " +
                 "(active, disabled, etc.)."
  )
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Search completed successfully",
      content = @Content(schema = @Schema(implementation = ChatSpaceSearchResult.class))),
    @ApiResponse(responseCode = "400", description = "Invalid search parameters",
      content = @Content(schema = @Schema(implementation = FailedOperationException.class))),
    @ApiResponse(responseCode = "401", description = "User not authenticated",
      content = @Content(schema = @Schema(implementation = InvalidAuthenticationException.class)))
  })
  @GetMapping(value = "/created")
  public ChatSpaceSearchResult findSpaceCreated(
      @Parameter(description = "Search criteria and pagination parameters", required = true)
        @SearchParam final ChatSpaceSearchRequest chatSpaceSearchRequest,
      @Parameter(hidden = true)
        @AuthenticationPrincipal final FleenUser user) {
    return chatSpaceSearchService.findSpacesCreated(chatSpaceSearchRequest, user);
  }
}
