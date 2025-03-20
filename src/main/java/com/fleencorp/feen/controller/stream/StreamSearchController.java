package com.fleencorp.feen.controller.stream;

import com.fleencorp.base.resolver.SearchParam;
import com.fleencorp.feen.constant.stream.StreamTimeType;
import com.fleencorp.feen.constant.stream.StreamType;
import com.fleencorp.feen.exception.auth.InvalidAuthenticationException;
import com.fleencorp.feen.exception.base.FailedOperationException;
import com.fleencorp.feen.exception.stream.FleenStreamNotFoundException;
import com.fleencorp.feen.exception.stream.core.StreamNotCreatedByUserException;
import com.fleencorp.feen.model.request.search.calendar.EventSearchRequest;
import com.fleencorp.feen.model.request.search.stream.StreamAttendeeSearchRequest;
import com.fleencorp.feen.model.request.search.stream.StreamSearchRequest;
import com.fleencorp.feen.model.response.stream.base.RetrieveStreamResponse;
import com.fleencorp.feen.model.search.join.RequestToJoinSearchResult;
import com.fleencorp.feen.model.search.stream.attendee.StreamAttendeeSearchResult;
import com.fleencorp.feen.model.search.stream.common.StreamSearchResult;
import com.fleencorp.feen.model.security.FleenUser;
import com.fleencorp.feen.service.stream.attendee.StreamAttendeeService;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/stream/search")
public class StreamSearchController {

  private final StreamSearchService streamSearchService;
  private final StreamAttendeeService streamAttendeeService;

  public StreamSearchController(
      final StreamSearchService streamSearchService,
      final StreamAttendeeService streamAttendeeService) {
    this.streamSearchService = streamSearchService;
    this.streamAttendeeService = streamAttendeeService;
  }

  @Operation(summary = "Search public streams",
    description = "Searches for public streams based on the provided search criteria"
  )
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Successfully retrieved streams",
      content = @Content(schema = @Schema(implementation = StreamSearchResult.class))),
    @ApiResponse(responseCode = "400", description = "Invalid search parameters",
      content = @Content(schema = @Schema(implementation = FailedOperationException.class)))
  })
  @GetMapping(value = "")
  public StreamSearchResult findStreamsPublic(
      @Parameter(description = "Search criteria for streams", required = true)
      @SearchParam final StreamSearchRequest searchRequest,
      @Parameter(hidden = true)
      @AuthenticationPrincipal final FleenUser user) {
    searchRequest.setStreamType(StreamType.event());
    return streamSearchService.findStreamsPublic(searchRequest, user);
  }

  @Operation(summary = "Search streams by type and time",
    description = "Searches for streams based on the event type and time criteria"
  )
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Successfully retrieved streams",
      content = @Content(schema = @Schema(implementation = StreamSearchResult.class))),
    @ApiResponse(responseCode = "400", description = "Invalid search parameters",
      content = @Content(schema = @Schema(implementation = FailedOperationException.class)))
  })
  @GetMapping(value = "/type")
  public StreamSearchResult findStreams(
      @Parameter(description = "Search criteria for events", required = true)
      @SearchParam final EventSearchRequest searchRequest,
      @Parameter(description = "Type of stream time filter", required = true)
      final StreamTimeType streamTimeType) {
    searchRequest.setStreamType(StreamType.event());
    return streamSearchService.findStreamsPublic(searchRequest, streamTimeType);
  }

  @Operation(summary = "Get stream details",
    description = "Retrieves detailed information about a specific stream"
  )
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Successfully retrieved stream details",
      content = @Content(schema = @Schema(implementation = RetrieveStreamResponse.class))),
    @ApiResponse(responseCode = "404", description = "Stream not found",
      content = @Content(schema = @Schema(implementation = FleenStreamNotFoundException.class))),
    @ApiResponse(responseCode = "400", description = "Invalid stream ID",
      content = @Content(schema = @Schema(implementation = FailedOperationException.class)))
  })
  @GetMapping(value = "/detail/{streamId}")
  public RetrieveStreamResponse findStream(
      @Parameter(description = "ID of the stream to retrieve", required = true)
      @PathVariable(name = "streamId") final Long streamId,
      @Parameter(hidden = true)
      @AuthenticationPrincipal final FleenUser user) {
    return streamSearchService.retrieveStream(streamId, user);
  }

  @Operation(summary = "Get user's private streams",
    description = "Retrieves all streams owned by the authenticated user. Requires authentication."
  )
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Successfully retrieved user's streams",
      content = @Content(schema = @Schema(implementation = StreamSearchResult.class))),
    @ApiResponse(responseCode = "401", description = "User not authenticated",
      content = @Content(schema = @Schema(implementation = InvalidAuthenticationException.class))),
    @ApiResponse(responseCode = "400", description = "Invalid search parameters",
      content = @Content(schema = @Schema(implementation = FailedOperationException.class)))
  })
  @PreAuthorize("hasAnyRole('USER', 'ADMINISTRATOR', 'SUPER_ADMINISTRATOR')")
  @GetMapping(value = "/mine")
  public StreamSearchResult findStreamsPrivate(
      @Parameter(description = "Search criteria for streams", required = true)
      @SearchParam final StreamSearchRequest searchRequest,
      @Parameter(hidden = true)
      @AuthenticationPrincipal final FleenUser user) {
    searchRequest.setDefaultStreamType();
    return streamSearchService.findStreamsPrivate(searchRequest, user);
  }

  @Operation(summary = "Get details of user's stream",
    description = "Retrieves detailed information about a specific stream owned by the authenticated user. Requires authentication."
  )
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Successfully retrieved stream details",
      content = @Content(schema = @Schema(implementation = RetrieveStreamResponse.class))),
    @ApiResponse(responseCode = "401", description = "User not authenticated",
      content = @Content(schema = @Schema(implementation = InvalidAuthenticationException.class))),
    @ApiResponse(responseCode = "403", description = "User is not the owner of the stream",
      content = @Content(schema = @Schema(implementation = StreamNotCreatedByUserException.class))),
    @ApiResponse(responseCode = "404", description = "Stream not found",
      content = @Content(schema = @Schema(implementation = FleenStreamNotFoundException.class))),
    @ApiResponse(responseCode = "400", description = "Invalid stream ID",
      content = @Content(schema = @Schema(implementation = FailedOperationException.class)))
  })
  @PreAuthorize("hasAnyRole('USER', 'ADMINISTRATOR', 'SUPER_ADMINISTRATOR')")
  @GetMapping(value = "/mine/detail/{streamId}")
  public RetrieveStreamResponse findMyStream(
      @Parameter(description = "ID of the stream to retrieve", required = true)
      @PathVariable(name = "streamId") final Long streamId,
      @Parameter(hidden = true)
      @AuthenticationPrincipal final FleenUser user) {
    return streamSearchService.retrieveStream(streamId, user);
  }

  @Operation(summary = "Get stream attendees",
    description = "Retrieves a list of attendees for a specific stream"
  )
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Successfully retrieved stream attendees",
      content = @Content(schema = @Schema(implementation = StreamAttendeeSearchResult.class))),
    @ApiResponse(responseCode = "404", description = "Stream not found",
      content = @Content(schema = @Schema(implementation = FleenStreamNotFoundException.class))),
    @ApiResponse(responseCode = "400", description = "Invalid stream ID or search parameters",
      content = @Content(schema = @Schema(implementation = FailedOperationException.class)))
  })
  @GetMapping(value = "/attendees/{streamId}")
  public StreamAttendeeSearchResult getStreamAttendees(
      @Parameter(description = "ID of the stream to get attendees for", required = true)
      @PathVariable(name = "streamId") final Long streamId,
      @Parameter(description = "Search criteria for stream attendees", required = true)
      @SearchParam final StreamAttendeeSearchRequest streamAttendeeSearchRequest) {
    return streamAttendeeService.getStreamAttendees(streamId, streamAttendeeSearchRequest);
  }

  @Operation(summary = "Find stream attendees (alternative)",
    description = "Alternative endpoint to search for attendees of a specific stream"
  )
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Successfully retrieved stream attendees",
      content = @Content(schema = @Schema(implementation = StreamAttendeeSearchResult.class))),
    @ApiResponse(responseCode = "404", description = "Stream not found",
      content = @Content(schema = @Schema(implementation = FleenStreamNotFoundException.class))),
    @ApiResponse(responseCode = "400", description = "Invalid stream ID or search parameters",
      content = @Content(schema = @Schema(implementation = FailedOperationException.class)))
  })
  @GetMapping(value = "/attendees-2/{streamId}")
  public StreamAttendeeSearchResult findStreamAttendees(
      @Parameter(description = "ID of the stream to find attendees for", required = true)
      @PathVariable(name = "streamId") final Long streamId,
      @Parameter(description = "Search criteria for stream attendees", required = true)
      @SearchParam final StreamAttendeeSearchRequest searchRequest) {
    return streamAttendeeService.findStreamAttendees(streamId, searchRequest);
  }

  @Operation(summary = "Get streams attended by current user",
    description = "Retrieves all streams that the authenticated user has attended. Requires authentication."
  )
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Successfully retrieved attended streams",
      content = @Content(schema = @Schema(implementation = StreamSearchResult.class))),
    @ApiResponse(responseCode = "401", description = "User not authenticated",
      content = @Content(schema = @Schema(implementation = InvalidAuthenticationException.class))),
    @ApiResponse(responseCode = "400", description = "Invalid search parameters",
      content = @Content(schema = @Schema(implementation = FailedOperationException.class)))
  })
  @PreAuthorize("hasAnyRole('USER', 'ADMINISTRATOR', 'SUPER_ADMINISTRATOR')")
  @GetMapping(value = "/attended-by-me")
  public StreamSearchResult findStreamsAttendedByUser(
      @Parameter(description = "Search criteria for attended streams", required = true)
      @SearchParam final StreamSearchRequest searchRequest,
      @Parameter(hidden = true)
      @AuthenticationPrincipal final FleenUser user) {
    return streamSearchService.findStreamsAttendedByUser(searchRequest, user);
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
  @GetMapping(value = "/attended-with-user")
  public StreamSearchResult findEventsAttendedWithAnotherUser(
      @Parameter(description = "Search criteria for co-attended streams", required = true)
      @SearchParam final StreamSearchRequest searchRequest,
      @Parameter(hidden = true)
      @AuthenticationPrincipal final FleenUser user) {
    return streamSearchService.findStreamsAttendedWithAnotherUser(searchRequest, user);
  }

  @Operation(summary = "Get stream join requests",
    description = "Retrieves all pending requests to join a specific stream. Requires authentication and appropriate permissions."
  )
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Successfully retrieved join requests",
      content = @Content(schema = @Schema(implementation = RequestToJoinSearchResult.class))),
    @ApiResponse(responseCode = "401", description = "User not authenticated",
      content = @Content(schema = @Schema(implementation = InvalidAuthenticationException.class))),
    @ApiResponse(responseCode = "403", description = "User is not authorized to view join requests",
      content = @Content(schema = @Schema(implementation = StreamNotCreatedByUserException.class))),
    @ApiResponse(responseCode = "404", description = "Stream not found",
      content = @Content(schema = @Schema(implementation = FleenStreamNotFoundException.class))),
    @ApiResponse(responseCode = "400", description = "Invalid stream ID or search parameters",
      content = @Content(schema = @Schema(implementation = FailedOperationException.class)))
  })
  @PreAuthorize("hasAnyRole('USER', 'ADMINISTRATOR', 'SUPER_ADMINISTRATOR')")
  @GetMapping(value = "/attendees/request-to-join/{streamId}")
  public RequestToJoinSearchResult findAttendeesRequestToJoin(
      @Parameter(description = "ID of the stream to get join requests for", required = true)
      @PathVariable(name = "streamId") final Long streamId,
      @Parameter(hidden = true)
      @AuthenticationPrincipal final FleenUser user,
      @Parameter(description = "Search criteria for join requests", required = true)
      @SearchParam final StreamAttendeeSearchRequest streamAttendeeSearchRequest) {
    return streamAttendeeService.getAttendeeRequestsToJoinStream(streamId, streamAttendeeSearchRequest, user);
  }
}
