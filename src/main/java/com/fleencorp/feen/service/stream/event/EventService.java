package com.fleencorp.feen.service.stream.event;

import com.fleencorp.feen.constant.stream.StreamVisibility;
import com.fleencorp.feen.exception.base.FailedOperationException;
import com.fleencorp.feen.exception.calendar.CalendarNotFoundException;
import com.fleencorp.feen.model.domain.stream.FleenStream;
import com.fleencorp.feen.model.dto.event.CreateCalendarEventDto;
import com.fleencorp.feen.model.dto.event.CreateInstantCalendarEventDto;
import com.fleencorp.feen.model.response.stream.base.CreateStreamResponse;
import com.fleencorp.feen.model.response.stream.common.event.DataForCreateEventResponse;
import com.fleencorp.feen.model.security.FleenUser;

public interface EventService {

  DataForCreateEventResponse getDataForCreateEvent();

  CreateStreamResponse createEvent(CreateCalendarEventDto createEventDto, FleenUser user) throws CalendarNotFoundException;

  CreateStreamResponse createInstantEvent(CreateInstantCalendarEventDto createInstantEventDto, FleenUser user) throws CalendarNotFoundException;

  void sendInvitationToPendingAttendeesBasedOnCurrentStreamStatus(String calendarExternalId, FleenStream stream, StreamVisibility previousStreamVisibility)
    throws FailedOperationException;
}
