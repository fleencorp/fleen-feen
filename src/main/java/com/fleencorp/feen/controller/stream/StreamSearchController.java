package com.fleencorp.feen.controller.stream;

import com.fleencorp.base.resolver.SearchParam;
import com.fleencorp.feen.constant.stream.StreamTimeType;
import com.fleencorp.feen.constant.stream.StreamType;
import com.fleencorp.feen.model.request.search.calendar.EventSearchRequest;
import com.fleencorp.feen.model.request.search.stream.StreamAttendeeSearchRequest;
import com.fleencorp.feen.model.request.search.stream.StreamSearchRequest;
import com.fleencorp.feen.model.response.stream.base.RetrieveStreamResponse;
import com.fleencorp.feen.model.search.join.RequestToJoinSearchResult;
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
@RequestMapping(value = "/api/stream/search")
public class StreamSearchController {

  private final StreamSearchService streamSearchService;
  private final StreamAttendeeService streamAttendeeService;

  public StreamSearchController(
      final StreamSearchService streamSearchService,
      final StreamAttendeeService streamAttendeeService) {
    this.streamSearchService = streamSearchService;
    this.streamAttendeeService = streamAttendeeService;
  }

  @GetMapping(value = "")
  public StreamSearchResult findStreamsPublic(
      @SearchParam final StreamSearchRequest searchRequest,
      @AuthenticationPrincipal final FleenUser user) {
    searchRequest.setStreamType(StreamType.event());
    return streamSearchService.findStreamsPublic(searchRequest, user);
  }

  @GetMapping(value = "/type")
  public StreamSearchResult findStreams(
      @SearchParam final EventSearchRequest searchRequest,
      final StreamTimeType streamTimeType) {
    searchRequest.setStreamType(StreamType.event());
    return streamSearchService.findStreamsPublic(searchRequest, streamTimeType);
  }

  @GetMapping(value = "/detail/{streamId}")
  public RetrieveStreamResponse findStream(
      @PathVariable(name = "streamId") final Long streamId,
      @AuthenticationPrincipal final FleenUser user) {
    return streamSearchService.retrieveStream(streamId, user);
  }

  @PreAuthorize("hasAnyRole('USER', 'ADMINISTRATOR', 'SUPER_ADMINISTRATOR')")
  @GetMapping(value = "/mine")
  public StreamSearchResult findStreamsPrivate(
      @SearchParam final StreamSearchRequest searchRequest,
      @AuthenticationPrincipal final FleenUser user) {
    searchRequest.setDefaultStreamType();
    return streamSearchService.findStreamsPrivate(searchRequest, user);
  }

  @PreAuthorize("hasAnyRole('USER', 'ADMINISTRATOR', 'SUPER_ADMINISTRATOR')")
  @GetMapping(value = "/mine/detail/{streamId}")
  public RetrieveStreamResponse findMyStream(
      @PathVariable(name = "streamId") final Long streamId,
      @AuthenticationPrincipal final FleenUser user) {
    return streamSearchService.retrieveStream(streamId, user);
  }

  @GetMapping(value = "/attendees/{streamId}")
  public StreamAttendeeSearchResult getStreamAttendees(
    @PathVariable(name = "streamId") final Long streamId,
    @SearchParam final StreamAttendeeSearchRequest streamAttendeeSearchRequest) {
    return streamAttendeeService.getStreamAttendees(streamId, streamAttendeeSearchRequest);
  }

  @GetMapping(value = "/attendees-2/{streamId}")
  public StreamAttendeeSearchResult findStreamAttendees(
      @PathVariable(name = "streamId") final Long streamId,
      @SearchParam final StreamAttendeeSearchRequest searchRequest) {
    return streamAttendeeService.findStreamAttendees(streamId, searchRequest);
  }

  @PreAuthorize("hasAnyRole('USER', 'ADMINISTRATOR', 'SUPER_ADMINISTRATOR')")
  @GetMapping(value = "/attended-by-me")
  public StreamSearchResult findStreamsAttendedByUser(
      @SearchParam final StreamSearchRequest searchRequest,
      @AuthenticationPrincipal final FleenUser user) {
    return streamSearchService.findStreamsAttendedByUser(searchRequest, user);
  }

  @PreAuthorize("hasAnyRole('USER', 'ADMINISTRATOR', 'SUPER_ADMINISTRATOR')")
  @GetMapping(value = "/attended-with-user")
  public StreamSearchResult findEventsAttendedWithAnotherUser(
      @SearchParam final StreamSearchRequest searchRequest,
      @AuthenticationPrincipal final FleenUser user) {
    return streamSearchService.findStreamsAttendedWithAnotherUser(searchRequest, user);
  }

  @PreAuthorize("hasAnyRole('USER', 'ADMINISTRATOR', 'SUPER_ADMINISTRATOR')")
  @GetMapping(value = "/attendees/request-to-join/{streamId}")
  public RequestToJoinSearchResult findAttendeesRequestToJoin(
      @PathVariable(name = "streamId") final Long streamId,
      @AuthenticationPrincipal final FleenUser user,
      @SearchParam final StreamAttendeeSearchRequest streamAttendeeSearchRequest) {
    return streamAttendeeService.getAttendeeRequestsToJoinStream(streamId, streamAttendeeSearchRequest, user);
  }
}
