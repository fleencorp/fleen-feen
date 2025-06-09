package com.fleencorp.feen.controller.stream;

import com.fleencorp.feen.exception.base.FailedOperationException;
import com.fleencorp.feen.exception.calendar.CalendarNotFoundException;
import com.fleencorp.feen.exception.stream.StreamNotFoundException;
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
import com.fleencorp.feen.user.model.security.RegisteredUser;
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
    description = "Allows an authenticated user to join a specific stream by providing stream details and joining information. " +
                 "For public streams, grants immediate access. For private streams, users must request to join using the dedicated endpoint."
  )
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Successfully joined the stream. User now has access to the stream.",
      content = @Content(schema = @Schema(implementation = JoinStreamResponse.class))),
    @ApiResponse(responseCode = "400", description = "Bad Request - Attempted to directly join a private stream or operation failed",
      content = {
        @Content(schema = @Schema(implementation = CannotJoinPrivateStreamWithoutApprovalException.class)),
        @Content(schema = @Schema(implementation = FailedOperationException.class))
      }),
    @ApiResponse(responseCode = "404", description = "Stream or associated calendar not found",
      content = {
        @Content(schema = @Schema(implementation = StreamNotFoundException.class)),
        @Content(schema = @Schema(implementation = CalendarNotFoundException.class))
      }),
    @ApiResponse(responseCode = "409", description = "Conflict - Stream is canceled/happened or user has existing join status",
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
        @AuthenticationPrincipal final RegisteredUser user) {
    return commonStreamJoinService.joinStream(streamId, joinStreamDto, user);
  }

  @Operation(summary = "Request to join a private stream",
    description = "Allows an authenticated user to submit a request to join a private stream. " +
                 "This is required for private streams as they cannot be joined directly. " +
                 "The stream organizer will need to approve the request before access is granted."
  )
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Successfully submitted request to join the stream. Awaiting approval.",
      content = @Content(schema = @Schema(implementation = RequestToJoinStreamResponse.class))),
    @ApiResponse(responseCode = "400", description = "Bad Request - Invalid request parameters or operation not allowed",
      content = @Content(schema = @Schema(implementation = FailedOperationException.class))),
    @ApiResponse(responseCode = "404", description = "Stream or associated calendar not found",
      content = {
        @Content(schema = @Schema(implementation = StreamNotFoundException.class)),
        @Content(schema = @Schema(implementation = CalendarNotFoundException.class))
      }),
    @ApiResponse(responseCode = "409", description = "Conflict - Stream is canceled/happened or request already exists",
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
      @AuthenticationPrincipal final RegisteredUser user) {
    return commonStreamJoinService.requestToJoinStream(streamId, requestToJoinStreamDto, user);
  }

  @Operation(summary = "Mark user as not attending a stream",
    description = "Allows an authenticated user to indicate that they will not attend a stream they previously joined " +
                 "or requested to join. This helps stream organizers manage attendance and update capacity planning. " +
                 "The user's spot may be offered to others on the waiting list."
  )
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Successfully marked user as not attending the stream. Attendance status updated.",
      content = @Content(schema = @Schema(implementation = NotAttendingStreamResponse.class))),
    @ApiResponse(responseCode = "400", description = "Bad Request - Invalid parameters or operation not allowed",
      content = @Content(schema = @Schema(implementation = FailedOperationException.class))),
    @ApiResponse(responseCode = "404", description = "Stream or associated calendar not found",
      content = {
        @Content(schema = @Schema(implementation = StreamNotFoundException.class)),
        @Content(schema = @Schema(implementation = CalendarNotFoundException.class))
      }),
    @ApiResponse(responseCode = "409", description = "Conflict - Stream is canceled or has already happened",
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
      @AuthenticationPrincipal final RegisteredUser user) {
    return commonStreamJoinService.notAttendingStream(streamId, notAttendingStreamDto, user);
  }
}
