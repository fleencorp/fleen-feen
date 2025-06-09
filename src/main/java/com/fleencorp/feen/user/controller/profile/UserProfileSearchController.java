package com.fleencorp.feen.user.controller.profile;

import com.fleencorp.base.resolver.SearchParam;
import com.fleencorp.feen.user.exception.auth.InvalidAuthenticationException;
import com.fleencorp.feen.exception.base.FailedOperationException;
import com.fleencorp.feen.model.request.search.chat.space.ChatSpaceSearchRequest;
import com.fleencorp.feen.model.request.search.stream.StreamSearchRequest;
import com.fleencorp.feen.model.search.chat.space.ChatSpaceSearchResult;
import com.fleencorp.feen.model.search.chat.space.mutual.MutualChatSpaceMembershipSearchResult;
import com.fleencorp.feen.model.search.stream.common.StreamSearchResult;
import com.fleencorp.feen.model.search.stream.common.UserCreatedStreamsSearchResult;
import com.fleencorp.feen.model.search.stream.mutual.MutualStreamAttendanceSearchResult;
import com.fleencorp.feen.user.security.RegisteredUser;
import com.fleencorp.feen.service.chat.space.ChatSpaceSearchService;
import com.fleencorp.feen.service.stream.search.StreamSearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/user")
public class UserProfileSearchController {

  private final StreamSearchService streamSearchService;
  private final ChatSpaceSearchService chatSpaceSearchService;

  public UserProfileSearchController(
      final StreamSearchService streamSearchService,
      final ChatSpaceSearchService chatSpaceSearchService) {
    this.streamSearchService = streamSearchService;
    this.chatSpaceSearchService = chatSpaceSearchService;
  }

  @Operation(summary = "Get streams created by a user",
    description = "Retrieves all streams that match the search criteria and were created by a specific user. Requires authentication."
  )
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Successfully retrieved created streams",
      content = @Content(schema = @Schema(implementation = StreamSearchResult.class))),
    @ApiResponse(responseCode = "401", description = "User not authenticated",
      content = @Content(schema = @Schema(implementation = InvalidAuthenticationException.class))),
    @ApiResponse(responseCode = "400", description = "Invalid search parameters",
      content = @Content(schema = @Schema(implementation = FailedOperationException.class)))
  })
  @PreAuthorize("hasAnyRole('USER', 'ADMINISTRATOR', 'SUPER_ADMINISTRATOR')")
  @GetMapping(value = "/stream-created-by-user")
  public UserCreatedStreamsSearchResult findStreamsCreatedByUser(
    @Parameter(description = "Search criteria for created streams", required = true)
    @SearchParam final StreamSearchRequest searchRequest) {
    return streamSearchService.findStreamsCreatedByUser(searchRequest);
  }

  @Operation(summary = "Get streams attended with another user",
    description = "Retrieves all streams that the authenticated user has attended together with another user. Requires authentication."
  )
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Successfully retrieved co-attended streams",
      content = @Content(schema = @Schema(implementation = StreamSearchResult.class))),
    @ApiResponse(responseCode = "401", description = "User not authenticated",
      content = @Content(schema = @Schema(implementation = InvalidAuthenticationException.class))),
    @ApiResponse(responseCode = "400", description = "Invalid search parameters",
      content = @Content(schema = @Schema(implementation = FailedOperationException.class)))
  })
  @PreAuthorize("hasAnyRole('USER', 'ADMINISTRATOR', 'SUPER_ADMINISTRATOR')")
  @GetMapping(value = "/stream-attended-with-user")
  public MutualStreamAttendanceSearchResult findStreamsAttendedWithAnotherUser(
      @Parameter(description = "Search criteria for co-attended streams", required = true)
        @SearchParam final StreamSearchRequest searchRequest,
      @Parameter(hidden = true)
        @AuthenticationPrincipal final RegisteredUser user) {
    return streamSearchService.findStreamsAttendedWithAnotherUser(searchRequest, user);
  }

  @Operation(summary = "Get chat spaces with another user",
    description = "Retrieves all chat spaces where the authenticated user and another user are both members. Requires authentication."
  )
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Successfully retrieved co-membership chat spaces",
      content = @Content(schema = @Schema(implementation = ChatSpaceSearchResult.class))),
    @ApiResponse(responseCode = "401", description = "User not authenticated",
      content = @Content(schema = @Schema(implementation = InvalidAuthenticationException.class))),
    @ApiResponse(responseCode = "400", description = "Invalid search parameters",
      content = @Content(schema = @Schema(implementation = FailedOperationException.class)))
  })
  @PreAuthorize("hasAnyRole('USER', 'ADMINISTRATOR', 'SUPER_ADMINISTRATOR')")
  @GetMapping(value = "/chat-space-membership-with-user")
  public MutualChatSpaceMembershipSearchResult findChatSpacesMembershipWithAnotherUser(
      @Parameter(description = "Search criteria for co-membership chat space", required = true)
        @SearchParam final ChatSpaceSearchRequest searchRequest,
      @Parameter(hidden = true)
        @AuthenticationPrincipal final RegisteredUser user) {
    return chatSpaceSearchService.findChatSpacesMembershipWithAnotherUser(searchRequest, user);
  }
}
