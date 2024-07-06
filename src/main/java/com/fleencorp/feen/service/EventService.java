package com.fleencorp.feen.service;

import com.fleencorp.base.model.view.search.SearchResultView;
import com.fleencorp.feen.constant.stream.StreamTimeType;
import com.fleencorp.feen.model.dto.event.*;
import com.fleencorp.feen.model.request.search.calendar.CalendarEventSearchRequest;
import com.fleencorp.feen.model.response.base.FleenFeenResponse;
import com.fleencorp.feen.model.response.event.*;
import com.fleencorp.feen.model.security.FleenUser;

public interface EventService {

  SearchResultView findEvents(CalendarEventSearchRequest searchRequest);

  SearchResultView findEvents(CalendarEventSearchRequest searchRequest, FleenUser user);

  SearchResultView findEvents(CalendarEventSearchRequest searchRequest, StreamTimeType streamTimeType);

  SearchResultView findEventsAttendedByUser(CalendarEventSearchRequest searchRequest, FleenUser user);

  SearchResultView findEventsAttendedWithAnotherUser(CalendarEventSearchRequest searchRequest, FleenUser user);

  RetrieveEventResponse retrieveEvent(Long eventId);

  CreateEventResponse createEvent(CreateCalendarEventDto createCalendarEventDto, FleenUser user);

  CreateEventResponse createInstantEvent(CreateInstantCalendarEventDto createInstantCalendarEventDto, FleenUser user);

  UpdateEventResponse updateEvent(Long eventId, UpdateCalendarEventDto updateCalendarEventDto, FleenUser user);

  DeleteEventResponse deleteEvent(Long eventId, FleenUser user);

  CancelEventResponse cancelEvent(Long eventId, FleenUser user);

  RescheduleEventResponse rescheduleEvent(Long eventId, RescheduleCalendarEventDto rescheduleCalendarEventDto, FleenUser user);

  FleenFeenResponse joinEvent(Long eventId, FleenUser user);

  RequestToJoinEventResponse requestToJoinEvent(Long eventId, RequestToJoinEventDto requestToJoinEventDto, FleenUser user);

  ProcessAttendeeRequestToJoinEventResponse processAttendeeRequestToJoinEvent(Long eventId, ProcessAttendeeRequestToJoinEventDto processAttendeeRequestToJoinEventDto, FleenUser user);

  UpdateEventVisibilityResponse updateEventVisibility(Long eventId, UpdateEventVisibilityDto updateEventVisibilityDto, FleenUser user);

  AddNewEventAttendeeResponse addEventAttendee(Long eventId, AddNewEventAttendeeDto addNewEventAttendeeDto, FleenUser user);

  EventAttendeesResponse getEventAttendees(Long eventId);

  TotalEventsCreatedByUserResponse countTotalEventsByUser(FleenUser user);

  TotalEventsAttendedByUserResponse countTotalEventsAttended(FleenUser user);
}
