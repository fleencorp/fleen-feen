package com.fleencorp.feen.controller.event;

import com.fleencorp.base.resolver.SearchParam;
import com.fleencorp.feen.constant.stream.StreamTimeType;
import com.fleencorp.feen.constant.stream.StreamType;
import com.fleencorp.feen.model.dto.event.CreateCalendarEventDto;
import com.fleencorp.feen.model.dto.event.CreateInstantCalendarEventDto;
import com.fleencorp.feen.model.dto.stream.attendance.JoinStreamDto;
import com.fleencorp.feen.model.dto.stream.attendance.NotAttendingStreamDto;
import com.fleencorp.feen.model.dto.stream.attendance.RequestToJoinStreamDto;
import com.fleencorp.feen.model.dto.stream.base.UpdateStreamDto;
import com.fleencorp.feen.model.request.search.calendar.EventSearchRequest;
import com.fleencorp.feen.model.request.search.stream.StreamAttendeeSearchRequest;
import com.fleencorp.feen.model.response.stream.attendance.JoinStreamResponse;
import com.fleencorp.feen.model.response.stream.attendance.NotAttendingStreamResponse;
import com.fleencorp.feen.model.response.stream.attendance.RequestToJoinStreamResponse;
import com.fleencorp.feen.model.response.stream.base.CreateStreamResponse;
import com.fleencorp.feen.model.response.stream.base.RetrieveStreamResponse;
import com.fleencorp.feen.model.response.stream.base.UpdateStreamResponse;
import com.fleencorp.feen.model.response.stream.common.event.DataForCreateEventResponse;
import com.fleencorp.feen.model.search.stream.attendee.StreamAttendeeSearchResult;
import com.fleencorp.feen.model.search.stream.common.StreamSearchResult;
import com.fleencorp.feen.model.security.FleenUser;
import com.fleencorp.feen.service.stream.EventService;
import com.fleencorp.feen.service.stream.attendee.StreamAttendeeService;
import com.fleencorp.feen.service.stream.join.EventJoinService;
import com.fleencorp.feen.service.stream.search.StreamSearchService;
import jakarta.validation.Valid;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/event")
public class EventController {

  private final EventService eventService;
  private final EventJoinService eventJoinService;
  private final StreamSearchService streamSearchService;
  private final StreamAttendeeService streamAttendeeService;

  public EventController(
      final EventService eventService,
      final EventJoinService eventJoinService,
      final StreamSearchService streamSearchService,
      final StreamAttendeeService streamAttendeeService) {
    this.eventService = eventService;
    this.eventJoinService = eventJoinService;
    this.streamSearchService = streamSearchService;
    this.streamAttendeeService = streamAttendeeService;
  }

  @GetMapping(value = "/data-create-event")
  @Cacheable(value = "data-required-to-create-event")
  public DataForCreateEventResponse getDataCreateEvent() {
    return eventService.getDataForCreateEvent();
  }

  @GetMapping(value = "/entries")
  public StreamSearchResult findEvents(
      @SearchParam final EventSearchRequest searchRequest,
      @AuthenticationPrincipal final FleenUser user) {
    searchRequest.setStreamType(StreamType.event());
    return streamSearchService.findStreams(searchRequest, user);
  }

  @GetMapping(value = "/entries/type")
  public StreamSearchResult findEvents(
      @SearchParam final EventSearchRequest searchRequest,
      final StreamTimeType streamTimeType) {
    searchRequest.setStreamType(StreamType.event());
    return streamSearchService.findStreams(searchRequest, streamTimeType);
  }

  @GetMapping(value = "/detail/{eventId}")
  public RetrieveStreamResponse findEvent(
      @PathVariable(name = "eventId") final Long eventId,
      @AuthenticationPrincipal final FleenUser user) {
    return streamSearchService.retrieveStream(eventId, user);
  }

  @GetMapping(value = "/attendees/{eventId}")
  public StreamAttendeeSearchResult findEventAttendees(
      @PathVariable(name = "eventId") final Long eventId,
      @SearchParam final StreamAttendeeSearchRequest searchRequest) {
    return streamAttendeeService.findStreamAttendees(eventId, searchRequest);
  }

  @PreAuthorize("hasAnyRole('USER', 'ADMINISTRATOR', 'SUPER_ADMINISTRATOR')")
  @PostMapping(value = "/create")
  public CreateStreamResponse createEvent(
      @Valid @RequestBody final CreateCalendarEventDto createCalendarEventDto,
      @AuthenticationPrincipal final FleenUser user) {
    return eventService.createEvent(createCalendarEventDto, user);
  }

  @PreAuthorize("hasAnyRole('USER', 'ADMINISTRATOR', 'SUPER_ADMINISTRATOR')")
  @PostMapping(value = "/create/instant")
  public CreateStreamResponse createInstantEvent(
      @Valid @RequestBody final CreateInstantCalendarEventDto createInstantCalendarEventDto,
      @AuthenticationPrincipal final FleenUser user) {
    return eventService.createInstantEvent(createInstantCalendarEventDto, user);
  }

  @PreAuthorize("hasAnyRole('USER', 'ADMINISTRATOR', 'SUPER_ADMINISTRATOR')")
  @PutMapping(value = "/update/{eventId}")
  public UpdateStreamResponse updateEvent(
      @PathVariable(name = "eventId") final Long eventId,
      @Valid @RequestBody final UpdateStreamDto updateStreamDto,
      @AuthenticationPrincipal final FleenUser user) {
    return eventService.updateEvent(eventId, updateStreamDto, user);
  }

  @PreAuthorize("hasAnyRole('USER', 'ADMINISTRATOR', 'SUPER_ADMINISTRATOR')")
  @PutMapping(value = "/not-attending/{eventId}")
  public NotAttendingStreamResponse notAttendingEvent(
      @PathVariable(name = "eventId") final Long eventId,
      @Valid @RequestBody final NotAttendingStreamDto notAttendingStreamDto,
      @AuthenticationPrincipal final FleenUser user) {
    return eventJoinService.notAttendingEvent(eventId, notAttendingStreamDto, user);
  }

  @PreAuthorize("hasAnyRole('USER', 'ADMINISTRATOR', 'SUPER_ADMINISTRATOR')")
  @PostMapping(value = "/join/{eventId}")
  public JoinStreamResponse joinEvent(
      @PathVariable(name = "eventId") final Long eventId,
      @Valid @RequestBody final JoinStreamDto joinStreamDto,
      @AuthenticationPrincipal final FleenUser user) {
    return eventJoinService.joinEvent(eventId, joinStreamDto, user);
  }

  @PreAuthorize("hasAnyRole('USER', 'ADMINISTRATOR', 'SUPER_ADMINISTRATOR')")
  @PostMapping(value = "/request-to-join/{eventId}")
  public RequestToJoinStreamResponse requestToJoinEvent(
      @PathVariable(name = "eventId") final Long eventId,
      @Valid @RequestBody final RequestToJoinStreamDto requestToJoinStreamDto,
      @AuthenticationPrincipal final FleenUser user) {
    return eventJoinService.requestToJoinEvent(eventId, requestToJoinStreamDto, user);
  }

}
