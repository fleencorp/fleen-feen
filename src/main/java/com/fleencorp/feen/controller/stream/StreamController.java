package com.fleencorp.feen.controller.stream;

import com.fleencorp.feen.model.dto.stream.attendance.JoinStreamDto;
import com.fleencorp.feen.model.dto.stream.attendance.NotAttendingStreamDto;
import com.fleencorp.feen.model.dto.stream.attendance.RequestToJoinStreamDto;
import com.fleencorp.feen.model.response.stream.attendance.JoinStreamResponse;
import com.fleencorp.feen.model.response.stream.attendance.NotAttendingStreamResponse;
import com.fleencorp.feen.model.response.stream.attendance.RequestToJoinStreamResponse;
import com.fleencorp.feen.model.security.FleenUser;
import com.fleencorp.feen.service.stream.join.EventJoinService;
import com.fleencorp.feen.service.stream.join.LiveBroadcastJoinService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/stream")
@PreAuthorize("hasAnyRole('ADMINISTRATOR', 'SUPER_ADMINISTRATOR', 'USER')")
public class StreamController {

  private final EventJoinService eventJoinService;
  private final LiveBroadcastJoinService liveBroadcastJoinService;

  public StreamController(
      final EventJoinService eventJoinService,
      final LiveBroadcastJoinService liveBroadcastJoinService) {
    this.eventJoinService = eventJoinService;
    this.liveBroadcastJoinService = liveBroadcastJoinService;
  }

  @PostMapping(value = "/join/{streamId}")
  public JoinStreamResponse joinStream(
      @PathVariable(name = "streamId") final Long streamId,
      @Valid @RequestBody final JoinStreamDto joinStreamDto,
      @AuthenticationPrincipal final FleenUser user) {
    return joinStreamDto.isEvent()
      ? eventJoinService.joinEvent(streamId, joinStreamDto, user)
      : liveBroadcastJoinService.joinLiveBroadcast(streamId, joinStreamDto, user);
  }

  @PostMapping(value = "/request-to-join/{streamId}")
  public RequestToJoinStreamResponse requestToJoinStream(
      @PathVariable(name = "streamId") final Long streamId,
      @Valid @RequestBody final RequestToJoinStreamDto requestToJoinStreamDto,
      @AuthenticationPrincipal final FleenUser user) {
    return requestToJoinStreamDto.isEvent()
      ? eventJoinService.requestToJoinEvent(streamId, requestToJoinStreamDto, user)
      : liveBroadcastJoinService.requestToJoinLiveBroadcast(streamId, requestToJoinStreamDto, user);
  }

  @PutMapping(value = "/not-attending/{streamId}")
  public NotAttendingStreamResponse notAttendingStream(
      @PathVariable(name = "streamId") final Long streamId,
      @Valid @RequestBody final NotAttendingStreamDto notAttendingStreamDto,
      @AuthenticationPrincipal final FleenUser user) {
    return notAttendingStreamDto.isEvent()
      ? eventJoinService.notAttendingEvent(streamId, notAttendingStreamDto, user)
      : liveBroadcastJoinService.notAttendingLiveBroadcast(streamId, user);
  }
}
