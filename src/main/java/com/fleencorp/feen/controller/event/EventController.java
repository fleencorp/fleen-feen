package com.fleencorp.feen.controller.event;

import com.fleencorp.base.model.view.search.SearchResultView;
import com.fleencorp.base.resolver.SearchParam;
import com.fleencorp.feen.constant.stream.StreamTimeType;
import com.fleencorp.feen.model.dto.event.CreateCalendarEventDto;
import com.fleencorp.feen.model.dto.event.CreateInstantCalendarEventDto;
import com.fleencorp.feen.model.dto.event.UpdateCalendarEventDto;
import com.fleencorp.feen.model.dto.stream.RequestToJoinEventOrStreamDto;
import com.fleencorp.feen.model.request.search.calendar.CalendarEventSearchRequest;
import com.fleencorp.feen.model.request.search.stream.StreamAttendeeSearchRequest;
import com.fleencorp.feen.model.response.event.*;
import com.fleencorp.feen.model.security.FleenUser;
import com.fleencorp.feen.service.stream.EventService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/event")
@PreAuthorize("hasAnyRole('USER', 'ADMINISTRATOR', 'SUPER_ADMINISTRATOR')")
public class EventController {

  private final EventService eventService;

  public EventController(final EventService eventService) {
    this.eventService = eventService;
  }

  @GetMapping(value = "/entries")
  public SearchResultView findEvents(
      @SearchParam final CalendarEventSearchRequest searchRequest) {
    return eventService.findEvents(searchRequest);
  }

  @GetMapping(value = "/entries/type")
  public SearchResultView findEvents(
      @SearchParam final CalendarEventSearchRequest searchRequest,
      final StreamTimeType streamTimeType) {
    return eventService.findEvents(searchRequest, streamTimeType);
  }

  @GetMapping(value = "/detail/{eventId}")
  public RetrieveEventResponse findEvent(
      @PathVariable(name = "eventId") final Long eventId) {
    return eventService.retrieveEvent(eventId);
  }

  @GetMapping(value = "/attendees/{eventId}")
  public SearchResultView findEventAttendees(
      @PathVariable(name = "eventId") final Long eventId,
      @SearchParam final StreamAttendeeSearchRequest searchRequest) {
    return eventService.findEventAttendees(eventId, searchRequest);
  }

  @PostMapping(value = "/create")
  public CreateEventResponse createEvent(
      @Valid @RequestBody final CreateCalendarEventDto createCalendarEventDto,
      @AuthenticationPrincipal final FleenUser user) {
    return eventService.createEvent(createCalendarEventDto, user);
  }

  @PostMapping(value = "/create/instant")
  public CreateEventResponse createInstantEvent(
      @Valid @RequestBody final CreateInstantCalendarEventDto createInstantCalendarEventDto,
      @AuthenticationPrincipal final FleenUser user) {
    return eventService.createInstantEvent(createInstantCalendarEventDto, user);
  }

  @PutMapping(value = "/update/{eventId}")
  public UpdateEventResponse updateEvent(
      @PathVariable(name = "eventId") final Long eventId,
      @Valid @RequestBody final UpdateCalendarEventDto updateCalendarEventDto,
      @AuthenticationPrincipal final FleenUser user) {
    return eventService.updateEvent(eventId, updateCalendarEventDto, user);
  }

  @PutMapping(value = "/not-attending/{eventId}")
  public NotAttendingEventResponse notAttendingEvent(
      @PathVariable(name = "eventId") final Long eventId,
      @AuthenticationPrincipal final FleenUser user) {
    return eventService.notAttendingEvent(eventId, user);
  }

  @PostMapping(value = "/join/{eventId}")
  public JoinEventResponse joinEvent(
      @PathVariable(name = "eventId") final Long eventId,
      @AuthenticationPrincipal final FleenUser user) {
    return eventService.joinEvent(eventId, user);
  }

  @PostMapping(value = "/request-to-join/{eventId}")
  public RequestToJoinEventResponse requestToJoinEvent(
      @PathVariable(name = "eventId") final Long eventId,
      @Valid @RequestBody final RequestToJoinEventOrStreamDto requestToJoinEventOrStreamDto,
      @AuthenticationPrincipal final FleenUser user) {
    return eventService.requestToJoinEvent(eventId, requestToJoinEventOrStreamDto, user);
  }


}