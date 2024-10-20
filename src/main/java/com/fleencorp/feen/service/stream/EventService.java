package com.fleencorp.feen.service.stream;

import com.fleencorp.feen.constant.stream.StreamTimeType;
import com.fleencorp.feen.model.dto.event.*;
import com.fleencorp.feen.model.dto.stream.JoinEventOrStreamDto;
import com.fleencorp.feen.model.dto.stream.ProcessAttendeeRequestToJoinEventOrStreamDto;
import com.fleencorp.feen.model.dto.stream.RequestToJoinEventOrStreamDto;
import com.fleencorp.feen.model.dto.stream.UpdateEventOrStreamVisibilityDto;
import com.fleencorp.feen.model.request.search.calendar.CalendarEventSearchRequest;
import com.fleencorp.feen.model.request.search.stream.StreamAttendeeSearchRequest;
import com.fleencorp.feen.model.response.event.*;
import com.fleencorp.feen.model.search.broadcast.request.RequestToJoinSearchResult;
import com.fleencorp.feen.model.search.event.EventSearchResult;
import com.fleencorp.feen.model.search.stream.attendee.StreamAttendeeSearchResult;
import com.fleencorp.feen.model.security.FleenUser;

public interface EventService {

  EventSearchResult findEvents(CalendarEventSearchRequest searchRequest, FleenUser user);

  EventSearchResult findMyEvents(CalendarEventSearchRequest searchRequest, FleenUser user);

  EventSearchResult findEvents(CalendarEventSearchRequest searchRequest, StreamTimeType streamTimeType);

  EventSearchResult findEventsAttendedByUser(CalendarEventSearchRequest searchRequest, FleenUser user);

  EventSearchResult findEventsAttendedWithAnotherUser(CalendarEventSearchRequest searchRequest, FleenUser user);

  StreamAttendeeSearchResult findEventAttendees(Long eventId, StreamAttendeeSearchRequest searchRequest);

  RetrieveEventResponse retrieveEvent(Long eventId, FleenUser user);

  CreateEventResponse createEvent(CreateCalendarEventDto createCalendarEventDto, FleenUser user);

  CreateEventResponse createInstantEvent(CreateInstantCalendarEventDto createInstantCalendarEventDto, FleenUser user);

  UpdateEventResponse updateEvent(Long eventId, UpdateCalendarEventDto updateCalendarEventDto, FleenUser user);

  DeletedEventResponse deleteEvent(Long eventId, FleenUser user);

  CancelEventResponse cancelEvent(Long eventId, FleenUser user);

  NotAttendingEventResponse notAttendingEvent(Long eventId, FleenUser user);

  RescheduleEventResponse rescheduleEvent(Long eventId, RescheduleCalendarEventDto rescheduleCalendarEventDto, FleenUser user);

  JoinEventResponse joinEvent(Long eventId, JoinEventOrStreamDto joinEventOrStreamDto, FleenUser user);

  RequestToJoinEventResponse requestToJoinEvent(Long eventId, RequestToJoinEventOrStreamDto requestToJoinEventOrStreamDto, FleenUser user);

  ProcessAttendeeRequestToJoinEventResponse processAttendeeRequestToJoinEvent(Long eventId, ProcessAttendeeRequestToJoinEventOrStreamDto processAttendeeRequestToJoinEventOrStreamDto, FleenUser user);

  UpdateEventVisibilityResponse updateEventVisibility(Long eventId, UpdateEventOrStreamVisibilityDto updateEventOrStreamVisibilityDto, FleenUser user);

  AddNewEventAttendeeResponse addEventAttendee(Long eventId, AddNewEventAttendeeDto addNewEventAttendeeDto, FleenUser user);

  RequestToJoinSearchResult getEventAttendeeRequestsToJoinEvent(Long eventId, StreamAttendeeSearchRequest searchRequest, FleenUser user);

  StreamAttendeeSearchResult getEventAttendees(Long eventId, StreamAttendeeSearchRequest searchRequest, FleenUser user);

  TotalEventsCreatedByUserResponse countTotalEventsByUser(FleenUser user);

  TotalEventsAttendedByUserResponse countTotalEventsAttended(FleenUser user);
}
