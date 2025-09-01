package com.fleencorp.feen.stream.service.event;

import com.fleencorp.feen.calendar.exception.core.CalendarNotFoundException;
import com.fleencorp.feen.calendar.model.request.event.create.AddNewEventAttendeeRequest;
import com.fleencorp.feen.calendar.model.request.event.create.CreateCalendarEventRequest;
import com.fleencorp.feen.calendar.model.request.event.create.CreateInstantCalendarEventRequest;
import com.fleencorp.feen.calendar.model.request.event.update.*;
import com.fleencorp.feen.common.exception.FailedOperationException;
import com.fleencorp.feen.stream.constant.core.StreamVisibility;
import com.fleencorp.feen.stream.exception.core.StreamAlreadyCanceledException;
import com.fleencorp.feen.stream.exception.core.StreamAlreadyHappenedException;
import com.fleencorp.feen.stream.exception.core.StreamNotCreatedByUserException;
import com.fleencorp.feen.stream.exception.core.StreamNotFoundException;
import com.fleencorp.feen.stream.model.domain.FleenStream;
import com.fleencorp.feen.stream.model.domain.StreamAttendee;
import com.fleencorp.feen.stream.model.dto.event.AddNewStreamAttendeeDto;
import com.fleencorp.feen.stream.model.dto.event.CreateEventDto;
import com.fleencorp.feen.stream.model.dto.event.CreateInstantEventDto;
import com.fleencorp.feen.stream.model.response.base.CreateStreamResponse;
import com.fleencorp.feen.stream.model.response.common.AddNewStreamAttendeeResponse;
import com.fleencorp.feen.stream.model.response.common.event.DataForCreateEventResponse;
import com.fleencorp.feen.shared.security.RegisteredUser;

public interface EventOperationsService {

  DataForCreateEventResponse getDataForCreateEvent();

  CreateStreamResponse createEvent(CreateEventDto createEventDto, RegisteredUser user) throws CalendarNotFoundException;

  CreateStreamResponse createInstantEvent(CreateInstantEventDto createInstantEventDto, RegisteredUser user) throws CalendarNotFoundException;

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

  void handleJoinRequestForPrivateStreamBasedOnChatSpaceMembership(FleenStream stream, StreamAttendee streamAttendee, String comment, RegisteredUser user);

  AddNewStreamAttendeeResponse addEventAttendee(Long eventId, AddNewStreamAttendeeDto addNewStreamAttendeeDto, RegisteredUser user)
    throws StreamNotFoundException, CalendarNotFoundException, StreamNotCreatedByUserException,
    StreamAlreadyHappenedException, StreamAlreadyCanceledException, FailedOperationException;

  void addAttendeeToEventExternally(String calendarExternalId, String streamExternalId, String attendeeEmailAddress, String displayOrAliasName);
}