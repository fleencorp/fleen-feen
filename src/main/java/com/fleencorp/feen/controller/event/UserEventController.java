package com.fleencorp.feen.controller.event;

import com.fleencorp.base.model.view.search.SearchResultView;
import com.fleencorp.base.resolver.SearchParam;
import com.fleencorp.feen.model.dto.event.AddNewEventAttendeeDto;
import com.fleencorp.feen.model.dto.event.ProcessAttendeeRequestToJoinEventDto;
import com.fleencorp.feen.model.dto.event.UpdateEventVisibilityDto;
import com.fleencorp.feen.model.request.search.calendar.CalendarEventSearchRequest;
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
@RequestMapping(value = "/user/event")
public class UserEventController {

  private final EventService eventService;

  public UserEventController(final EventService eventService) {
    this.eventService = eventService;
  }

  public SearchResultView findEventsAttendedByUser(
      @SearchParam final CalendarEventSearchRequest searchRequest,
      @AuthenticationPrincipal final FleenUser user) {
    return eventService.findEventsAttendedByUser(searchRequest, user);
  }

  public SearchResultView findEventsAttendedWithAnotherUser(
      @SearchParam final CalendarEventSearchRequest searchRequest,
      @AuthenticationPrincipal final FleenUser user) {
    return eventService.findEventsAttendedWithAnotherUser(searchRequest, user);
  }

  public ProcessAttendeeRequestToJoinEventResponse processAttendeeRequestToJoinEvent(
      @PathVariable(name = "id") final Long eventId,
      @Valid @RequestBody final ProcessAttendeeRequestToJoinEventDto processAttendeeRequestToJoinEventDto,
      @AuthenticationPrincipal final FleenUser user) {
    return eventService.processAttendeeRequestToJoinEvent(eventId, processAttendeeRequestToJoinEventDto, user);
  }

  public UpdateEventVisibilityResponse updateEventVisibility(
    @PathVariable(name = "id") final Long eventId,
    @Valid @RequestBody final UpdateEventVisibilityDto updateEventVisibilityDto,
    @AuthenticationPrincipal final FleenUser user) {
    return eventService.updateEventVisibility(eventId, updateEventVisibilityDto, user);
  }

  public AddNewEventAttendeeResponse addNewEventAttendee(
    @PathVariable(name = "id") final Long eventId,
    @Valid @RequestBody final AddNewEventAttendeeDto addNewEventAttendeeDto,
    @AuthenticationPrincipal final FleenUser user) {
    return eventService.addEventAttendee(eventId, addNewEventAttendeeDto, user);
  }

  public EventAttendeesResponse getEventAttendees(
    @PathVariable(name = "id") final Long eventId,
    @AuthenticationPrincipal final FleenUser user) {
    return eventService.getEventAttendees(eventId, user);
  }

  public TotalEventsCreatedByUserResponse countTotalEventsCreatedByUser(
    @AuthenticationPrincipal final FleenUser user) {
    return eventService.countTotalEventsByUser(user);
  }

  public TotalEventsAttendedByUserResponse countTotalEventsAttendedByUser(
    @AuthenticationPrincipal final FleenUser user) {
    return eventService.countTotalEventsAttended(user);
  }
}
