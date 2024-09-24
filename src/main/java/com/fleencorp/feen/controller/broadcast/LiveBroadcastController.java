package com.fleencorp.feen.controller.broadcast;

import com.fleencorp.base.model.view.search.SearchResultView;
import com.fleencorp.base.resolver.SearchParam;
import com.fleencorp.feen.model.dto.livebroadcast.CreateLiveBroadcastDto;
import com.fleencorp.feen.model.dto.livebroadcast.UpdateLiveBroadcastDto;
import com.fleencorp.feen.model.dto.stream.RequestToJoinEventOrStreamDto;
import com.fleencorp.feen.model.request.search.stream.StreamAttendeeSearchRequest;
import com.fleencorp.feen.model.request.search.youtube.LiveBroadcastSearchRequest;
import com.fleencorp.feen.model.response.broadcast.*;
import com.fleencorp.feen.model.security.FleenUser;
import com.fleencorp.feen.service.stream.LiveBroadcastService;
import jakarta.validation.Valid;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/live-stream")
public class LiveBroadcastController {

  private final LiveBroadcastService liveBroadcastService;

  public LiveBroadcastController(
      final LiveBroadcastService liveBroadcastService) {
    this.liveBroadcastService = liveBroadcastService;
  }

  @PreAuthorize("hasAnyRole('USER', 'ADMINISTRATOR', 'SUPER_ADMINISTRATOR')")
  @GetMapping(value = "/data-create-stream")
  @Cacheable(value = "data-required-to-create-stream")
  public DataForCreateStreamResponse getDataCreateStream() {
    return liveBroadcastService.getDataForCreateStream();
  }

  @GetMapping(value = "/entries")
  public SearchResultView findLiveBroadcasts(
      @SearchParam final LiveBroadcastSearchRequest searchRequest,
      @AuthenticationPrincipal final FleenUser user) {
    return liveBroadcastService.findLiveBroadcasts(searchRequest, user);
  }

  @GetMapping(value = "/detail/{streamId}")
  public RetrieveStreamResponse findLiveBroadcast(
      @PathVariable(name = "streamId") final Long streamId) {
    return liveBroadcastService.retrieveStream(streamId);
  }

  @GetMapping(value = "/attendees/{streamId}")
  public SearchResultView findStreamAttendees(
    @PathVariable(name = "streamId") final Long streamId,
    @SearchParam final StreamAttendeeSearchRequest searchRequest) {
    return liveBroadcastService.findStreamAttendees(streamId, searchRequest);
  }

  @PreAuthorize("hasAnyRole('USER', 'ADMINISTRATOR', 'SUPER_ADMINISTRATOR')")
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

  @PreAuthorize("hasAnyRole('USER', 'ADMINISTRATOR', 'SUPER_ADMINISTRATOR')")
  @PutMapping(value = "/not-attending/{streamId}")
  public NotAttendingStreamResponse notAttendingStream(
      @PathVariable(name = "streamId") final Long streamId,
      @AuthenticationPrincipal final FleenUser user) {
    return liveBroadcastService.notAttendingStream(streamId, user);
  }

  @PreAuthorize("hasAnyRole('USER', 'ADMINISTRATOR', 'SUPER_ADMINISTRATOR')")
  @PostMapping(value = "/join/{streamId}")
  public JoinStreamResponse joinStream(
      @PathVariable(name = "streamId") final Long streamId,
      @AuthenticationPrincipal final FleenUser user) {
    return liveBroadcastService.joinStream(streamId, user);
  }

  @PreAuthorize("hasAnyRole('USER', 'ADMINISTRATOR', 'SUPER_ADMINISTRATOR')")
  @PostMapping(value = "/request-to-join/{streamId}")
  public RequestToJoinStreamResponse requestToJoinStream(
      @PathVariable(name = "streamId") final Long streamId,
      @Valid @RequestBody final RequestToJoinEventOrStreamDto requestToJoinEventOrStreamDto,
      @AuthenticationPrincipal final FleenUser user) {
    return liveBroadcastService.requestToJoinStream(streamId, requestToJoinEventOrStreamDto, user);
  }
}
