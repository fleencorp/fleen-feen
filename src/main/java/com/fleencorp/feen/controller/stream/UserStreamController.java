package com.fleencorp.feen.controller.stream;

import com.fleencorp.base.resolver.SearchParam;
import com.fleencorp.feen.user.exception.auth.InvalidAuthenticationException;
import com.fleencorp.feen.exception.base.FailedOperationException;
import com.fleencorp.feen.exception.stream.StreamNotFoundException;
import com.fleencorp.feen.exception.stream.core.CannotCancelOrDeleteOngoingStreamException;
import com.fleencorp.feen.exception.stream.core.StreamAlreadyCanceledException;
import com.fleencorp.feen.exception.stream.core.StreamAlreadyHappenedException;
import com.fleencorp.feen.exception.stream.core.StreamNotCreatedByUserException;
import com.fleencorp.feen.model.dto.event.AddNewStreamAttendeeDto;
import com.fleencorp.feen.model.dto.stream.attendance.ProcessAttendeeRequestToJoinStreamDto;
import com.fleencorp.feen.model.dto.stream.base.*;
import com.fleencorp.feen.model.request.search.stream.type.StreamTypeSearchRequest;
import com.fleencorp.feen.model.response.stream.attendance.ProcessAttendeeRequestToJoinStreamResponse;
import com.fleencorp.feen.model.response.stream.base.*;
import com.fleencorp.feen.model.response.stream.common.AddNewStreamAttendeeResponse;
import com.fleencorp.feen.model.response.stream.statistic.TotalStreamsAttendedByUserResponse;
import com.fleencorp.feen.model.response.stream.statistic.TotalStreamsCreatedByUserResponse;
import com.fleencorp.feen.user.security.RegisteredUser;
import com.fleencorp.feen.service.stream.common.CommonStreamJoinService;
import com.fleencorp.feen.service.stream.common.CommonStreamService;
import com.fleencorp.feen.service.stream.event.EventOperationsService;
import com.fleencorp.feen.service.stream.search.StreamSearchService;
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
@RequestMapping(value = "/api/stream")
@PreAuthorize("hasAnyRole('ADMINISTRATOR', 'SUPER_ADMINISTRATOR', 'USER')")
public class UserStreamController {

  private final CommonStreamService commonStreamService;
  private final CommonStreamJoinService commonStreamJoinService;
  private final EventOperationsService eventOperationsService;
  private final StreamSearchService streamSearchService;

  public UserStreamController(
      final CommonStreamService commonStreamService,
      final CommonStreamJoinService commonStreamJoinService,
      final EventOperationsService eventOperationsService,
      final StreamSearchService streamSearchService) {
    this.commonStreamService = commonStreamService;
    this.commonStreamJoinService = commonStreamJoinService;
    this.eventOperationsService = eventOperationsService;
    this.streamSearchService = streamSearchService;
  }

  @Operation(summary = "Update stream details",
    description = "Updates the details of a stream. Only the stream owner can perform this operation. Requires authentication."
  )
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Successfully updated stream details",
      content = @Content(schema = @Schema(implementation = UpdateStreamResponse.class))),
    @ApiResponse(responseCode = "401", description = "User not authenticated",
      content = @Content(schema = @Schema(implementation = InvalidAuthenticationException.class))),
    @ApiResponse(responseCode = "403", description = "User is not the stream owner",
      content = @Content(schema = @Schema(implementation = StreamNotCreatedByUserException.class))),
    @ApiResponse(responseCode = "404", description = "Stream not found",
      content = @Content(schema = @Schema(implementation = StreamNotFoundException.class))),
    @ApiResponse(responseCode = "400", description = "Invalid request parameters",
      content = @Content(schema = @Schema(implementation = FailedOperationException.class)))
  })
  @PutMapping(value = "/update/{streamId}")
  public UpdateStreamResponse updateStream(
      @Parameter(description = "ID of the stream to update", required = true)
      @PathVariable(name = "streamId") final Long streamId,
      @Parameter(description = "Updated stream details", required = true)
      @Valid @RequestBody final UpdateStreamDto updateStreamDto,
      @Parameter(hidden = true)
      @AuthenticationPrincipal final RegisteredUser user) {
    return commonStreamService.updateStream(streamId, updateStreamDto, user);
  }

  @Operation(summary = "Update other details of a stream",
    description = "Updates various non-core details of a specific stream, such as its title, description, or tags. " +
      "This operation can only be performed by the user who created the stream, and only if the stream " +
      "has not already happened or been canceled."
  )
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Stream details updated successfully",
      content = @Content(schema = @Schema(implementation = UpdateStreamResponse.class))),
    @ApiResponse(responseCode = "400", description = "Invalid update parameters",
      content = @Content(schema = @Schema(implementation = FailedOperationException.class))),
    @ApiResponse(responseCode = "401", description = "User not authenticated",
      content = @Content(schema = @Schema(implementation = InvalidAuthenticationException.class))),
    @ApiResponse(responseCode = "403", description = "User not authorized to update this stream",
      content = @Content(schema = @Schema(implementation = StreamNotCreatedByUserException.class))),
    @ApiResponse(responseCode = "404", description = "Stream not found",
      content = @Content(schema = @Schema(implementation = StreamNotFoundException.class))),
    @ApiResponse(responseCode = "409", description = "Stream has already happened or been canceled",
      content = @Content(schema = @Schema(oneOf = {StreamAlreadyHappenedException.class, StreamAlreadyCanceledException.class})))
  })
  @PutMapping(value = "/update-other-detail/{streamId}")
  public UpdateStreamResponse updateStreamOtherDetails(
      @Parameter(description = "ID of the stream to update", required = true)
        @PathVariable(name = "streamId") final Long streamId,
      @Parameter(description = "Updated stream details", required = true)
        @Valid @RequestBody final UpdateStreamOtherDetailDto updateStreamOtherDetailDto,
      @Parameter(hidden = true)
        @AuthenticationPrincipal final RegisteredUser user) {
    return commonStreamService.updateStreamOtherDetails(streamId, updateStreamOtherDetailDto, user);
  }

  @Operation(summary = "Cancel stream",
    description = "Cancels a stream. Cannot cancel ongoing streams or already canceled streams. Only the stream owner can perform this operation. Requires authentication."
  )
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Successfully canceled the stream",
      content = @Content(schema = @Schema(implementation = CancelStreamResponse.class))),
    @ApiResponse(responseCode = "401", description = "User not authenticated",
      content = @Content(schema = @Schema(implementation = InvalidAuthenticationException.class))),
    @ApiResponse(responseCode = "403", description = "User is not the stream owner, stream is ongoing, or already canceled",
      content = @Content(schema = @Schema(oneOf = {
        StreamNotCreatedByUserException.class,
        CannotCancelOrDeleteOngoingStreamException.class,
        StreamAlreadyCanceledException.class
      }))),
    @ApiResponse(responseCode = "404", description = "Stream not found",
      content = @Content(schema = @Schema(implementation = StreamNotFoundException.class))),
    @ApiResponse(responseCode = "400", description = "Invalid request parameters",
      content = @Content(schema = @Schema(implementation = FailedOperationException.class)))
  })
  @PutMapping(value = "/cancel/{streamId}")
  public CancelStreamResponse cancelStream(
      @Parameter(description = "ID of the stream to cancel", required = true)
      @PathVariable(name = "streamId") final Long streamId,
      @Parameter(description = "Stream cancellation details", required = true)
      @Valid @RequestBody final CancelStreamDto cancelStreamDto,
      @Parameter(hidden = true)
      @AuthenticationPrincipal final RegisteredUser user) {
    return commonStreamService.cancelStream(streamId, cancelStreamDto, user);
  }

  @Operation(summary = "Delete stream",
    description = "Deletes a stream. Cannot delete ongoing streams. Only the stream owner can perform this operation. Requires authentication."
  )
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Successfully deleted the stream",
      content = @Content(schema = @Schema(implementation = DeleteStreamResponse.class))),
    @ApiResponse(responseCode = "401", description = "User not authenticated",
      content = @Content(schema = @Schema(implementation = InvalidAuthenticationException.class))),
    @ApiResponse(responseCode = "403", description = "User is not the stream owner or stream is ongoing",
      content = @Content(schema = @Schema(oneOf = {
        StreamNotCreatedByUserException.class,
        CannotCancelOrDeleteOngoingStreamException.class
      }))),
    @ApiResponse(responseCode = "404", description = "Stream not found",
      content = @Content(schema = @Schema(implementation = StreamNotFoundException.class))),
    @ApiResponse(responseCode = "400", description = "Invalid request parameters",
      content = @Content(schema = @Schema(implementation = FailedOperationException.class)))
  })
  @PutMapping(value = "/delete/{streamId}")
  public DeleteStreamResponse deleteStream(
      @Parameter(description = "ID of the stream to delete", required = true)
      @PathVariable(name = "streamId") final Long streamId,
      @Parameter(description = "Stream deletion details", required = true)
      @Valid @RequestBody final DeleteStreamDto deleteStreamDto,
      @Parameter(hidden = true)
      @AuthenticationPrincipal final RegisteredUser user) {
    return commonStreamService.deleteStream(streamId, deleteStreamDto, user);
  }

  @Operation(summary = "Process stream join request",
    description = "Processes (approve/reject) a request to join a stream. Only the stream owner can perform this operation. Requires authentication."
  )
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Successfully processed join request",
      content = @Content(schema = @Schema(implementation = ProcessAttendeeRequestToJoinStreamResponse.class))),
    @ApiResponse(responseCode = "401", description = "User not authenticated",
      content = @Content(schema = @Schema(implementation = InvalidAuthenticationException.class))),
    @ApiResponse(responseCode = "403", description = "User is not the stream owner",
      content = @Content(schema = @Schema(implementation = StreamNotCreatedByUserException.class))),
    @ApiResponse(responseCode = "404", description = "Stream not found",
      content = @Content(schema = @Schema(implementation = StreamNotFoundException.class))),
    @ApiResponse(responseCode = "400", description = "Invalid request parameters",
      content = @Content(schema = @Schema(implementation = FailedOperationException.class)))
  })
  @PutMapping(value = "/process-join-request/{streamId}")
  public ProcessAttendeeRequestToJoinStreamResponse processAttendeeRequestToJoinStream(
      @Parameter(description = "ID of the stream to process join request for", required = true)
      @PathVariable(name = "streamId") final Long streamId,
      @Parameter(description = "Join request processing details", required = true)
      @Valid @RequestBody final ProcessAttendeeRequestToJoinStreamDto processAttendeeRequestToJoinStreamDto,
      @Parameter(hidden = true)
      @AuthenticationPrincipal final RegisteredUser user) {
    return commonStreamJoinService.processAttendeeRequestToJoinStream(streamId, processAttendeeRequestToJoinStreamDto, user);
  }

  @Operation(summary = "Reschedule stream",
    description = "Changes the scheduled time of a stream. Only the stream owner can perform this operation. Cannot reschedule ongoing or past streams. Requires authentication."
  )
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Successfully rescheduled the stream",
      content = @Content(schema = @Schema(implementation = RescheduleStreamResponse.class))),
    @ApiResponse(responseCode = "401", description = "User not authenticated",
      content = @Content(schema = @Schema(implementation = InvalidAuthenticationException.class))),
    @ApiResponse(responseCode = "403", description = "User is not the stream owner or stream cannot be rescheduled",
      content = @Content(schema = @Schema(oneOf = {
        StreamNotCreatedByUserException.class,
        StreamAlreadyHappenedException.class,
        CannotCancelOrDeleteOngoingStreamException.class
      }))),
    @ApiResponse(responseCode = "404", description = "Stream not found",
      content = @Content(schema = @Schema(implementation = StreamNotFoundException.class))),
    @ApiResponse(responseCode = "400", description = "Invalid request parameters",
      content = @Content(schema = @Schema(implementation = FailedOperationException.class)))
  })
  @PutMapping(value = "/reschedule/{streamId}")
  public RescheduleStreamResponse rescheduleStream(
      @Parameter(description = "ID of the stream to reschedule", required = true)
      @PathVariable(name = "streamId") final Long streamId,
      @Parameter(description = "New schedule details", required = true)
      @Valid @RequestBody final RescheduleStreamDto rescheduleStreamDto,
      @Parameter(hidden = true)
      @AuthenticationPrincipal final RegisteredUser user) {
    return commonStreamService.rescheduleStream(streamId, rescheduleStreamDto, user);
  }

  @Operation(summary = "Update stream visibility",
    description = "Changes the visibility settings of a stream (public/private). Only the stream owner can perform this operation. Requires authentication."
  )
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Successfully updated stream visibility",
      content = @Content(schema = @Schema(implementation = UpdateStreamVisibilityResponse.class))),
    @ApiResponse(responseCode = "401", description = "User not authenticated",
      content = @Content(schema = @Schema(implementation = InvalidAuthenticationException.class))),
    @ApiResponse(responseCode = "403", description = "User is not the stream owner",
      content = @Content(schema = @Schema(implementation = StreamNotCreatedByUserException.class))),
    @ApiResponse(responseCode = "404", description = "Stream not found",
      content = @Content(schema = @Schema(implementation = StreamNotFoundException.class))),
    @ApiResponse(responseCode = "400", description = "Invalid request parameters",
      content = @Content(schema = @Schema(implementation = FailedOperationException.class)))
  })
  @PutMapping(value = "/update-visibility/{streamId}")
  public UpdateStreamVisibilityResponse updateStreamVisibility(
      @Parameter(description = "ID of the stream to update visibility", required = true)
      @PathVariable(name = "streamId") final Long streamId,
      @Parameter(description = "New visibility settings", required = true)
      @Valid @RequestBody final UpdateStreamVisibilityDto updateStreamVisibilityDto,
      @Parameter(hidden = true)
      @AuthenticationPrincipal final RegisteredUser user) {
    return commonStreamService.updateStreamVisibility(streamId, updateStreamVisibilityDto, user);
  }

  @Operation(summary = "Add attendee to stream",
    description = "Adds a new attendee to a stream. Only works for event-type streams. Requires authentication."
  )
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Successfully added attendee to stream",
      content = @Content(schema = @Schema(implementation = AddNewStreamAttendeeResponse.class))),
    @ApiResponse(responseCode = "401", description = "User not authenticated",
      content = @Content(schema = @Schema(implementation = InvalidAuthenticationException.class))),
    @ApiResponse(responseCode = "404", description = "Stream not found",
      content = @Content(schema = @Schema(implementation = StreamNotFoundException.class))),
    @ApiResponse(responseCode = "400", description = "Invalid request parameters or non-event stream",
      content = @Content(schema = @Schema(implementation = FailedOperationException.class)))
  })
  @PostMapping(value = "/add-attendee/{streamId}")
  public AddNewStreamAttendeeResponse addStreamAttendee(
      @Parameter(description = "ID of the stream to add attendee to", required = true)
      @PathVariable(name = "streamId") final Long streamId,
      @Parameter(description = "Details of the attendee to add", required = true)
      @Valid @RequestBody final AddNewStreamAttendeeDto addNewStreamAttendeeDto,
      @Parameter(hidden = true)
      @AuthenticationPrincipal final RegisteredUser user) {
    if (addNewStreamAttendeeDto.isEvent()) {
      return eventOperationsService.addEventAttendee(streamId, addNewStreamAttendeeDto, user);
    }
    throw new FailedOperationException();
  }

  @Operation(summary = "Count total streams created by user",
    description = "Returns the total number of streams created by the authenticated user. Can be filtered by stream type. Requires authentication."
  )
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Successfully retrieved stream count",
      content = @Content(schema = @Schema(implementation = TotalStreamsCreatedByUserResponse.class))),
    @ApiResponse(responseCode = "401", description = "User not authenticated",
      content = @Content(schema = @Schema(implementation = InvalidAuthenticationException.class))),
    @ApiResponse(responseCode = "400", description = "Invalid search parameters",
      content = @Content(schema = @Schema(implementation = FailedOperationException.class)))
  })
  @GetMapping(value = "/total-by-me")
  public TotalStreamsCreatedByUserResponse countTotalStreamsCreatedByUser(
      @Parameter(description = "Search criteria for stream types", required = true)
      @SearchParam final StreamTypeSearchRequest searchRequest,
      @Parameter(hidden = true)
      @AuthenticationPrincipal final RegisteredUser user) {
    return streamSearchService.countTotalStreamsByUser(searchRequest, user);
  }

  @Operation(summary = "Count total streams attended by user",
    description = "Returns the total number of streams attended by the authenticated user. Can be filtered by stream type. Requires authentication."
  )
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Successfully retrieved attended stream count",
      content = @Content(schema = @Schema(implementation = TotalStreamsAttendedByUserResponse.class))),
    @ApiResponse(responseCode = "401", description = "User not authenticated",
      content = @Content(schema = @Schema(implementation = InvalidAuthenticationException.class))),
    @ApiResponse(responseCode = "400", description = "Invalid search parameters",
      content = @Content(schema = @Schema(implementation = FailedOperationException.class)))
  })
  @GetMapping(value = "/total-attended-by-me")
  public TotalStreamsAttendedByUserResponse countTotalEventsAttendedByUser(
      @Parameter(description = "Search criteria for stream types", required = true)
      @SearchParam final StreamTypeSearchRequest searchRequest,
      @Parameter(hidden = true)
      @AuthenticationPrincipal final RegisteredUser user) {
    return streamSearchService.countTotalStreamsAttendedByUser(searchRequest, user);
  }
}
