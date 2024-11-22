package com.fleencorp.feen.controller.event;

import com.fleencorp.base.resolver.SearchParam;
import com.fleencorp.feen.constant.stream.StreamTimeType;
import com.fleencorp.feen.model.dto.event.CreateCalendarEventDto;
import com.fleencorp.feen.model.dto.event.CreateInstantCalendarEventDto;
import com.fleencorp.feen.model.dto.event.UpdateCalendarEventDto;
import com.fleencorp.feen.model.dto.stream.JoinEventOrStreamDto;
import com.fleencorp.feen.model.dto.stream.RequestToJoinEventOrStreamDto;
import com.fleencorp.feen.model.request.search.calendar.EventSearchRequest;
import com.fleencorp.feen.model.request.search.stream.StreamAttendeeSearchRequest;
import com.fleencorp.feen.model.response.event.*;
import com.fleencorp.feen.model.search.event.EventSearchResult;
import com.fleencorp.feen.model.search.stream.attendee.StreamAttendeeSearchResult;
import com.fleencorp.feen.model.security.FleenUser;
import com.fleencorp.feen.service.stream.EventService;
import jakarta.validation.Valid;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/event")
public class EventController {

  private final EventService eventService;

  public EventController(final EventService eventService) {
    this.eventService = eventService;
  }

  @GetMapping(value = "/data-create-event")
  @Cacheable(value = "data-required-to-create-event")
  public DataForCreateEventResponse getDataCreateEvent() {
    return eventService.getDataForCreateEvent();
  }

  @GetMapping(value = "/entries")
  public EventSearchResult findEvents(
      @SearchParam final EventSearchRequest eventSearchRequest,
      @AuthenticationPrincipal final FleenUser user) {
    return eventService.findEvents(eventSearchRequest, user);
  }

  @GetMapping(value = "/entries/type")
  public EventSearchResult findEvents(
      @SearchParam final EventSearchRequest searchRequest,
      final StreamTimeType streamTimeType) {
    return eventService.findEvents(searchRequest, streamTimeType);
  }

  @GetMapping(value = "/detail/{eventId}")
  public RetrieveEventResponse findEvent(
      @PathVariable(name = "eventId") final Long eventId,
      @AuthenticationPrincipal final FleenUser user) {
    return eventService.retrieveEvent(eventId, user);
  }

  @GetMapping(value = "/attendees/{eventId}")
  public StreamAttendeeSearchResult findEventAttendees(
      @PathVariable(name = "eventId") final Long eventId,
      @SearchParam final StreamAttendeeSearchRequest searchRequest) {
    return eventService.findEventAttendees(eventId, searchRequest);
  }

  @PreAuthorize("hasAnyRole('USER', 'ADMINISTRATOR', 'SUPER_ADMINISTRATOR')")
  @PostMapping(value = "/create")
  public CreateEventResponse createEvent(
      @Valid @RequestBody final CreateCalendarEventDto createCalendarEventDto,
      @AuthenticationPrincipal final FleenUser user) {
    return eventService.createEvent(createCalendarEventDto, user);
  }

  @PreAuthorize("hasAnyRole('USER', 'ADMINISTRATOR', 'SUPER_ADMINISTRATOR')")
  @PostMapping(value = "/create/instant")
  public CreateEventResponse createInstantEvent(
      @Valid @RequestBody final CreateInstantCalendarEventDto createInstantCalendarEventDto,
      @AuthenticationPrincipal final FleenUser user) {
    return eventService.createInstantEvent(createInstantCalendarEventDto, user);
  }

  @PreAuthorize("hasAnyRole('USER', 'ADMINISTRATOR', 'SUPER_ADMINISTRATOR')")
  @PutMapping(value = "/update/{eventId}")
  public UpdateEventResponse updateEvent(
      @PathVariable(name = "eventId") final Long eventId,
      @Valid @RequestBody final UpdateCalendarEventDto updateCalendarEventDto,
      @AuthenticationPrincipal final FleenUser user) {
    return eventService.updateEvent(eventId, updateCalendarEventDto, user);
  }

  @PreAuthorize("hasAnyRole('USER', 'ADMINISTRATOR', 'SUPER_ADMINISTRATOR')")
  @PutMapping(value = "/not-attending/{eventId}")
  public NotAttendingEventResponse notAttendingEvent(
      @PathVariable(name = "eventId") final Long eventId,
      @AuthenticationPrincipal final FleenUser user) {
    return eventService.notAttendingEvent(eventId, user);
  }

  @PreAuthorize("hasAnyRole('USER', 'ADMINISTRATOR', 'SUPER_ADMINISTRATOR')")
  @PostMapping(value = "/join/{eventId}")
  public JoinEventResponse joinEvent(
      @PathVariable(name = "eventId") final Long eventId,
      @Valid @RequestBody final JoinEventOrStreamDto joinEventOrStreamDto,
      @AuthenticationPrincipal final FleenUser user) {
    return eventService.joinEvent(eventId, joinEventOrStreamDto, user);
  }

  @PreAuthorize("hasAnyRole('USER', 'ADMINISTRATOR', 'SUPER_ADMINISTRATOR')")
  @PostMapping(value = "/request-to-join/{eventId}")
  public RequestToJoinEventResponse requestToJoinEvent(
      @PathVariable(name = "eventId") final Long eventId,
      @Valid @RequestBody final RequestToJoinEventOrStreamDto requestToJoinEventOrStreamDto,
      @AuthenticationPrincipal final FleenUser user) {
    return eventService.requestToJoinEvent(eventId, requestToJoinEventOrStreamDto, user);
  }

}
