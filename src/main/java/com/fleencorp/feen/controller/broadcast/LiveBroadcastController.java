package com.fleencorp.feen.controller.broadcast;

import com.fleencorp.base.resolver.SearchParam;
import com.fleencorp.feen.constant.stream.StreamTimeType;
import com.fleencorp.feen.constant.stream.StreamType;
import com.fleencorp.feen.model.dto.livebroadcast.CreateLiveBroadcastDto;
import com.fleencorp.feen.model.dto.livebroadcast.UpdateLiveBroadcastDto;
import com.fleencorp.feen.model.dto.stream.attendance.JoinStreamDto;
import com.fleencorp.feen.model.dto.stream.attendance.RequestToJoinStreamDto;
import com.fleencorp.feen.model.request.search.calendar.EventSearchRequest;
import com.fleencorp.feen.model.response.stream.attendance.JoinStreamResponse;
import com.fleencorp.feen.model.response.stream.attendance.NotAttendingStreamResponse;
import com.fleencorp.feen.model.response.stream.attendance.RequestToJoinStreamResponse;
import com.fleencorp.feen.model.response.stream.base.CreateStreamResponse;
import com.fleencorp.feen.model.response.stream.base.UpdateStreamResponse;
import com.fleencorp.feen.model.response.stream.common.live.broadcast.DataForCreateLiveBroadcastResponse;
import com.fleencorp.feen.model.search.stream.common.StreamSearchResult;
import com.fleencorp.feen.model.security.FleenUser;
import com.fleencorp.feen.service.stream.LiveBroadcastService;
import com.fleencorp.feen.service.stream.join.LiveBroadcastJoinService;
import com.fleencorp.feen.service.stream.search.StreamSearchService;
import jakarta.validation.Valid;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/live-stream")
@PreAuthorize("hasAnyRole('USER', 'ADMINISTRATOR', 'SUPER_ADMINISTRATOR')")
public class LiveBroadcastController {

  private final LiveBroadcastService liveBroadcastService;
  private final LiveBroadcastJoinService liveBroadcastJoinService;
  private final StreamSearchService streamSearchService;

  public LiveBroadcastController(
      final LiveBroadcastService liveBroadcastService,
      final LiveBroadcastJoinService liveBroadcastJoinService,
      final StreamSearchService streamSearchService) {
    this.liveBroadcastService = liveBroadcastService;
    this.liveBroadcastJoinService = liveBroadcastJoinService;
    this.streamSearchService = streamSearchService;
  }

  @GetMapping(value = "/data-create-stream")
  @Cacheable(value = "data-required-to-create-stream")
  public DataForCreateLiveBroadcastResponse getDataCreateStream() {
    return liveBroadcastService.getDataForCreateLiveBroadcast();
  }

  @GetMapping(value = "/entries")
  public StreamSearchResult findEvents(
    @SearchParam final EventSearchRequest searchRequest,
    @AuthenticationPrincipal final FleenUser user) {
    searchRequest.setStreamType(StreamType.liveStream());
    return streamSearchService.findStreams(searchRequest, user);
  }

  @GetMapping(value = "/entries/type")
  public StreamSearchResult findEvents(
    @SearchParam final EventSearchRequest searchRequest,
    final StreamTimeType streamTimeType) {
    searchRequest.setStreamType(StreamType.liveStream());
    return streamSearchService.findStreams(searchRequest, streamTimeType);
  }

  @PostMapping(value = "/create")
  public CreateStreamResponse createLiveStream(
      @Valid @RequestBody final CreateLiveBroadcastDto createLiveBroadcastDto,
      @AuthenticationPrincipal final FleenUser user) {
    return liveBroadcastService.createLiveBroadcast(createLiveBroadcastDto, user);
  }

  @PreAuthorize("hasAnyRole('USER', 'ADMINISTRATOR', 'SUPER_ADMINISTRATOR')")
  @PutMapping(value = "/update/{streamId}")
  public UpdateStreamResponse updateLiveBroadcast(
      @PathVariable(name = "streamId") final Long streamId,
      @Valid @RequestBody final UpdateLiveBroadcastDto updateLiveBroadcastDto,
      @AuthenticationPrincipal final FleenUser user) {
    return liveBroadcastService.updateLiveBroadcast(streamId, updateLiveBroadcastDto, user);
  }

  @PutMapping(value = "/not-attending/{streamId}")
  public NotAttendingStreamResponse notAttendingStream(
      @PathVariable(name = "streamId") final Long streamId,
      @AuthenticationPrincipal final FleenUser user) {
    return liveBroadcastJoinService.notAttendingLiveBroadcast(streamId, user);
  }

  @PostMapping(value = "/join/{streamId}")
  public JoinStreamResponse joinStream(
      @PathVariable(name = "streamId") final Long streamId,
      @Valid @RequestBody final JoinStreamDto joinStreamDto,
      @AuthenticationPrincipal final FleenUser user) {
    return liveBroadcastJoinService.joinLiveBroadcast(streamId, joinStreamDto, user);
  }

  @PreAuthorize("hasAnyRole('USER', 'ADMINISTRATOR', 'SUPER_ADMINISTRATOR')")
  @PostMapping(value = "/request-to-join/{streamId}")
  public RequestToJoinStreamResponse requestToJoinStream(
      @PathVariable(name = "streamId") final Long streamId,
      @Valid @RequestBody final RequestToJoinStreamDto requestToJoinStreamDto,
      @AuthenticationPrincipal final FleenUser user) {
    return liveBroadcastJoinService.requestToJoinLiveBroadcast(streamId, requestToJoinStreamDto, user);
  }
}
