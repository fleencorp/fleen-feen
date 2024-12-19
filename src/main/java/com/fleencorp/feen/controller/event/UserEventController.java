package com.fleencorp.feen.controller.event;

import com.fleencorp.feen.model.dto.event.AddNewStreamAttendeeDto;
import com.fleencorp.feen.model.dto.stream.attendance.ProcessAttendeeRequestToJoinStreamDto;
import com.fleencorp.feen.model.dto.stream.base.RescheduleStreamDto;
import com.fleencorp.feen.model.dto.stream.base.UpdateStreamVisibilityDto;
import com.fleencorp.feen.model.response.stream.attendance.ProcessAttendeeRequestToJoinStreamResponse;
import com.fleencorp.feen.model.response.stream.base.CancelStreamResponse;
import com.fleencorp.feen.model.response.stream.base.DeleteStreamResponse;
import com.fleencorp.feen.model.response.stream.base.RescheduleStreamResponse;
import com.fleencorp.feen.model.response.stream.base.UpdateStreamVisibilityResponse;
import com.fleencorp.feen.model.response.stream.common.AddNewStreamAttendeeResponse;
import com.fleencorp.feen.model.security.FleenUser;
import com.fleencorp.feen.service.stream.EventService;
import com.fleencorp.feen.service.stream.join.EventJoinService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/user/event")
@PreAuthorize("hasAnyRole('ADMINISTRATOR', 'SUPER_ADMINISTRATOR', 'USER')")
public class UserEventController {

  private final EventService eventService;
  private final EventJoinService eventJoinService;

  public UserEventController(
      final EventService eventService,
      final EventJoinService eventJoinService) {
    this.eventService = eventService;
    this.eventJoinService = eventJoinService;
  }

  @PutMapping(value = "/cancel/{eventId}")
  public CancelStreamResponse cancelEvent(
      @PathVariable(name = "eventId") final Long eventId,
      @AuthenticationPrincipal final FleenUser user) {
    return eventService.cancelEvent(eventId, user);
  }

  @DeleteMapping(value = "/delete/{eventId}")
  public DeleteStreamResponse deleteEvent(
      @PathVariable(name = "eventId") final Long eventId,
      @AuthenticationPrincipal final FleenUser user) {
    return eventService.deleteEvent(eventId, user);
  }

  @PutMapping(value = "/process-join-request/{eventId}")
  public ProcessAttendeeRequestToJoinStreamResponse processAttendeeRequestToJoinEvent(
      @PathVariable(name = "eventId") final Long eventId,
      @Valid @RequestBody final ProcessAttendeeRequestToJoinStreamDto processAttendeeRequestToJoinStreamDto,
      @AuthenticationPrincipal final FleenUser user) {
    return eventJoinService.processAttendeeRequestToJoinEvent(eventId, processAttendeeRequestToJoinStreamDto, user);
  }

  @PutMapping(value = "/reschedule/{eventId}")
  public RescheduleStreamResponse rescheduleEvent(
      @PathVariable(name = "eventId") final Long eventId,
      @Valid @RequestBody final RescheduleStreamDto rescheduleStreamDto,
      @AuthenticationPrincipal final FleenUser user) {
    return eventService.rescheduleEvent(eventId, rescheduleStreamDto, user);
  }

  @PutMapping(value = "/update-visibility/{eventId}")
  public UpdateStreamVisibilityResponse updateEventVisibility(
      @PathVariable(name = "eventId") final Long eventId,
      @Valid @RequestBody final UpdateStreamVisibilityDto updateStreamVisibilityDto,
      @AuthenticationPrincipal final FleenUser user) {
    return eventService.updateEventVisibility(eventId, updateStreamVisibilityDto, user);
  }

  @PostMapping(value = "/add-attendee/{eventId}")
  public AddNewStreamAttendeeResponse addNewEventAttendee(
      @PathVariable(name = "eventId") final Long eventId,
      @Valid @RequestBody final AddNewStreamAttendeeDto addNewStreamAttendeeDto,
      @AuthenticationPrincipal final FleenUser user) {
    return eventJoinService.addEventAttendee(eventId, addNewStreamAttendeeDto, user);
  }
}
