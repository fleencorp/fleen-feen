package com.fleencorp.feen.service.stream.event;

import com.fleencorp.feen.constant.stream.StreamVisibility;
import com.fleencorp.feen.exception.base.FailedOperationException;
import com.fleencorp.feen.exception.calendar.CalendarNotFoundException;
import com.fleencorp.feen.model.domain.stream.FleenStream;
import com.fleencorp.feen.model.dto.event.CreateEventDto;
import com.fleencorp.feen.model.dto.event.CreateInstantEventDto;
import com.fleencorp.feen.model.response.stream.base.CreateStreamResponse;
import com.fleencorp.feen.model.response.stream.common.event.DataForCreateEventResponse;
import com.fleencorp.feen.user.security.RegisteredUser;

public interface EventService {

  DataForCreateEventResponse getDataForCreateEvent();

  CreateStreamResponse createEvent(CreateEventDto createEventDto, RegisteredUser user) throws CalendarNotFoundException;

  CreateStreamResponse createInstantEvent(CreateInstantEventDto createInstantEventDto, RegisteredUser user) throws CalendarNotFoundException;

  void sendInvitationToPendingAttendeesBasedOnCurrentStreamStatus(String calendarExternalId, FleenStream stream, StreamVisibility previousStreamVisibility)
    throws FailedOperationException;
}
