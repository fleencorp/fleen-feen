package com.fleencorp.feen.controller.stream;

import com.fleencorp.base.resolver.SearchParam;
import com.fleencorp.feen.exception.base.FailedOperationException;
import com.fleencorp.feen.model.dto.event.AddNewStreamAttendeeDto;
import com.fleencorp.feen.model.dto.stream.attendance.ProcessAttendeeRequestToJoinStreamDto;
import com.fleencorp.feen.model.dto.stream.base.*;
import com.fleencorp.feen.model.request.search.stream.type.StreamTypeSearchRequest;
import com.fleencorp.feen.model.response.stream.attendance.ProcessAttendeeRequestToJoinStreamResponse;
import com.fleencorp.feen.model.response.stream.base.*;
import com.fleencorp.feen.model.response.stream.common.AddNewStreamAttendeeResponse;
import com.fleencorp.feen.model.response.stream.statistic.TotalStreamsAttendedByUserResponse;
import com.fleencorp.feen.model.response.stream.statistic.TotalStreamsCreatedByUserResponse;
import com.fleencorp.feen.model.security.FleenUser;
import com.fleencorp.feen.service.stream.common.CommonStreamJoinService;
import com.fleencorp.feen.service.stream.common.CommonStreamService;
import com.fleencorp.feen.service.stream.join.EventJoinService;
import com.fleencorp.feen.service.stream.search.StreamSearchService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/stream")
@PreAuthorize("hasAnyRole('ADMINISTRATOR', 'SUPER_ADMINISTRATOR', 'USER')")
public class UserStreamController {

  private final CommonStreamService commonStreamService;
  private final CommonStreamJoinService commonStreamJoinService;
  private final EventJoinService eventJoinService;
  private final StreamSearchService streamSearchService;

  public UserStreamController(
      final CommonStreamService commonStreamService,
      final CommonStreamJoinService commonStreamJoinService,
      final EventJoinService eventJoinService,
      final StreamSearchService streamSearchService) {
    this.commonStreamService = commonStreamService;
    this.commonStreamJoinService = commonStreamJoinService;
    this.eventJoinService = eventJoinService;
    this.streamSearchService = streamSearchService;
  }

  @PutMapping(value = "/update/{streamId}")
  public UpdateStreamResponse updateStream(
      @PathVariable(name = "streamId") final Long streamId,
      @Valid @RequestBody final UpdateStreamDto updateStreamDto,
      @AuthenticationPrincipal final FleenUser user) {
    return commonStreamService.updateStream(streamId, updateStreamDto, user);
  }

  @PutMapping(value = "/cancel/{streamId}")
  public CancelStreamResponse cancelStream(
      @PathVariable(name = "streamId") final Long streamId,
      @Valid @RequestBody final CancelStreamDto cancelStreamDto,
      @AuthenticationPrincipal final FleenUser user) {
    return commonStreamService.cancelStream(streamId, cancelStreamDto, user);
  }

  @PutMapping(value = "/delete/{streamId}")
  public DeleteStreamResponse deleteStream(
      @PathVariable(name = "streamId") final Long streamId,
      @Valid @RequestBody final DeleteStreamDto deleteStreamDto,
      @AuthenticationPrincipal final FleenUser user) {
    return commonStreamService.deleteStream(streamId, deleteStreamDto, user);
  }

  @PutMapping(value = "/process-join-request/{streamId}")
  public ProcessAttendeeRequestToJoinStreamResponse processAttendeeRequestToJoinStream(
      @PathVariable(name = "streamId") final Long streamId,
      @Valid @RequestBody final ProcessAttendeeRequestToJoinStreamDto processAttendeeRequestToJoinStreamDto,
      @AuthenticationPrincipal final FleenUser user) {
    return commonStreamJoinService.processAttendeeRequestToJoinStream(streamId, processAttendeeRequestToJoinStreamDto, user);
  }

  @PutMapping(value = "/reschedule/{streamId}")
  public RescheduleStreamResponse rescheduleStream(
      @PathVariable(name = "streamId") final Long streamId,
      @Valid @RequestBody final RescheduleStreamDto rescheduleStreamDto,
      @AuthenticationPrincipal final FleenUser user) {
    return commonStreamService.rescheduleStream(streamId, rescheduleStreamDto, user);
  }

  @PutMapping(value = "/update-visibility/{streamId}")
  public UpdateStreamVisibilityResponse updateStreamVisibility(
      @PathVariable(name = "streamId") final Long streamId,
      @Valid @RequestBody final UpdateStreamVisibilityDto updateStreamVisibilityDto,
      @AuthenticationPrincipal final FleenUser user) {
    return commonStreamService.updateStreamVisibility(streamId, updateStreamVisibilityDto, user);
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
