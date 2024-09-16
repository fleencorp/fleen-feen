package com.fleencorp.feen.controller.broadcast;

import com.fleencorp.base.model.view.search.SearchResultView;
import com.fleencorp.base.resolver.SearchParam;
import com.fleencorp.feen.model.dto.livebroadcast.RescheduleLiveBroadcastDto;
import com.fleencorp.feen.model.dto.stream.ProcessAttendeeRequestToJoinEventOrStreamDto;
import com.fleencorp.feen.model.dto.stream.UpdateEventOrStreamVisibilityDto;
import com.fleencorp.feen.model.request.search.stream.StreamAttendeeSearchRequest;
import com.fleencorp.feen.model.response.broadcast.*;
import com.fleencorp.feen.model.response.stream.EventOrStreamAttendeesResponse;
import com.fleencorp.feen.model.security.FleenUser;
import com.fleencorp.feen.service.stream.LiveBroadcastService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/user/live-stream")
@PreAuthorize("hasAnyRole('USER', 'ADMINISTRATOR', 'SUPER_ADMINISTRATOR')")
public class UserLiveBroadcastController {
  
  private final LiveBroadcastService liveBroadcastService;
  
  public UserLiveBroadcastController(final LiveBroadcastService liveBroadcastService) {
    this.liveBroadcastService = liveBroadcastService;
  }

  @PutMapping(value = "/reschedule/{streamId}")
  public RescheduleStreamResponse rescheduleStream(
      @PathVariable(name = "streamId") final Long streamId,
      @Valid @RequestBody final RescheduleLiveBroadcastDto rescheduleLiveBroadcastDto,
      @AuthenticationPrincipal final FleenUser user) {
    return liveBroadcastService.rescheduleLiveBroadcast(streamId, rescheduleLiveBroadcastDto, user);
  }

  @DeleteMapping(value = "/delete/{streamId}")
  public DeletedStreamResponse deleteStream(
      @PathVariable(name = "streamId") final Long streamId,
      @AuthenticationPrincipal final FleenUser user) {
    return liveBroadcastService.deleteStream(streamId, user);
  }

  @GetMapping(value = "/attendees/request-to-join/{streamId}")
  public SearchResultView getAttendeesRequestToJoin(
    @PathVariable(name = "streamId") final Long streamId,
    @AuthenticationPrincipal final FleenUser user,
    @SearchParam final StreamAttendeeSearchRequest searchRequest) {
    return liveBroadcastService.getAttendeeRequestsToJoinStream(streamId, searchRequest, user);
  }

  @PutMapping(value = "/process-join-request/{streamId}")
  public ProcessAttendeeRequestToJoinStreamResponse processAttendeeRequestToJoinEvent(
      @PathVariable(name = "streamId") final Long streamId,
      @Valid @RequestBody final ProcessAttendeeRequestToJoinEventOrStreamDto processAttendeeRequestToJoinEventOrStreamDto,
      @AuthenticationPrincipal final FleenUser user) {
    return liveBroadcastService.processAttendeeRequestToJoinStream(streamId, processAttendeeRequestToJoinEventOrStreamDto, user);
  }

  @PutMapping(value = "/update-visibility/{streamId}")
  public UpdateStreamVisibilityResponse updateStreamVisibility(
      @PathVariable(name = "streamId") final Long streamId,
      @Valid @RequestBody final UpdateEventOrStreamVisibilityDto updateEventOrStreamVisibilityDto,
      @AuthenticationPrincipal final FleenUser user) {
    return liveBroadcastService.updateStreamVisibility(streamId, updateEventOrStreamVisibilityDto, user);
  }

  @GetMapping(value = "/attendees/{streamId}")
  public EventOrStreamAttendeesResponse getStreamAttendees(
    @PathVariable(name = "streamId") final Long streamId,
    @AuthenticationPrincipal final FleenUser user) {
    return liveBroadcastService.getStreamAttendees(streamId, user);
  }

  @GetMapping(value = "/total-by-me")
  public TotalStreamsCreatedByUserResponse countTotalEventsCreatedByUser(
    @AuthenticationPrincipal final FleenUser user) {
    return liveBroadcastService.countTotalStreamsByUser(user);
  }

  @GetMapping(value = "/total-attended-by-me")
  public TotalStreamsAttendedByUserResponse countTotalEventsAttendedByUser(
    @AuthenticationPrincipal final FleenUser user) {
    return liveBroadcastService.countTotalStreamsAttended(user);
  }
}
