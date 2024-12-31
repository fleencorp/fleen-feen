package com.fleencorp.feen.controller.stream;

import com.fleencorp.base.resolver.SearchParam;
import com.fleencorp.feen.model.request.search.stream.StreamAttendeeSearchRequest;
import com.fleencorp.feen.model.request.search.stream.StreamSearchRequest;
import com.fleencorp.feen.model.request.search.stream.type.StreamTypeSearchRequest;
import com.fleencorp.feen.model.response.stream.statistic.TotalStreamsAttendedByUserResponse;
import com.fleencorp.feen.model.response.stream.statistic.TotalStreamsCreatedByUserResponse;
import com.fleencorp.feen.model.search.broadcast.request.RequestToJoinSearchResult;
import com.fleencorp.feen.model.search.stream.attendee.StreamAttendeeSearchResult;
import com.fleencorp.feen.model.search.stream.common.StreamSearchResult;
import com.fleencorp.feen.model.security.FleenUser;
import com.fleencorp.feen.service.stream.attendee.StreamAttendeeService;
import com.fleencorp.feen.service.stream.search.StreamSearchService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/stream")
@PreAuthorize("hasAnyRole('ADMINISTRATOR', 'SUPER_ADMINISTRATOR', 'USER')")
public class StreamController {

  private final StreamSearchService streamService;
  private final StreamAttendeeService streamAttendeeService;

  public StreamController(
    final StreamSearchService streamService,
    final StreamAttendeeService streamAttendeeService) {
    this.streamService = streamService;
    this.streamAttendeeService = streamAttendeeService;
  }

  @GetMapping(value = "/entries")
  public StreamSearchResult findMyStreams(
      @SearchParam final StreamSearchRequest searchRequest,
      @AuthenticationPrincipal final FleenUser user) {
    return streamService.findMyStreams(searchRequest, user);
  }

  @GetMapping(value = "/attended-by-me")
  public StreamSearchResult findStreamsAttendedByUser(
      @SearchParam final StreamSearchRequest searchRequest,
      @AuthenticationPrincipal final FleenUser user) {
    return streamService.findStreamsAttendedByUser(searchRequest, user);
  }

  @GetMapping(value = "/attended-with-user")
  public StreamSearchResult findEventsAttendedWithAnotherUser(
      @SearchParam final StreamSearchRequest searchRequest,
      @AuthenticationPrincipal final FleenUser user) {
    return streamService.findStreamsAttendedWithAnotherUser(searchRequest, user);
  }

  @GetMapping(value = "/attendees/{streamId}")
  public StreamAttendeeSearchResult findStreamAttendees(
      @PathVariable(name = "streamId") final Long streamId,
      @SearchParam final StreamAttendeeSearchRequest searchRequest) {
    return streamAttendeeService.findStreamAttendees(streamId, searchRequest);
  }

  @GetMapping(value = "/attendees/request-to-join/{streamId}")
  public RequestToJoinSearchResult findAttendeesRequestToJoin(
      @PathVariable(name = "streamId") final Long streamId,
      @AuthenticationPrincipal final FleenUser user,
      @SearchParam final StreamAttendeeSearchRequest streamAttendeeSearchRequest) {
    return streamAttendeeService.getAttendeeRequestsToJoinStream(streamId, streamAttendeeSearchRequest, user);
  }

  @GetMapping(value = "/total-by-me")
  public TotalStreamsCreatedByUserResponse countTotalStreamsCreatedByUser(
    @SearchParam final StreamTypeSearchRequest searchRequest,
    @AuthenticationPrincipal final FleenUser user) {
    return streamService.countTotalStreamsByUser(searchRequest, user);
  }

  @GetMapping(value = "/total-attended-by-me")
  public TotalStreamsAttendedByUserResponse countTotalEventsAttendedByUser(
    @SearchParam final StreamTypeSearchRequest searchRequest,
    @AuthenticationPrincipal final FleenUser user) {
    return streamService.countTotalStreamsAttendedByUser(searchRequest, user);
  }
}
