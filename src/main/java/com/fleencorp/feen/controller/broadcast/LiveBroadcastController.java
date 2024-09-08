package com.fleencorp.feen.controller.broadcast;

import com.fleencorp.base.model.view.search.SearchResultView;
import com.fleencorp.base.resolver.SearchParam;
import com.fleencorp.feen.model.dto.livebroadcast.CreateLiveBroadcastDto;
import com.fleencorp.feen.model.dto.livebroadcast.UpdateLiveBroadcastDto;
import com.fleencorp.feen.model.request.search.youtube.LiveBroadcastSearchRequest;
import com.fleencorp.feen.model.response.broadcast.CreateStreamResponse;
import com.fleencorp.feen.model.response.broadcast.NotAttendingStreamResponse;
import com.fleencorp.feen.model.response.broadcast.RetrieveStreamResponse;
import com.fleencorp.feen.model.response.broadcast.UpdateStreamResponse;
import com.fleencorp.feen.model.response.stream.DataForCreateStreamResponse;
import com.fleencorp.feen.model.security.FleenUser;
import com.fleencorp.feen.service.stream.LiveBroadcastService;
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

  public LiveBroadcastController(
      final LiveBroadcastService liveBroadcastService) {
    this.liveBroadcastService = liveBroadcastService;
  }

  @GetMapping(value = "/data-create-stream")
  @Cacheable(value = "data-required-to-create-stream")
  public DataForCreateStreamResponse getDataCreateStream() {
    return liveBroadcastService.getDataForCreateStream();
  }

  @GetMapping(value = "/entries")
  public SearchResultView findLiveBroadcasts(
    @SearchParam final LiveBroadcastSearchRequest searchRequest) {
    return liveBroadcastService.findLiveBroadcasts(searchRequest);
  }

  @GetMapping(value = "/detail/{streamId}")
  public RetrieveStreamResponse findLiveBroadcast(
    @PathVariable(name = "streamId") final Long streamId) {
    return liveBroadcastService.retrieveStream(streamId);
  }

  @PostMapping(value = "/create")
  public CreateStreamResponse createLiveStream(
      @Valid @RequestBody final CreateLiveBroadcastDto createLiveBroadcastDto,
      @AuthenticationPrincipal final FleenUser user) {
    return liveBroadcastService.createLiveBroadcast(createLiveBroadcastDto, user);
  }

  @PutMapping(value = "/update/{streamId}")
  public UpdateStreamResponse updateLiveBroadcast(
      @PathVariable(name = "streamId") final Long streamId,
      @Valid @RequestBody final UpdateLiveBroadcastDto updateLiveBroadcastDto,
      @AuthenticationPrincipal final FleenUser user) {
    return liveBroadcastService.updateLiveBroadcast(streamId, updateLiveBroadcastDto, user);
  }

  @PutMapping(value = "/not-attending/{streamId}")
  public NotAttendingStreamResponse notAttendingEvent(
    @PathVariable(name = "streamId") final Long streamId,
    @AuthenticationPrincipal final FleenUser user) {
    return liveBroadcastService.notAttendingStream(streamId, user);
  }
}
