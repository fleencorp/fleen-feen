package com.fleencorp.feen.bookmark.controller;

import com.fleencorp.base.resolver.SearchParam;
import com.fleencorp.feen.bookmark.model.dto.BookmarkDto;
import com.fleencorp.feen.bookmark.model.request.search.BookmarkSearchRequest;
import com.fleencorp.feen.bookmark.model.response.BookmarkCreateResponse;
import com.fleencorp.feen.bookmark.model.search.BookmarkSearchResult;
import com.fleencorp.feen.bookmark.service.BookmarkSearchService;
import com.fleencorp.feen.bookmark.service.BookmarkService;
import com.fleencorp.feen.chat.space.exception.core.ChatSpaceNotFoundException;
import com.fleencorp.feen.common.exception.FailedOperationException;
import com.fleencorp.feen.shared.security.RegisteredUser;
import com.fleencorp.feen.softask.exception.core.SoftAskNotFoundException;
import com.fleencorp.feen.softask.exception.core.SoftAskReplyNotFoundException;
import com.fleencorp.feen.stream.exception.core.StreamNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/bookmark")
public class BookmarkController {

  private final BookmarkService bookmarkService;
  private final BookmarkSearchService bookmarkSearchService;

  public BookmarkController(
      final BookmarkService bookmarkService,
      final BookmarkSearchService bookmarkSearchService) {
    this.bookmarkService = bookmarkService;
    this.bookmarkSearchService = bookmarkSearchService;
  }

  @Operation(summary = "Bookmark a entity",
    description = "Creates a bookmark for a specific entity. Requires full user authentication.")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Successfully bookmarked the chat",
      content = @Content(schema = @Schema(implementation = BookmarkCreateResponse.class))),
    @ApiResponse(responseCode = "404", description = "Chat space not found",
      content = @Content(schema = @Schema(implementation = ChatSpaceNotFoundException.class))),
    @ApiResponse(responseCode = "404", description = "Soft Ask not found",
      content = @Content(schema = @Schema(implementation = SoftAskNotFoundException.class))),
    @ApiResponse(responseCode = "404", description = "Soft Ask Reply not found",
      content = @Content(schema = @Schema(implementation = SoftAskReplyNotFoundException.class))),
    @ApiResponse(responseCode = "404", description = "Stream not found",
      content = @Content(schema = @Schema(implementation = StreamNotFoundException.class))),
    @ApiResponse(responseCode = "400", description = "Failed operation",
      content = @Content(schema = @Schema(implementation = FailedOperationException.class)))
  })
  @PreAuthorize("isFullyAuthenticated()")
  @PostMapping(value = "")
  public BookmarkCreateResponse bookmark(
    @Parameter(description = "Bookmark details", required = true)
    @Valid @RequestBody final BookmarkDto bookmarkDto,
    @Parameter(hidden = true)
    @AuthenticationPrincipal final RegisteredUser user) {
    return bookmarkService.bookmark(bookmarkDto, user);
  }

  @Operation(summary = "Find bookmarks",
    description = "Searches for and retrieves a list of bookmarks based on specified search criteria.")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Successfully found bookmarks",
      content = @Content(schema = @Schema(implementation = BookmarkSearchResult.class)))
  })
  @GetMapping(value = "/entries")
  public BookmarkSearchResult findBookmarks(
    @Parameter(description = "Search criteria for bookmarks")
      @SearchParam final BookmarkSearchRequest searchRequest,
    @Parameter(hidden = true)
      @AuthenticationPrincipal final RegisteredUser user) {
    return bookmarkSearchService.findBookmarks(searchRequest, user);
  }
}
