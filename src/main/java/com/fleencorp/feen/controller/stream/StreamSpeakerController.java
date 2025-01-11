package com.fleencorp.feen.controller.stream;

import com.fleencorp.base.resolver.SearchParam;
import com.fleencorp.feen.model.dto.stream.base.DeleteStreamSpeakerDto;
import com.fleencorp.feen.model.dto.stream.speaker.AddStreamSpeakerDto;
import com.fleencorp.feen.model.dto.stream.speaker.UpdateStreamSpeakerDto;
import com.fleencorp.feen.model.request.search.stream.StreamSpeakerSearchRequest;
import com.fleencorp.feen.model.response.stream.speaker.AddStreamSpeakerResponse;
import com.fleencorp.feen.model.response.stream.speaker.DeleteStreamSpeakerResponse;
import com.fleencorp.feen.model.response.stream.speaker.GetStreamSpeakersResponse;
import com.fleencorp.feen.model.response.stream.speaker.UpdateStreamSpeakerResponse;
import com.fleencorp.feen.model.search.stream.speaker.StreamSpeakerSearchResult;
import com.fleencorp.feen.model.security.FleenUser;
import com.fleencorp.feen.service.stream.speaker.StreamSpeakerService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/stream-speaker")
public class StreamSpeakerController {

  private final StreamSpeakerService streamSpeakerService;

  public StreamSpeakerController(final StreamSpeakerService streamSpeakerService) {
    this.streamSpeakerService = streamSpeakerService;
  }

  @GetMapping(value = "/find-speakers")
  public StreamSpeakerSearchResult findStreamSpeakers(
      @SearchParam final StreamSpeakerSearchRequest searchRequest) {
    return streamSpeakerService.findSpeakers(searchRequest);
  }

  @GetMapping(value = "/entries/{streamId}")
  public GetStreamSpeakersResponse getStreamSpeakers(
      @PathVariable(name = "streamId") final Long streamId) {
    return streamSpeakerService.getSpeakers(streamId);
  }

  @PreAuthorize("isFullyAuthenticated()")
  @PostMapping(value = "/add/{streamId}")
  public AddStreamSpeakerResponse addStreamSpeaker(
      @PathVariable(name = "streamId") final Long streamId,
      @Valid @RequestBody final AddStreamSpeakerDto addStreamSpeakerDto,
      @AuthenticationPrincipal final FleenUser user) {
    return streamSpeakerService.addSpeakers(streamId, addStreamSpeakerDto, user);
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
  @DeleteMapping(value = "/delete/{streamId}")
  public DeleteStreamSpeakerResponse deleteStreamSpeaker(
      @PathVariable(name = "streamId") final Long streamId,
      @Valid @RequestBody final DeleteStreamSpeakerDto deleteStreamSpeakerDto,
      @AuthenticationPrincipal final FleenUser user) {
    return streamSpeakerService.deleteSpeakers(streamId, deleteStreamSpeakerDto, user);
  }
}
