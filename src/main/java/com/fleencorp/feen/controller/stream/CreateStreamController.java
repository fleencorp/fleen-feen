package com.fleencorp.feen.controller.stream;

import com.fleencorp.feen.exception.base.FailedOperationException;
import com.fleencorp.feen.exception.calendar.CalendarNotFoundException;
import com.fleencorp.feen.exception.google.oauth2.Oauth2InvalidAuthorizationException;
import com.fleencorp.feen.model.dto.event.CreateEventDto;
import com.fleencorp.feen.model.dto.event.CreateInstantEventDto;
import com.fleencorp.feen.model.dto.livebroadcast.CreateLiveBroadcastDto;
import com.fleencorp.feen.model.response.stream.base.CreateStreamResponse;
import com.fleencorp.feen.model.response.stream.common.DataForRescheduleStreamResponse;
import com.fleencorp.feen.model.response.stream.common.event.DataForCreateEventResponse;
import com.fleencorp.feen.model.response.stream.common.live.broadcast.DataForCreateLiveBroadcastResponse;
import com.fleencorp.feen.model.security.FleenUser;
import com.fleencorp.feen.service.stream.LiveBroadcastService;
import com.fleencorp.feen.service.stream.StreamOperationsService;
import com.fleencorp.feen.service.stream.event.EventOperationsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api")
@PreAuthorize("hasAnyRole('USER', 'ADMINISTRATOR', 'SUPER_ADMINISTRATOR')")
public class CreateStreamController {

  private final EventOperationsService eventOperationsService;
  private final LiveBroadcastService liveBroadcastService;
  private final StreamOperationsService streamOperationsService;

  public CreateStreamController(
      final EventOperationsService eventOperationsService,
      final LiveBroadcastService liveBroadcastService,
      final StreamOperationsService streamOperationsService) {
    this.eventOperationsService = eventOperationsService;
    this.liveBroadcastService = liveBroadcastService;
    this.streamOperationsService = streamOperationsService;
  }

  @Operation(summary = "Get required data for event creation",
    description = "Retrieves all necessary data and configuration required to create a new event."
  )
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Successfully retrieved event creation data",
      content = @Content(schema = @Schema(implementation = DataForCreateEventResponse.class)))
  })
  @GetMapping(value = "/event/required-data-create")
  @Cacheable(value = "data-required-to-create-event")
  public DataForCreateEventResponse getDataCreateEvent() {
    return eventOperationsService.getDataForCreateEvent();
  }

  @Operation(summary = "Create a new event",
    description = "Creates a new calendar event with the provided details. Requires user authentication."
  )
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Successfully created the event",
      content = @Content(schema = @Schema(implementation = CreateStreamResponse.class))),
    @ApiResponse(responseCode = "404", description = "Calendar not found",
      content = @Content(schema = @Schema(implementation = CalendarNotFoundException.class))),
    @ApiResponse(responseCode = "400", description = "Failed operation",
      content = @Content(schema = @Schema(implementation = FailedOperationException.class)))
  })
  @PostMapping(value = "/event/create")
  public CreateStreamResponse createEvent(
      @Parameter(description = "Event details for creation", required = true)
        @Valid @RequestBody final CreateEventDto createEventDto,
      @Parameter(hidden = true)
        @AuthenticationPrincipal final FleenUser user) {
    return eventOperationsService.createEvent(createEventDto, user);
  }

  @Operation(summary = "Create an instant event",
    description = "Creates a new instant calendar event that starts immediately. Requires user authentication."
  )
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Successfully created the instant event",
      content = @Content(schema = @Schema(implementation = CreateStreamResponse.class))),
    @ApiResponse(responseCode = "404", description = "Calendar not found",
      content = @Content(schema = @Schema(implementation = CalendarNotFoundException.class))),
    @ApiResponse(responseCode = "400", description = "Failed operation",
      content = @Content(schema = @Schema(implementation = FailedOperationException.class)))
  })
  @PostMapping(value = "/event/create/instant")
  public CreateStreamResponse createInstantEvent(
      @Parameter(description = "Instant event details for creation", required = true)
        @Valid @RequestBody final CreateInstantEventDto createInstantEventDto,
      @Parameter(hidden = true)
        @AuthenticationPrincipal final FleenUser user) {
    return eventOperationsService.createInstantEvent(createInstantEventDto, user);
  }

  @Operation(summary = "Get required data for live broadcast creation",
    description = "Retrieves all necessary data and configuration required to create a new live broadcast."
  )
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Successfully retrieved live broadcast creation data",
      content = @Content(schema = @Schema(implementation = DataForCreateLiveBroadcastResponse.class)))
  })
  @GetMapping(value = "/live-broadcast/required-data-create")
  @Cacheable(value = "data-required-to-create-live-broadcast")
  public DataForCreateLiveBroadcastResponse getDataCreateLiveBroadcast() {
    return liveBroadcastService.getDataForCreateLiveBroadcast();
  }

  @Operation(summary = "Get required data for stream rescheduling",
    description = "Retrieves all necessary data and configuration required to reschedule an existing stream."
  )
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Successfully retrieved stream rescheduling data",
      content = @Content(schema = @Schema(implementation = DataForRescheduleStreamResponse.class)))
  })
  @GetMapping(value = "/required-data-reschedule-stream")
  @Cacheable(value = "data-required-to-reschedule-stream")
  public DataForRescheduleStreamResponse getDataRescheduleStream() {
    return streamOperationsService.getDataForRescheduleStream();
  }

  @Operation(summary = "Create a new live broadcast",
    description = "Creates a new live broadcast stream with the provided details. Requires user authentication and valid OAuth2 authorization."
  )
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Successfully created the live broadcast",
      content = @Content(schema = @Schema(implementation = CreateStreamResponse.class))),
    @ApiResponse(responseCode = "401", description = "Invalid OAuth2 authorization",
      content = @Content(schema = @Schema(implementation = Oauth2InvalidAuthorizationException.class))),
    @ApiResponse(responseCode = "400", description = "Failed operation",
      content = @Content(schema = @Schema(implementation = FailedOperationException.class)))
  })
  @PostMapping(value = "/create")
  public CreateStreamResponse createLiveBroadcast(
      @Parameter(description = "Live broadcast details for creation", required = true)
        @Valid @RequestBody final CreateLiveBroadcastDto createLiveBroadcastDto,
      @Parameter(hidden = true)
        @AuthenticationPrincipal final FleenUser user) {
    return liveBroadcastService.createLiveBroadcast(createLiveBroadcastDto, user);
  }
}
