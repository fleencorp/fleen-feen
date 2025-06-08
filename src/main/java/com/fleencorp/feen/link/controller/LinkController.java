package com.fleencorp.feen.link.controller;

import com.fleencorp.base.resolver.SearchParam;
import com.fleencorp.feen.exception.auth.InvalidAuthenticationException;
import com.fleencorp.feen.exception.base.FailedOperationException;
import com.fleencorp.feen.exception.chat.space.ChatSpaceNotFoundException;
import com.fleencorp.feen.exception.stream.StreamNotFoundException;
import com.fleencorp.feen.link.model.dto.DeleteLinkDto;
import com.fleencorp.feen.link.model.dto.UpdateLinkDto;
import com.fleencorp.feen.link.model.dto.UpdateStreamMusicLinkDto;
import com.fleencorp.feen.model.request.search.LinkSearchRequest;
import com.fleencorp.feen.link.model.response.DeleteLinkResponse;
import com.fleencorp.feen.link.model.response.UpdateLinkResponse;
import com.fleencorp.feen.link.model.response.UpdateStreamMusicLinkResponse;
import com.fleencorp.feen.link.model.response.availability.GetAvailableLinkTypeResponse;
import com.fleencorp.feen.link.model.response.availability.GetAvailableMusicLinkTypeResponse;
import com.fleencorp.feen.link.model.search.LinkSearchResult;
import com.fleencorp.feen.model.security.FleenUser;
import com.fleencorp.feen.link.service.LinkService;
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
@RequestMapping(value = "/api/link")
public class LinkController {

  private final LinkService linkService;

  public LinkController(final LinkService linkService) {
    this.linkService = linkService;
  }

  @Operation(summary = "Retrieve available link types",
    description = "Returns a list of supported link types along with their display values and expected formats."
  )
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Available link types retrieved successfully",
      content = @Content(schema = @Schema(implementation = GetAvailableLinkTypeResponse.class)))
  })
  @GetMapping(value = "/link-types")
  public GetAvailableLinkTypeResponse getAvailableLinkTypes() {
    return linkService.getAvailableLinkTypes();
  }

  @Operation(summary = "Retrieve available music link types",
    description = "Returns a list of supported music link types along with their display values and expected formats."
  )
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Available music link types retrieved successfully",
      content = @Content(schema = @Schema(implementation = GetAvailableLinkTypeResponse.class)))
  })
  @GetMapping(value = "/music-link-types")
  public GetAvailableMusicLinkTypeResponse getAvailableMusicLinkTypes() {
    return linkService.getAvailableMusicLinkType();
  }

  @Operation(summary = "Search for links",
    description = "Searches for links based on the specified criteria and returns a paginated list of results."
  )
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Link search completed successfully",
      content = @Content(schema = @Schema(implementation = LinkSearchResult.class))),
    @ApiResponse(responseCode = "401", description = "User not authenticated",
      content = @Content(schema = @Schema(implementation = InvalidAuthenticationException.class)))
  })
  @GetMapping(value = "")
  public LinkSearchResult findLinks(
      @Parameter(description = "Search criteria and pagination parameters", required = true)
        @SearchParam final LinkSearchRequest linkSearchRequest,
      @Parameter(hidden = true)
        @AuthenticationPrincipal final FleenUser user) {
    return linkService.findLinks(linkSearchRequest, user);
  }

  @Operation(summary = "Update an existing link",
    description = "Updates the details of an existing link."
  )
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Link updated successfully",
      content = @Content(schema = @Schema(implementation = UpdateLinkResponse.class))),
    @ApiResponse(responseCode = "400", description = "Invalid update parameters",
      content = @Content(schema = @Schema(implementation = FailedOperationException.class))),
    @ApiResponse(responseCode = "401", description = "User not authenticated",
      content = @Content(schema = @Schema(implementation = InvalidAuthenticationException.class))),
    @ApiResponse(responseCode = "404", description = "Chat space not found",
      content = @Content(schema = @Schema(implementation = ChatSpaceNotFoundException.class)))
  })
  @PutMapping(value = "/update")
  public UpdateLinkResponse updateLink(
      @Parameter(description = "Link details to update", required = true)
        @Valid @RequestBody final UpdateLinkDto updateLinkDto,
      @Parameter(hidden = true)
        @AuthenticationPrincipal final FleenUser user) {
    return linkService.updateLink(updateLinkDto, user);
  }

  @Operation(summary = "Delete a link",
    description = "Deletes a specified link."
  )
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Link deleted successfully",
      content = @Content(schema = @Schema(implementation = DeleteLinkResponse.class))),
    @ApiResponse(responseCode = "400", description = "Invalid delete parameters",
      content = @Content(schema = @Schema(implementation = FailedOperationException.class))),
    @ApiResponse(responseCode = "401", description = "User not authenticated",
      content = @Content(schema = @Schema(implementation = InvalidAuthenticationException.class))),
    @ApiResponse(responseCode = "404", description = "Chat space not found",
      content = @Content(schema = @Schema(implementation = ChatSpaceNotFoundException.class)))
  })
  @PutMapping(value = "/delete")
  public DeleteLinkResponse deleteLink(
      @Parameter(description = "Details of the link to delete", required = true)
        @Valid @RequestBody final DeleteLinkDto deleteLinkDto,
      @Parameter(hidden = true)
        @AuthenticationPrincipal final FleenUser user) {
    return linkService.deleteLink(deleteLinkDto, user);
  }

  @Operation(summary = "Update the streaming music link",
    description = "Updates the link for streaming music."
  )
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Streaming music link updated successfully",
      content = @Content(schema = @Schema(implementation = UpdateStreamMusicLinkResponse.class))),
    @ApiResponse(responseCode = "400", description = "Invalid update parameters",
      content = @Content(schema = @Schema(implementation = FailedOperationException.class))),
    @ApiResponse(responseCode = "401", description = "User not authenticated",
      content = @Content(schema = @Schema(implementation = InvalidAuthenticationException.class))),
    @ApiResponse(responseCode = "404", description = "Fleen stream not found",
      content = @Content(schema = @Schema(implementation = StreamNotFoundException.class)))
  })
  @PutMapping(value = "/update-music-link")
  public UpdateStreamMusicLinkResponse updateStreamMusicLink(
      @Parameter(description = "Details for updating the music stream link", required = true)
        @Valid @RequestBody final UpdateStreamMusicLinkDto updateStreamMusicLinkDto,
      @Parameter(hidden = true)
        @AuthenticationPrincipal final FleenUser user) {
    return linkService.updateStreamMusicLink(updateStreamMusicLinkDto, user);
  }
}
