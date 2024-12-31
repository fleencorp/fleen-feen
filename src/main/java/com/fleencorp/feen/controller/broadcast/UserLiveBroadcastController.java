package com.fleencorp.feen.controller.broadcast;

import com.fleencorp.base.resolver.SearchParam;
import com.fleencorp.feen.model.dto.livebroadcast.RescheduleLiveBroadcastDto;
import com.fleencorp.feen.model.dto.stream.attendance.ProcessAttendeeRequestToJoinStreamDto;
import com.fleencorp.feen.model.dto.stream.base.UpdateStreamVisibilityDto;
import com.fleencorp.feen.model.request.search.stream.StreamAttendeeSearchRequest;
import com.fleencorp.feen.model.response.stream.attendance.ProcessAttendeeRequestToJoinStreamResponse;
import com.fleencorp.feen.model.response.stream.base.CancelStreamResponse;
import com.fleencorp.feen.model.response.stream.base.DeleteStreamResponse;
import com.fleencorp.feen.model.response.stream.base.RescheduleStreamResponse;
import com.fleencorp.feen.model.response.stream.base.UpdateStreamVisibilityResponse;
import com.fleencorp.feen.model.search.broadcast.request.RequestToJoinSearchResult;
import com.fleencorp.feen.model.security.FleenUser;
import com.fleencorp.feen.service.stream.LiveBroadcastService;
import com.fleencorp.feen.service.stream.attendee.StreamAttendeeService;
import com.fleencorp.feen.service.stream.join.LiveBroadcastJoinService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/user/live-stream")
@PreAuthorize("hasAnyRole('USER', 'ADMINISTRATOR', 'SUPER_ADMINISTRATOR')")
public class UserLiveBroadcastController {
  
  private final LiveBroadcastService liveBroadcastService;
  private final LiveBroadcastJoinService liveBroadcastJoinService;
  private final StreamAttendeeService streamAttendeeService;
  
  public UserLiveBroadcastController(
      final LiveBroadcastService liveBroadcastService,
      final LiveBroadcastJoinService liveBroadcastJoinService,
      final StreamAttendeeService streamAttendeeService) {
    this.liveBroadcastService = liveBroadcastService;
    this.liveBroadcastJoinService = liveBroadcastJoinService;
    this.streamAttendeeService = streamAttendeeService;
  }

  @PutMapping(value = "/reschedule/{streamId}")
  public RescheduleStreamResponse rescheduleStream(
      @PathVariable(name = "streamId") final Long streamId,
      @Valid @RequestBody final RescheduleLiveBroadcastDto rescheduleLiveBroadcastDto,
      @AuthenticationPrincipal final FleenUser user) {
    return liveBroadcastService.rescheduleLiveBroadcast(streamId, rescheduleLiveBroadcastDto, user);
  }

  @DeleteMapping(value = "/delete/{streamId}")
  public DeleteStreamResponse deleteStream(
      @PathVariable(name = "streamId") final Long streamId,
      @AuthenticationPrincipal final FleenUser user) {
    return liveBroadcastService.deleteLiveBroadcast(streamId, user);
  }

  @PutMapping(value = "/delete/{streamId}")
  public CancelStreamResponse cancelStream(
      @PathVariable(name = "streamId") final Long streamId,
      @AuthenticationPrincipal final FleenUser user) {
    return liveBroadcastService.cancelLiveBroadcast(streamId, user);
  }

  @GetMapping(value = "/attendees/request-to-join/{streamId}")
  public RequestToJoinSearchResult getAttendeesRequestToJoin(
      @PathVariable(name = "streamId") final Long streamId,
      @AuthenticationPrincipal final FleenUser user,
      @SearchParam final StreamAttendeeSearchRequest searchRequest) {
    return streamAttendeeService.getAttendeeRequestsToJoinStream(streamId, searchRequest, user);
  }

  @PutMapping(value = "/process-join-request/{streamId}")
  public ProcessAttendeeRequestToJoinStreamResponse processAttendeeRequestToJoinEvent(
      @PathVariable(name = "streamId") final Long streamId,
      @Valid @RequestBody final ProcessAttendeeRequestToJoinStreamDto processAttendeeRequestToJoinStreamDto,
      @AuthenticationPrincipal final FleenUser user) {
    return liveBroadcastJoinService.processAttendeeRequestToJoinLiveBroadcast(streamId, processAttendeeRequestToJoinStreamDto, user);
  }

  @PutMapping(value = "/update-visibility/{streamId}")
  public UpdateStreamVisibilityResponse updateStreamVisibility(
      @PathVariable(name = "streamId") final Long streamId,
      @Valid @RequestBody final UpdateStreamVisibilityDto updateStreamVisibilityDto,
      @AuthenticationPrincipal final FleenUser user) {
    return liveBroadcastService.updateLiveBroadcastVisibility(streamId, updateStreamVisibilityDto, user);
  }
}
