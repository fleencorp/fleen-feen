package com.fleencorp.feen.controller.event;

import com.fleencorp.base.model.view.search.SearchResultView;
import com.fleencorp.base.resolver.SearchParam;
import com.fleencorp.feen.model.dto.event.AddNewEventAttendeeDto;
import com.fleencorp.feen.model.dto.event.RescheduleCalendarEventDto;
import com.fleencorp.feen.model.dto.stream.ProcessAttendeeRequestToJoinEventOrStreamDto;
import com.fleencorp.feen.model.dto.stream.UpdateEventOrStreamVisibilityDto;
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
@RequestMapping(value = "/api/user/event")
@PreAuthorize("hasAnyRole('USER', 'ADMINISTRATOR', 'SUPER_ADMINISTRATOR')")
public class UserEventController {

  private final EventService eventService;

  public UserEventController(final EventService eventService) {
    this.eventService = eventService;
  }

  @GetMapping(value = "/entries")
  public SearchResultView findEvents(
      @SearchParam final CalendarEventSearchRequest searchRequest,
      @AuthenticationPrincipal final FleenUser user) {
    return eventService.findEvents(searchRequest, user);
  }

  @GetMapping(value = "/attended-by-me")
  public SearchResultView findEventsAttendedByUser(
      @SearchParam final CalendarEventSearchRequest searchRequest,
      @AuthenticationPrincipal final FleenUser user) {
    return eventService.findEventsAttendedByUser(searchRequest, user);
  }

  @GetMapping(value = "/attended-with-user")
  public SearchResultView findEventsAttendedWithAnotherUser(
      @SearchParam final CalendarEventSearchRequest searchRequest,
      @AuthenticationPrincipal final FleenUser user) {
    return eventService.findEventsAttendedWithAnotherUser(searchRequest, user);
  }

  @PutMapping(value = "/cancel/{eventId}")
  public CancelEventResponse cancelEvent(
      @PathVariable(name = "eventId") final Long eventId,
      @AuthenticationPrincipal final FleenUser user) {
    return eventService.cancelEvent(eventId, user);
  }

  @DeleteMapping(value = "/delete/{eventId}")
  public DeletedEventResponse deleteEvent(
      @PathVariable(name = "eventId") final Long eventId,
      @AuthenticationPrincipal final FleenUser user) {
    return eventService.deleteEvent(eventId, user);
  }

  @GetMapping(value = "/attendees/request-to-join/{eventId}")
  public SearchResultView getAttendeesRequestToJoin(
      @PathVariable(name = "eventId") final Long eventId,
      @AuthenticationPrincipal final FleenUser user,
      @SearchParam final StreamAttendeeSearchRequest searchRequest) {
    return eventService.getEventAttendeeRequestsToJoinEvent(eventId, searchRequest, user);
  }

  @PutMapping(value = "/process-join-request/{eventId}")
  public ProcessAttendeeRequestToJoinEventResponse processAttendeeRequestToJoinEvent(
      @PathVariable(name = "eventId") final Long eventId,
      @Valid @RequestBody final ProcessAttendeeRequestToJoinEventOrStreamDto processAttendeeRequestToJoinEventOrStreamDto,
      @AuthenticationPrincipal final FleenUser user) {
    return eventService.processAttendeeRequestToJoinEvent(eventId, processAttendeeRequestToJoinEventOrStreamDto, user);
  }

  @PutMapping(value = "/reschedule/{eventId}")
  public RescheduleEventResponse rescheduleEvent(
      @PathVariable(name = "eventId") final Long eventId,
      @Valid @RequestBody final RescheduleCalendarEventDto rescheduleCalendarEventDto,
      @AuthenticationPrincipal final FleenUser user) {
    return eventService.rescheduleEvent(eventId, rescheduleCalendarEventDto, user);
  }

  @PutMapping(value = "/update-visibility/{eventId}")
  public UpdateEventVisibilityResponse updateEventVisibility(
      @PathVariable(name = "eventId") final Long eventId,
      @Valid @RequestBody final UpdateEventOrStreamVisibilityDto updateEventOrStreamVisibilityDto,
      @AuthenticationPrincipal final FleenUser user) {
    return eventService.updateEventVisibility(eventId, updateEventOrStreamVisibilityDto, user);
  }

  @PostMapping(value = "/add-attendee/{eventId}")
  public AddNewEventAttendeeResponse addNewEventAttendee(
      @PathVariable(name = "eventId") final Long eventId,
      @Valid @RequestBody final AddNewEventAttendeeDto addNewEventAttendeeDto,
      @AuthenticationPrincipal final FleenUser user) {
    return eventService.addEventAttendee(eventId, addNewEventAttendeeDto, user);
  }

  @GetMapping(value = "/attendees/{eventId}")
  public EventAttendeesResponse getEventAttendees(
      @PathVariable(name = "eventId") final Long eventId,
      @AuthenticationPrincipal final FleenUser user) {
    return eventService.getEventAttendees(eventId, user);
  }

  @GetMapping(value = "/total-by-me")
  public TotalEventsCreatedByUserResponse countTotalEventsCreatedByUser(
      @AuthenticationPrincipal final FleenUser user) {
    return eventService.countTotalEventsByUser(user);
  }

  @GetMapping(value = "/total-attended-by-me")
  public TotalEventsAttendedByUserResponse countTotalEventsAttendedByUser(
      @AuthenticationPrincipal final FleenUser user) {
    return eventService.countTotalEventsAttended(user);
  }
}
