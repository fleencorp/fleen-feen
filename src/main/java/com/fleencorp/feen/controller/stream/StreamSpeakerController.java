package com.fleencorp.feen.controller.stream;

import com.fleencorp.base.resolver.SearchParam;
import com.fleencorp.feen.model.dto.stream.base.RemoveStreamSpeakerDto;
import com.fleencorp.feen.model.dto.stream.speaker.MarkAsStreamSpeakerDto;
import com.fleencorp.feen.model.dto.stream.speaker.UpdateStreamSpeakerDto;
import com.fleencorp.feen.model.request.search.stream.StreamSpeakerSearchRequest;
import com.fleencorp.feen.model.response.stream.speaker.MarkAsStreamSpeakerResponse;
import com.fleencorp.feen.model.response.stream.speaker.RemoveStreamSpeakerResponse;
import com.fleencorp.feen.model.response.stream.speaker.UpdateStreamSpeakerResponse;
import com.fleencorp.feen.model.search.stream.speaker.StreamSpeakerSearchResult;
import com.fleencorp.feen.model.security.FleenUser;
import com.fleencorp.feen.service.stream.speaker.StreamSpeakerService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/stream/speaker")
public class StreamSpeakerController {

  private final StreamSpeakerService streamSpeakerService;

  public StreamSpeakerController(final StreamSpeakerService streamSpeakerService) {
    this.streamSpeakerService = streamSpeakerService;
  }

  @PreAuthorize("isFullyAuthenticated()")
  @GetMapping(value = "/search/{streamId}")
  public StreamSpeakerSearchResult findStreamSpeakers(
      @PathVariable(name = "streamId") final Long streamId,
      @SearchParam final StreamSpeakerSearchRequest searchRequest,
      @AuthenticationPrincipal final FleenUser user) {
    return streamSpeakerService.findSpeakers(streamId, searchRequest, user);
  }

  @PreAuthorize("isFullyAuthenticated()")
  @GetMapping(value = "/entries/{streamId}")
  public StreamSpeakerSearchResult getStreamSpeakers(
      @PathVariable(name = "streamId") final Long streamId,
      @SearchParam final StreamSpeakerSearchRequest searchRequest,
      @AuthenticationPrincipal final FleenUser user) {
    return streamSpeakerService.findStreamSpeakers(streamId, searchRequest, user);
  }

  @PreAuthorize("isFullyAuthenticated()")
  @PostMapping(value = "/mark-as-speaker/{streamId}")
  public MarkAsStreamSpeakerResponse markAsStreamSpeaker(
      @PathVariable(name = "streamId") final Long streamId,
      @Valid @RequestBody final MarkAsStreamSpeakerDto markAsStreamSpeakerDto,
      @AuthenticationPrincipal final FleenUser user) {
    return streamSpeakerService.markAsSpeaker(streamId, markAsStreamSpeakerDto, user);
  }

  @PreAuthorize("isFullyAuthenticated()")
  @PutMapping(value = "/update/{streamId}")
  public UpdateStreamSpeakerResponse updateStreamSpeaker(
      @PathVariable(name = "streamId") final Long streamId,
      @Valid @RequestBody final UpdateStreamSpeakerDto updateStreamSpeakerDto,
      @AuthenticationPrincipal final FleenUser user) {
    return streamSpeakerService.updateSpeakers(streamId, updateStreamSpeakerDto, user);
  }

  @PreAuthorize("isFullyAuthenticated()")
  @PutMapping(value = "/remove/{streamId}")
  public RemoveStreamSpeakerResponse removeStreamSpeaker(
      @PathVariable(name = "streamId") final Long streamId,
      @Valid @RequestBody final RemoveStreamSpeakerDto removeStreamSpeakerDto,
      @AuthenticationPrincipal final FleenUser user) {
    return streamSpeakerService.removeSpeakers(streamId, removeStreamSpeakerDto, user);
  }
}
