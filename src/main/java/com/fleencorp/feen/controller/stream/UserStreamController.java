package com.fleencorp.feen.controller.stream;

import com.fleencorp.base.resolver.SearchParam;
import com.fleencorp.feen.model.request.search.stream.StreamAttendeeSearchRequest;
import com.fleencorp.feen.model.search.stream.attendee.StreamAttendeeSearchResult;
import com.fleencorp.feen.service.stream.attendee.StreamAttendeeService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/user/stream")
@PreAuthorize("hasAnyRole('ADMINISTRATOR', 'SUPER_ADMINISTRATOR', 'USER')")
public class UserStreamController {

  private final StreamAttendeeService streamAttendeeService;

  public UserStreamController(final StreamAttendeeService streamAttendeeService) {
    this.streamAttendeeService = streamAttendeeService;
  }

  @GetMapping(value = "/attendees/{streamId}")
  public StreamAttendeeSearchResult getStreamAttendees(
    @PathVariable(name = "streamId") final Long streamId,
    @SearchParam final StreamAttendeeSearchRequest streamAttendeeSearchRequest) {
    return streamAttendeeService.getStreamAttendees(streamId, streamAttendeeSearchRequest);
  }
}
