package com.fleencorp.feen.service.stream;

import com.fleencorp.feen.exception.base.FailedOperationException;
import com.fleencorp.feen.exception.calendar.CalendarNotFoundException;
import com.fleencorp.feen.exception.stream.*;
import com.fleencorp.feen.model.dto.event.CreateCalendarEventDto;
import com.fleencorp.feen.model.dto.event.CreateInstantCalendarEventDto;
import com.fleencorp.feen.model.dto.stream.base.RescheduleStreamDto;
import com.fleencorp.feen.model.dto.stream.base.UpdateStreamDto;
import com.fleencorp.feen.model.dto.stream.base.UpdateStreamVisibilityDto;
import com.fleencorp.feen.model.response.stream.base.*;
import com.fleencorp.feen.model.response.stream.common.event.DataForCreateEventResponse;
import com.fleencorp.feen.model.security.FleenUser;

public interface EventService {

  DataForCreateEventResponse getDataForCreateEvent();

  CreateStreamResponse createEvent(CreateCalendarEventDto createEventDto, FleenUser user) throws CalendarNotFoundException;

  CreateStreamResponse createInstantEvent(CreateInstantCalendarEventDto createInstantEventDto, FleenUser user) throws CalendarNotFoundException;

  UpdateStreamResponse updateEvent(Long eventId, UpdateStreamDto updateStreamDto, FleenUser user)
    throws CalendarNotFoundException, FleenStreamNotFoundException, StreamNotCreatedByUserException,
    StreamAlreadyHappenedException, StreamAlreadyCanceledException, FailedOperationException;

  DeleteStreamResponse deleteEvent(Long eventId, FleenUser user)
    throws FleenStreamNotFoundException, CalendarNotFoundException, StreamNotCreatedByUserException,
    CannotCancelOrDeleteOngoingStreamException, FailedOperationException;

  CancelStreamResponse cancelEvent(Long eventId, FleenUser user)
    throws FleenStreamNotFoundException, CalendarNotFoundException, StreamNotCreatedByUserException,
    StreamAlreadyHappenedException, StreamAlreadyCanceledException, CannotCancelOrDeleteOngoingStreamException, FailedOperationException;

  RescheduleStreamResponse rescheduleEvent(Long eventId, RescheduleStreamDto rescheduleStreamDto, FleenUser user)
    throws FleenStreamNotFoundException, CalendarNotFoundException, StreamNotCreatedByUserException,
    StreamAlreadyHappenedException, StreamAlreadyCanceledException, FailedOperationException;

  UpdateStreamVisibilityResponse updateEventVisibility(Long eventId, UpdateStreamVisibilityDto updateStreamVisibilityDto, FleenUser user)
    throws FleenStreamNotFoundException, CalendarNotFoundException, StreamNotCreatedByUserException,
    StreamAlreadyHappenedException, StreamAlreadyCanceledException, CannotCancelOrDeleteOngoingStreamException,
    FailedOperationException;
}
