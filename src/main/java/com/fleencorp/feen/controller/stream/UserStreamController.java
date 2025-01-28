package com.fleencorp.feen.controller.stream;

import com.fleencorp.base.resolver.SearchParam;
import com.fleencorp.feen.exception.base.FailedOperationException;
import com.fleencorp.feen.model.dto.event.AddNewStreamAttendeeDto;
import com.fleencorp.feen.model.dto.stream.attendance.ProcessAttendeeRequestToJoinStreamDto;
import com.fleencorp.feen.model.dto.stream.base.*;
import com.fleencorp.feen.model.request.search.stream.StreamAttendeeSearchRequest;
import com.fleencorp.feen.model.request.search.stream.type.StreamTypeSearchRequest;
import com.fleencorp.feen.model.response.stream.attendance.ProcessAttendeeRequestToJoinStreamResponse;
import com.fleencorp.feen.model.response.stream.base.*;
import com.fleencorp.feen.model.response.stream.common.AddNewStreamAttendeeResponse;
import com.fleencorp.feen.model.response.stream.statistic.TotalStreamsAttendedByUserResponse;
import com.fleencorp.feen.model.response.stream.statistic.TotalStreamsCreatedByUserResponse;
import com.fleencorp.feen.model.search.stream.attendee.StreamAttendeeSearchResult;
import com.fleencorp.feen.model.security.FleenUser;
import com.fleencorp.feen.service.stream.EventService;
import com.fleencorp.feen.service.stream.LiveBroadcastService;
import com.fleencorp.feen.service.stream.attendee.StreamAttendeeService;
import com.fleencorp.feen.service.stream.join.EventJoinService;
import com.fleencorp.feen.service.stream.join.LiveBroadcastJoinService;
import com.fleencorp.feen.service.stream.search.StreamSearchService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/stream")
@PreAuthorize("hasAnyRole('ADMINISTRATOR', 'SUPER_ADMINISTRATOR', 'USER')")
public class UserStreamController {

  private final EventService eventService;
  private final EventJoinService eventJoinService;
  private final LiveBroadcastService liveBroadcastService;
  private final LiveBroadcastJoinService liveBroadcastJoinService;
  private final StreamAttendeeService streamAttendeeService;
  private final StreamSearchService streamSearchService;

  public UserStreamController(
      final EventService eventService,
      final EventJoinService eventJoinService,
      final LiveBroadcastService liveBroadcastService,
      final LiveBroadcastJoinService liveBroadcastJoinService,
      final StreamAttendeeService streamAttendeeService,
      final StreamSearchService streamSearchService) {
    this.eventService = eventService;
    this.eventJoinService = eventJoinService;
    this.liveBroadcastService = liveBroadcastService;
    this.liveBroadcastJoinService = liveBroadcastJoinService;
    this.streamAttendeeService = streamAttendeeService;
    this.streamSearchService = streamSearchService;
  }

  @PutMapping(value = "/update/{streamId}")
  public UpdateStreamResponse updateStream(
      @PathVariable(name = "streamId") final Long streamId,
      @Valid @RequestBody final UpdateStreamDto updateStreamDto,
      @AuthenticationPrincipal final FleenUser user) {
    return updateStreamDto.isEvent()
      ? eventService.updateEvent(streamId, updateStreamDto, user)
      : liveBroadcastService.updateLiveBroadcast(streamId, updateStreamDto, user);
  }

  @PutMapping(value = "/cancel/{streamId}")
  public CancelStreamResponse cancelStream(
      @PathVariable(name = "streamId") final Long streamId,
      @Valid @RequestBody final CancelStreamDto cancelStreamDto,
      @AuthenticationPrincipal final FleenUser user) {
    return cancelStreamDto.isEvent()
      ? eventService.cancelEvent(streamId, cancelStreamDto, user)
      : liveBroadcastService.cancelLiveBroadcast(streamId, cancelStreamDto, user);
  }

  @PutMapping(value = "/delete/{streamId}")
  public DeleteStreamResponse deleteStream(
      @PathVariable(name = "streamId") final Long streamId,
      @Valid @RequestBody final DeleteStreamDto deleteStreamDto,
      @AuthenticationPrincipal final FleenUser user) {
    return deleteStreamDto.isEvent()
      ? eventService.deleteEvent(streamId, deleteStreamDto, user)
      : liveBroadcastService.deleteLiveBroadcast(streamId, deleteStreamDto, user);
  }

  @PutMapping(value = "/process-join-request/{streamId}")
  public ProcessAttendeeRequestToJoinStreamResponse processAttendeeRequestToJoinStream(
      @PathVariable(name = "streamId") final Long streamId,
      @Valid @RequestBody final ProcessAttendeeRequestToJoinStreamDto processAttendeeRequestToJoinStreamDto,
      @AuthenticationPrincipal final FleenUser user) {
    return processAttendeeRequestToJoinStreamDto.isEvent()
      ? eventJoinService.processAttendeeRequestToJoinEvent(streamId, processAttendeeRequestToJoinStreamDto, user)
      : liveBroadcastJoinService.processAttendeeRequestToJoinLiveBroadcast(streamId, processAttendeeRequestToJoinStreamDto, user);
  }

  @PutMapping(value = "/reschedule/{streamId}")
  public RescheduleStreamResponse rescheduleStream(
      @PathVariable(name = "streamId") final Long streamId,
      @Valid @RequestBody final RescheduleStreamDto rescheduleStreamDto,
      @AuthenticationPrincipal final FleenUser user) {
    return rescheduleStreamDto.isEvent()
      ? eventService.rescheduleEvent(streamId, rescheduleStreamDto, user)
      : liveBroadcastService.rescheduleLiveBroadcast(streamId, rescheduleStreamDto, user);
  }

  @PutMapping(value = "/update-visibility/{streamId}")
  public UpdateStreamVisibilityResponse updateStreamVisibility(
      @PathVariable(name = "streamId") final Long streamId,
      @Valid @RequestBody final UpdateStreamVisibilityDto updateStreamVisibilityDto,
      @AuthenticationPrincipal final FleenUser user) {
    return updateStreamVisibilityDto.isEvent()
      ? eventService.updateEventVisibility(streamId, updateStreamVisibilityDto, user)
      : liveBroadcastService.updateLiveBroadcastVisibility(streamId, updateStreamVisibilityDto, user);
  }

  @PostMapping(value = "/add-attendee/{streamId}")
  public AddNewStreamAttendeeResponse addStreamAttendee(
      @PathVariable(name = "streamId") final Long streamId,
      @Valid @RequestBody final AddNewStreamAttendeeDto addNewStreamAttendeeDto,
      @AuthenticationPrincipal final FleenUser user) {
    if (addNewStreamAttendeeDto.isEvent()) {
      return eventJoinService.addEventAttendee(streamId, addNewStreamAttendeeDto, user);
    }
    throw new FailedOperationException();
  }

  @GetMapping(value = "/attendees/{streamId}")
  public StreamAttendeeSearchResult getStreamAttendees(
      @PathVariable(name = "streamId") final Long streamId,
      @SearchParam final StreamAttendeeSearchRequest streamAttendeeSearchRequest) {
    return streamAttendeeService.getStreamAttendees(streamId, streamAttendeeSearchRequest);
  }

  @GetMapping(value = "/total-by-me")
  public TotalStreamsCreatedByUserResponse countTotalStreamsCreatedByUser(
      @SearchParam final StreamTypeSearchRequest searchRequest,
      @AuthenticationPrincipal final FleenUser user) {
    return streamSearchService.countTotalStreamsByUser(searchRequest, user);
  }

  @GetMapping(value = "/total-attended-by-me")
  public TotalStreamsAttendedByUserResponse countTotalEventsAttendedByUser(
      @SearchParam final StreamTypeSearchRequest searchRequest,
      @AuthenticationPrincipal final FleenUser user) {
    return streamSearchService.countTotalStreamsAttendedByUser(searchRequest, user);
  }
}
