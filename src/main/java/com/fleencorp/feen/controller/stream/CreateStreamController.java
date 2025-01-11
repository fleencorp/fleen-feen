package com.fleencorp.feen.controller.stream;

import com.fleencorp.feen.model.dto.event.CreateCalendarEventDto;
import com.fleencorp.feen.model.dto.event.CreateInstantCalendarEventDto;
import com.fleencorp.feen.model.dto.livebroadcast.CreateLiveBroadcastDto;
import com.fleencorp.feen.model.response.stream.base.CreateStreamResponse;
import com.fleencorp.feen.model.response.stream.common.event.DataForCreateEventResponse;
import com.fleencorp.feen.model.response.stream.common.live.broadcast.DataForCreateLiveBroadcastResponse;
import com.fleencorp.feen.model.security.FleenUser;
import com.fleencorp.feen.service.stream.EventService;
import com.fleencorp.feen.service.stream.LiveBroadcastService;
import jakarta.validation.Valid;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api")
@PreAuthorize("hasAnyRole('USER', 'ADMINISTRATOR', 'SUPER_ADMINISTRATOR')")
public class CreateStreamController {

  private final EventService eventService;
  private final LiveBroadcastService liveBroadcastService;

  public CreateStreamController(
      final EventService eventService,
      final LiveBroadcastService liveBroadcastService) {
    this.eventService = eventService;
    this.liveBroadcastService = liveBroadcastService;
  }

  @GetMapping(value = "/event/required-data-create")
  @Cacheable(value = "data-required-to-create-event")
  public DataForCreateEventResponse getDataCreateEvent() {
    return eventService.getDataForCreateEvent();
  }

  @PostMapping(value = "/event/create")
  public CreateStreamResponse createEvent(
      @Valid @RequestBody final CreateCalendarEventDto createCalendarEventDto,
      @AuthenticationPrincipal final FleenUser user) {
    return eventService.createEvent(createCalendarEventDto, user);
  }

  @PostMapping(value = "/event/create/instant")
  public CreateStreamResponse createInstantEvent(
      @Valid @RequestBody final CreateInstantCalendarEventDto createInstantCalendarEventDto,
      @AuthenticationPrincipal final FleenUser user) {
    return eventService.createInstantEvent(createInstantCalendarEventDto, user);
  }

  @GetMapping(value = "/live-broadcast/required-data-create")
  @Cacheable(value = "data-required-to-create-live-broadcast")
  public DataForCreateLiveBroadcastResponse getDataCreateLiveBroadcast() {
    return liveBroadcastService.getDataForCreateLiveBroadcast();
  }

  @PostMapping(value = "/create")
  public CreateStreamResponse createLiveBroadcast(
      @Valid @RequestBody final CreateLiveBroadcastDto createLiveBroadcastDto,
      @AuthenticationPrincipal final FleenUser user) {
    return liveBroadcastService.createLiveBroadcast(createLiveBroadcastDto, user);
  }
}
