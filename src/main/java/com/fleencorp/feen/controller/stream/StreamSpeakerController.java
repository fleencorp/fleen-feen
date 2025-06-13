package com.fleencorp.feen.controller.stream;

import com.fleencorp.base.resolver.SearchParam;
import com.fleencorp.feen.exception.base.FailedOperationException;
import com.fleencorp.feen.exception.stream.StreamNotFoundException;
import com.fleencorp.feen.exception.stream.core.StreamNotCreatedByUserException;
import com.fleencorp.feen.exception.stream.speaker.OrganizerOfStreamCannotBeRemovedAsSpeakerException;
import com.fleencorp.feen.model.dto.stream.base.RemoveStreamSpeakerDto;
import com.fleencorp.feen.model.dto.stream.speaker.MarkAsStreamSpeakerDto;
import com.fleencorp.feen.model.dto.stream.speaker.UpdateStreamSpeakerDto;
import com.fleencorp.feen.model.request.search.stream.StreamSpeakerSearchRequest;
import com.fleencorp.feen.model.response.stream.speaker.MarkAsStreamSpeakerResponse;
import com.fleencorp.feen.model.response.stream.speaker.RemoveStreamSpeakerResponse;
import com.fleencorp.feen.model.response.stream.speaker.UpdateStreamSpeakerResponse;
import com.fleencorp.feen.model.search.stream.speaker.StreamSpeakerSearchResult;
import com.fleencorp.feen.service.stream.speaker.StreamSpeakerService;
import com.fleencorp.feen.user.exception.auth.InvalidAuthenticationException;
import com.fleencorp.feen.user.model.security.RegisteredUser;
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
@RequestMapping(value = "/api/stream/speaker")
public class StreamSpeakerController {

  private final StreamSpeakerService streamSpeakerService;

  public StreamSpeakerController(final StreamSpeakerService streamSpeakerService) {
    this.streamSpeakerService = streamSpeakerService;
  }

  @Operation(summary = "Search stream speakers",
    description = "Searches for speakers in a specific stream. Requires authentication."
  )
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Successfully retrieved stream speakers",
      content = @Content(schema = @Schema(implementation = StreamSpeakerSearchResult.class))),
    @ApiResponse(responseCode = "401", description = "User not authenticated",
      content = @Content(schema = @Schema(implementation = InvalidAuthenticationException.class))),
    @ApiResponse(responseCode = "404", description = "Stream not found",
      content = @Content(schema = @Schema(implementation = StreamNotFoundException.class))),
    @ApiResponse(responseCode = "400", description = "Invalid stream ID or search parameters",
      content = @Content(schema = @Schema(implementation = FailedOperationException.class)))
  })
  @PreAuthorize("isFullyAuthenticated()")
  @GetMapping(value = "/search/{streamId}")
  public StreamSpeakerSearchResult findStreamSpeakers(
      @Parameter(description = "ID of the stream to search speakers in", required = true)
      @PathVariable(name = "streamId") final Long streamId,
      @Parameter(description = "Search criteria for stream speakers", required = true)
      @SearchParam final StreamSpeakerSearchRequest searchRequest,
      @Parameter(hidden = true)
      @AuthenticationPrincipal final RegisteredUser user) {
    return streamSpeakerService.findSpeakers(streamId, searchRequest, user);
  }

  @Operation(summary = "Get stream speakers (alternative)",
    description = "Alternative endpoint to retrieve speakers of a specific stream. Requires authentication."
  )
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Successfully retrieved stream speakers",
      content = @Content(schema = @Schema(implementation = StreamSpeakerSearchResult.class))),
    @ApiResponse(responseCode = "401", description = "User not authenticated",
      content = @Content(schema = @Schema(implementation = InvalidAuthenticationException.class))),
    @ApiResponse(responseCode = "404", description = "Stream not found",
      content = @Content(schema = @Schema(implementation = StreamNotFoundException.class))),
    @ApiResponse(responseCode = "400", description = "Invalid stream ID or search parameters",
      content = @Content(schema = @Schema(implementation = FailedOperationException.class)))
  })
  @PreAuthorize("isFullyAuthenticated()")
  @GetMapping(value = "/entries/{streamId}")
  public StreamSpeakerSearchResult getStreamSpeakers(
      @Parameter(description = "ID of the stream to get speakers from", required = true)
      @PathVariable(name = "streamId") final Long streamId,
      @Parameter(description = "Search criteria for stream speakers", required = true)
      @SearchParam final StreamSpeakerSearchRequest searchRequest,
      @Parameter(hidden = true)
      @AuthenticationPrincipal final RegisteredUser user) {
    return streamSpeakerService.findStreamSpeakers(streamId, searchRequest, user);
  }

  @Operation(summary = "Mark user as stream speaker",
    description = "Marks a user as a speaker for a specific stream. Requires authentication and appropriate permissions."
  )
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Successfully marked user as speaker",
      content = @Content(schema = @Schema(implementation = MarkAsStreamSpeakerResponse.class))),
    @ApiResponse(responseCode = "401", description = "User not authenticated",
      content = @Content(schema = @Schema(implementation = InvalidAuthenticationException.class))),
    @ApiResponse(responseCode = "403", description = "User not authorized to manage speakers",
      content = @Content(schema = @Schema(implementation = StreamNotCreatedByUserException.class))),
    @ApiResponse(responseCode = "404", description = "Stream not found",
      content = @Content(schema = @Schema(implementation = StreamNotFoundException.class))),
    @ApiResponse(responseCode = "400", description = "Invalid request parameters",
      content = @Content(schema = @Schema(implementation = FailedOperationException.class)))
  })
  @PreAuthorize("isFullyAuthenticated()")
  @PostMapping(value = "/mark-as-speaker/{streamId}")
  public MarkAsStreamSpeakerResponse markAsStreamSpeaker(
      @Parameter(description = "ID of the stream to add speaker to", required = true)
      @PathVariable(name = "streamId") final Long streamId,
      @Parameter(description = "Details of the user to be marked as speaker", required = true)
      @Valid @RequestBody final MarkAsStreamSpeakerDto markAsStreamSpeakerDto,
      @Parameter(hidden = true)
      @AuthenticationPrincipal final RegisteredUser user) {
    return streamSpeakerService.markAsSpeaker(streamId, markAsStreamSpeakerDto, user);
  }

  @Operation(summary = "Update stream speaker details",
    description = "Updates the details of a stream speaker. Requires authentication and appropriate permissions."
  )
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Successfully updated speaker details",
      content = @Content(schema = @Schema(implementation = UpdateStreamSpeakerResponse.class))),
    @ApiResponse(responseCode = "401", description = "User not authenticated",
      content = @Content(schema = @Schema(implementation = InvalidAuthenticationException.class))),
    @ApiResponse(responseCode = "403", description = "User not authorized to manage speakers",
      content = @Content(schema = @Schema(implementation = StreamNotCreatedByUserException.class))),
    @ApiResponse(responseCode = "404", description = "Stream not found",
      content = @Content(schema = @Schema(implementation = StreamNotFoundException.class))),
    @ApiResponse(responseCode = "400", description = "Invalid request parameters",
      content = @Content(schema = @Schema(implementation = FailedOperationException.class)))
  })
  @PreAuthorize("isFullyAuthenticated()")
  @PutMapping(value = "/update/{streamId}")
  public UpdateStreamSpeakerResponse updateStreamSpeaker(
      @Parameter(description = "ID of the stream to update speaker in", required = true)
      @PathVariable(name = "streamId") final Long streamId,
      @Parameter(description = "Updated speaker details", required = true)
      @Valid @RequestBody final UpdateStreamSpeakerDto updateStreamSpeakerDto,
      @Parameter(hidden = true)
      @AuthenticationPrincipal final RegisteredUser user) {
    return streamSpeakerService.updateSpeakers(streamId, updateStreamSpeakerDto, user);
  }

  @Operation(summary = "Remove stream speaker",
    description = "Removes a speaker from a stream. Cannot remove the stream organizer. Requires authentication and appropriate permissions."
  )
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Successfully removed speaker",
      content = @Content(schema = @Schema(implementation = RemoveStreamSpeakerResponse.class))),
    @ApiResponse(responseCode = "401", description = "User not authenticated",
      content = @Content(schema = @Schema(implementation = InvalidAuthenticationException.class))),
    @ApiResponse(responseCode = "403", description = "User not authorized or attempting to remove organizer",
      content = @Content(schema = @Schema(oneOf = {
        StreamNotCreatedByUserException.class,
        OrganizerOfStreamCannotBeRemovedAsSpeakerException.class
      }))),
    @ApiResponse(responseCode = "404", description = "Stream not found",
      content = @Content(schema = @Schema(implementation = StreamNotFoundException.class))),
    @ApiResponse(responseCode = "400", description = "Invalid request parameters",
      content = @Content(schema = @Schema(implementation = FailedOperationException.class)))
  })
  @PreAuthorize("isFullyAuthenticated()")
  @PutMapping(value = "/remove/{streamId}")
  public RemoveStreamSpeakerResponse removeStreamSpeaker(
      @Parameter(description = "ID of the stream to remove speaker from", required = true)
      @PathVariable(name = "streamId") final Long streamId,
      @Parameter(description = "Details of the speaker to remove", required = true)
      @Valid @RequestBody final RemoveStreamSpeakerDto removeStreamSpeakerDto,
      @Parameter(hidden = true)
      @AuthenticationPrincipal final RegisteredUser user) {
    return streamSpeakerService.removeSpeakers(streamId, removeStreamSpeakerDto, user);
  }
}
