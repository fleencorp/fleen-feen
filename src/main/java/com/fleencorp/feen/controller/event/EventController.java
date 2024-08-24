package com.fleencorp.feen.controller.event;

import com.fleencorp.base.model.view.search.SearchResultView;
import com.fleencorp.base.resolver.SearchParam;
import com.fleencorp.feen.constant.stream.StreamTimeType;
import com.fleencorp.feen.model.dto.event.*;
import com.fleencorp.feen.model.request.search.calendar.CalendarEventSearchRequest;
import com.fleencorp.feen.model.request.search.stream.StreamAttendeeSearchRequest;
import com.fleencorp.feen.model.response.event.*;
import com.fleencorp.feen.model.security.FleenUser;
import com.fleencorp.feen.service.stream.EventService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/event")
public class EventController {

  private final EventService eventService;

  public EventController(final EventService eventService) {
    this.eventService = eventService;
  }

  public SearchResultView findEvents(
      @SearchParam final CalendarEventSearchRequest searchRequest) {
    return eventService.findEvents(searchRequest);
  }

  public SearchResultView findEvents(
      @SearchParam final CalendarEventSearchRequest searchRequest,
      @AuthenticationPrincipal final FleenUser user) {
    return eventService.findEvents(searchRequest, user);
  }

  public SearchResultView findEvents(
      @SearchParam final CalendarEventSearchRequest searchRequest,
      final StreamTimeType streamTimeType) {
    return eventService.findEvents(searchRequest, streamTimeType);
  }

  public SearchResultView findEventAttendees(
    @PathVariable(name = "id") final Long eventId,
    @SearchParam final StreamAttendeeSearchRequest searchRequest) {
    return eventService.findEventAttendees(eventId, searchRequest);
  }

  public RetrieveEventResponse findEvent(
    @PathVariable(name = "id") final Long eventId) {
    return eventService.retrieveEvent(eventId);
  }

  public CreateEventResponse createEvent(
    @Valid @RequestBody final CreateCalendarEventDto createCalendarEventDto,
    @AuthenticationPrincipal final FleenUser user) {
    return eventService.createEvent(createCalendarEventDto, user);
  }

  public CreateEventResponse createInstantEvent(
    @Valid @RequestBody final CreateInstantCalendarEventDto createInstantCalendarEventDto,
    @AuthenticationPrincipal final FleenUser user) {
    return eventService.createInstantEvent(createInstantCalendarEventDto, user);
  }

  public UpdateEventResponse updateEvent(
    @PathVariable(name = "id") final Long eventId,
    @Valid @RequestBody final UpdateCalendarEventDto updateCalendarEventDto,
    @AuthenticationPrincipal final FleenUser user) {
    return eventService.updateEvent(eventId, updateCalendarEventDto, user);
  }

  public CancelEventResponse cancelEvent(
    @PathVariable(name = "id") final Long eventId,
    @AuthenticationPrincipal final FleenUser user) {
    return eventService.cancelEvent(eventId, user);
  }

  public NotAttendingEventResponse notAttendingEvent(
    @PathVariable(name = "id") final Long eventId,
    @AuthenticationPrincipal final FleenUser user) {
    return eventService.notAttendingEvent(eventId, user);
  }

  public RescheduleEventResponse rescheduleEvent(
    @PathVariable(name = "id") final Long eventId,
    @Valid @RequestBody final RescheduleCalendarEventDto rescheduleCalendarEventDto,
    @AuthenticationPrincipal final FleenUser user) {
    return eventService.rescheduleEvent(eventId, rescheduleCalendarEventDto, user);
  }

  public JoinEventResponse joinEvent(
    @PathVariable(name = "id") final Long eventId,
    @AuthenticationPrincipal final FleenUser user) {
    return eventService.joinEvent(eventId, user);
  }

  public RequestToJoinEventResponse requestToJoinEvent(
    @PathVariable(name = "id") final Long eventId,
    @Valid @RequestBody final RequestToJoinEventDto requestToJoinEventDto,
    @AuthenticationPrincipal final FleenUser user) {
    return eventService.requestToJoinEvent(eventId, requestToJoinEventDto, user);
  }


}
