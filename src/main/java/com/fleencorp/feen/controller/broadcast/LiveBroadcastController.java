package com.fleencorp.feen.controller.broadcast;

import com.fleencorp.feen.model.dto.livebroadcast.CreateLiveBroadcastDto;
import com.fleencorp.feen.model.response.broadcast.CreateStreamResponse;
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

  @PostMapping(value = "/create")
  public CreateStreamResponse createLiveStream(
      @Valid @RequestBody CreateLiveBroadcastDto createLiveBroadcastDto,
      @AuthenticationPrincipal FleenUser user) {
    return liveBroadcastService.createLiveBroadcast(createLiveBroadcastDto, user);
  }
}
