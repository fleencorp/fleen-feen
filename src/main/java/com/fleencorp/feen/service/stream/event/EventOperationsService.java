package com.fleencorp.feen.service.stream.event;

import com.fleencorp.feen.constant.stream.StreamVisibility;
import com.fleencorp.feen.exception.base.FailedOperationException;
import com.fleencorp.feen.exception.calendar.CalendarNotFoundException;
import com.fleencorp.feen.exception.stream.StreamNotFoundException;
import com.fleencorp.feen.exception.stream.core.StreamAlreadyCanceledException;
import com.fleencorp.feen.exception.stream.core.StreamAlreadyHappenedException;
import com.fleencorp.feen.exception.stream.core.StreamNotCreatedByUserException;
import com.fleencorp.feen.model.domain.stream.FleenStream;
import com.fleencorp.feen.model.domain.stream.StreamAttendee;
import com.fleencorp.feen.model.dto.event.AddNewStreamAttendeeDto;
import com.fleencorp.feen.model.dto.event.CreateCalendarEventDto;
import com.fleencorp.feen.model.dto.event.CreateInstantCalendarEventDto;
import com.fleencorp.feen.model.request.calendar.event.*;
import com.fleencorp.feen.model.response.stream.base.CreateStreamResponse;
import com.fleencorp.feen.model.response.stream.common.AddNewStreamAttendeeResponse;
import com.fleencorp.feen.model.response.stream.common.event.DataForCreateEventResponse;
import com.fleencorp.feen.model.security.FleenUser;

public interface EventOperationsService {

  DataForCreateEventResponse getDataForCreateEvent();

  CreateStreamResponse createEvent(CreateCalendarEventDto createEventDto, FleenUser user) throws CalendarNotFoundException;

  CreateStreamResponse createInstantEvent(CreateInstantCalendarEventDto createInstantEventDto, FleenUser user) throws CalendarNotFoundException;

  void sendInvitationToPendingAttendeesBasedOnCurrentStreamStatus(String calendarExternalId, FleenStream stream, StreamVisibility previousStreamVisibility)
    throws FailedOperationException;

  void createEventInGoogleCalendar(FleenStream stream, CreateCalendarEventRequest createCalendarEventRequest);

  void addOrganizerOrAnyoneAsAttendeeOrGuestOfEvent(String calendarId, String eventId, String organizerEmail, String organizerDisplayName);

  void broadcastEventOrStreamCreated(FleenStream stream);

  void addNewAttendeeToCalendarEvent(AddNewEventAttendeeRequest addNewEventAttendeeRequest);

  void createEventInGoogleCalendarAndAnnounceInSpace(FleenStream stream, CreateCalendarEventRequest createCalendarEventRequest);

  void createInstantEventInGoogleCalendar(FleenStream stream, CreateInstantCalendarEventRequest createInstantCalendarEventRequest);

  void updateEventInGoogleCalendar(FleenStream stream, PatchCalendarEventRequest patchCalendarEventRequest);

  void deleteEventInGoogleCalendar(DeleteCalendarEventRequest deleteCalendarEventRequest);

  void cancelEventInGoogleCalendar(CancelCalendarEventRequest cancelCalendarEventRequest);

  void rescheduleEventInGoogleCalendar(RescheduleCalendarEventRequest rescheduleCalendarEventRequest);

  void updateEventVisibility(UpdateCalendarEventVisibilityRequest updateCalendarEventVisibilityRequest);

  void notAttendingEvent(NotAttendingEventRequest notAttendingEventRequest);

  void handleJoinRequestForPrivateStreamBasedOnChatSpaceMembership(FleenStream stream, StreamAttendee streamAttendee, String comment, FleenUser user);

  AddNewStreamAttendeeResponse addEventAttendee(Long eventId, AddNewStreamAttendeeDto addNewStreamAttendeeDto, FleenUser user)
    throws StreamNotFoundException, CalendarNotFoundException, StreamNotCreatedByUserException,
    StreamAlreadyHappenedException, StreamAlreadyCanceledException, FailedOperationException;

  void addAttendeeToEventExternally(String calendarExternalId, String streamExternalId, String attendeeEmailAddress, String displayOrAliasName);
}