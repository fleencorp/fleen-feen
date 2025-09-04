package com.fleencorp.feen.stream.service.event;

import com.fleencorp.feen.calendar.exception.core.CalendarNotFoundException;
import com.fleencorp.feen.common.exception.FailedOperationException;
import com.fleencorp.feen.shared.security.RegisteredUser;
import com.fleencorp.feen.shared.stream.contract.IsAStream;
import com.fleencorp.feen.stream.constant.core.StreamVisibility;
import com.fleencorp.feen.stream.model.dto.event.CreateEventDto;
import com.fleencorp.feen.stream.model.dto.event.CreateInstantEventDto;
import com.fleencorp.feen.stream.model.response.base.CreateStreamResponse;
import com.fleencorp.feen.stream.model.response.common.event.DataForCreateEventResponse;

public interface EventService {

  DataForCreateEventResponse getDataForCreateEvent();

  CreateStreamResponse createEvent(CreateEventDto createEventDto, RegisteredUser user) throws CalendarNotFoundException;

  CreateStreamResponse createInstantEvent(CreateInstantEventDto createInstantEventDto, RegisteredUser user) throws CalendarNotFoundException;

  void sendInvitationToPendingAttendeesBasedOnCurrentStreamStatus(String calendarExternalId, IsAStream stream, StreamVisibility previousStreamVisibility)
    throws FailedOperationException;
}
