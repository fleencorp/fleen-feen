package com.fleencorp.feen.controller.stream;

import com.fleencorp.feen.exception.base.FailedOperationException;
import com.fleencorp.feen.exception.calendar.CalendarNotFoundException;
import com.fleencorp.feen.exception.stream.FleenStreamNotFoundException;
import com.fleencorp.feen.exception.stream.core.StreamAlreadyCanceledException;
import com.fleencorp.feen.exception.stream.core.StreamAlreadyHappenedException;
import com.fleencorp.feen.exception.stream.join.request.AlreadyApprovedRequestToJoinException;
import com.fleencorp.feen.exception.stream.join.request.AlreadyRequestedToJoinStreamException;
import com.fleencorp.feen.exception.stream.join.request.CannotJoinPrivateStreamWithoutApprovalException;
import com.fleencorp.feen.model.dto.stream.attendance.JoinStreamDto;
import com.fleencorp.feen.model.dto.stream.attendance.NotAttendingStreamDto;
import com.fleencorp.feen.model.dto.stream.attendance.RequestToJoinStreamDto;
import com.fleencorp.feen.model.response.stream.attendance.JoinStreamResponse;
import com.fleencorp.feen.model.response.stream.attendance.NotAttendingStreamResponse;
import com.fleencorp.feen.model.response.stream.attendance.RequestToJoinStreamResponse;
import com.fleencorp.feen.model.security.FleenUser;
import com.fleencorp.feen.service.stream.common.CommonStreamJoinService;
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
public class StreamController {

  private final CommonStreamJoinService commonStreamJoinService;

  public StreamController(final CommonStreamJoinService commonStreamJoinService) {
    this.commonStreamJoinService = commonStreamJoinService;
  }

  @Operation(summary = "Join a stream",
    description = "Allows an authenticated user to join a specific stream by providing stream details and joining information."
  )
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Successfully joined the stream", content = @Content(schema = @Schema(implementation = JoinStreamResponse.class))),
    @ApiResponse(responseCode = "400", description = "Bad Request",
      content = {
        @Content(schema = @Schema(implementation = CannotJoinPrivateStreamWithoutApprovalException.class)),
        @Content(schema = @Schema(implementation = FailedOperationException.class))
      }),
    @ApiResponse(responseCode = "404", description = "Not found", content = @Content(schema = @Schema(implementation = FleenStreamNotFoundException.class))),
    @ApiResponse(responseCode = "404", description = "Not found", content = @Content(schema = @Schema(implementation = CalendarNotFoundException.class))),
    @ApiResponse(responseCode = "409", description = "Conflict",
      content = {
        @Content(schema = @Schema(implementation = StreamAlreadyCanceledException.class)),
        @Content(schema = @Schema(implementation = StreamAlreadyHappenedException.class)),
        @Content(schema = @Schema(implementation = AlreadyRequestedToJoinStreamException.class)),
        @Content(schema = @Schema(implementation = AlreadyApprovedRequestToJoinException.class))
      })
  })
  @PostMapping(value = "/join/{streamId}")
  public JoinStreamResponse joinStream(
      @Parameter(description = "ID of the stream to join", required = true)
        @PathVariable(name = "streamId") final Long streamId,
      @Parameter(description = "Join stream details", required = true)
        @Valid @RequestBody final JoinStreamDto joinStreamDto,
      @Parameter(hidden = true)
        @AuthenticationPrincipal final FleenUser user) {
    return commonStreamJoinService.joinStream(streamId, joinStreamDto, user);
  }

  @Operation(summary = "Request to join a stream",
    description = "Allows an authenticated user to request to join a specific stream by providing stream details and request information."
  )
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Successfully requested to join the stream", content = @Content(schema = @Schema(implementation = RequestToJoinStreamResponse.class))),
    @ApiResponse(responseCode = "400", description = "Bad Request",
      content = {
        @Content(schema = @Schema(implementation = FailedOperationException.class))
      }),
    @ApiResponse(responseCode = "404", description = "Not found", content = @Content(schema = @Schema(implementation = FleenStreamNotFoundException.class))),
    @ApiResponse(responseCode = "404", description = "Not found", content = @Content(schema = @Schema(implementation = CalendarNotFoundException.class))),
    @ApiResponse(responseCode = "409", description = "Conflict",
      content = {
        @Content(schema = @Schema(implementation = StreamAlreadyCanceledException.class)),
        @Content(schema = @Schema(implementation = StreamAlreadyHappenedException.class)),
        @Content(schema = @Schema(implementation = AlreadyRequestedToJoinStreamException.class)),
        @Content(schema = @Schema(implementation = AlreadyApprovedRequestToJoinException.class))
      })
  })
  @PostMapping(value = "/request-to-join/{streamId}")
  public RequestToJoinStreamResponse requestToJoinStream(
    @Parameter(description = "ID of the stream to request to join", required = true)
      @PathVariable(name = "streamId") final Long streamId,
    @Parameter(description = "Request to join stream details", required = true)
      @Valid @RequestBody final RequestToJoinStreamDto requestToJoinStreamDto,
    @Parameter(hidden = true)
      @AuthenticationPrincipal final FleenUser user) {
    return commonStreamJoinService.requestToJoinStream(streamId, requestToJoinStreamDto, user);
  }

  @Operation(summary = "Mark user as not attending a stream",
    description = "Allows an authenticated user to indicate that they will not attend a specific stream by providing stream details."
  )
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Successfully marked as not attending the stream", content = @Content(schema = @Schema(implementation = NotAttendingStreamResponse.class))),
    @ApiResponse(responseCode = "400", description = "Bad Request",
      content = @Content(schema = @Schema(implementation = FailedOperationException.class))),
    @ApiResponse(responseCode = "404", description = "Not found",
      content = {
        @Content(schema = @Schema(implementation = FleenStreamNotFoundException.class)),
        @Content(schema = @Schema(implementation = CalendarNotFoundException.class))
      }),
    @ApiResponse(responseCode = "409", description = "Conflict",
      content = {
        @Content(schema = @Schema(implementation = StreamAlreadyCanceledException.class)),
        @Content(schema = @Schema(implementation = StreamAlreadyHappenedException.class))
      })
  })
  @PutMapping(value = "/not-attending/{streamId}")
  public NotAttendingStreamResponse notAttendingStream(
    @Parameter(description = "ID of the stream to mark as not attending", required = true)
      @PathVariable(name = "streamId") final Long streamId,
    @Parameter(description = "Not attending stream details", required = true)
      @Valid @RequestBody final NotAttendingStreamDto notAttendingStreamDto,
    @Parameter(hidden = true)
      @AuthenticationPrincipal final FleenUser user) {
    return commonStreamJoinService.notAttendingStream(streamId, notAttendingStreamDto, user);
  }
}
