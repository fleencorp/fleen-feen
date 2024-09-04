package com.fleencorp.feen.controller.broadcast;

import com.fleencorp.base.model.view.search.SearchResultView;
import com.fleencorp.base.resolver.SearchParam;
import com.fleencorp.feen.model.dto.stream.ProcessAttendeeRequestToJoinEventOrStreamDto;
import com.fleencorp.feen.model.dto.stream.UpdateEventOrStreamVisibilityDto;
import com.fleencorp.feen.model.request.search.stream.StreamAttendeeSearchRequest;
import com.fleencorp.feen.model.response.broadcast.DeletedStreamResponse;
import com.fleencorp.feen.model.response.broadcast.ProcessAttendeeRequestToJoinStreamResponse;
import com.fleencorp.feen.model.response.broadcast.UpdateStreamVisibilityResponse;
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
  
  public UserLiveBroadcastController(LiveBroadcastService liveBroadcastService) {
    this.liveBroadcastService = liveBroadcastService;
  }

  @DeleteMapping(value = "/delete/{streamId}")
  public DeletedStreamResponse deleteEvent(
    @PathVariable(name = "streamId") final Long streamId,
    @AuthenticationPrincipal final FleenUser user) {
    return liveBroadcastService.deleteStream(streamId, user);
  }

  @PutMapping(value = "/process-join-request/{streamId}")
  public ProcessAttendeeRequestToJoinStreamResponse processAttendeeRequestToJoinEvent(
    @PathVariable(name = "streamId") final Long streamId,
    @Valid @RequestBody final ProcessAttendeeRequestToJoinEventOrStreamDto processAttendeeRequestToJoinEventOrStreamDto,
    @AuthenticationPrincipal final FleenUser user) {
    return liveBroadcastService.processAttendeeRequestToJoinStream(streamId, processAttendeeRequestToJoinEventOrStreamDto, user);
  }

  @GetMapping(value = "/attendees/request-to-join/{streamId}")
  public SearchResultView getAttendeesRequestToJoin(
    @PathVariable(name = "streamId") final Long streamId,
    @AuthenticationPrincipal final FleenUser user,
    @SearchParam final StreamAttendeeSearchRequest searchRequest) {
    return liveBroadcastService.getAttendeeRequestsToJoinStream(streamId, searchRequest, user);
  }

  @PutMapping(value = "/update-visibility/{streamId}")
  public UpdateStreamVisibilityResponse updateEventVisibility(
    @PathVariable(name = "streamId") final Long streamId,
    @Valid @RequestBody final UpdateEventOrStreamVisibilityDto updateEventOrStreamVisibilityDto,
    @AuthenticationPrincipal final FleenUser user) {
    return liveBroadcastService.updateStreamVisibility(streamId, updateEventOrStreamVisibilityDto, user);
  }
}
