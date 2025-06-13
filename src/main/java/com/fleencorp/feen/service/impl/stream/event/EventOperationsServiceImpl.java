package com.fleencorp.feen.service.impl.stream.event;

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
import com.fleencorp.feen.model.dto.event.CreateEventDto;
import com.fleencorp.feen.model.dto.event.CreateInstantEventDto;
import com.fleencorp.feen.model.request.calendar.event.*;
import com.fleencorp.feen.model.response.stream.base.CreateStreamResponse;
import com.fleencorp.feen.model.response.stream.common.AddNewStreamAttendeeResponse;
import com.fleencorp.feen.model.response.stream.common.event.DataForCreateEventResponse;
import com.fleencorp.feen.service.stream.event.EventOperationsService;
import com.fleencorp.feen.service.stream.event.EventService;
import com.fleencorp.feen.service.stream.event.EventUpdateService;
import com.fleencorp.feen.service.stream.event.OtherEventUpdateService;
import com.fleencorp.feen.service.stream.join.EventJoinService;
import com.fleencorp.feen.user.model.security.RegisteredUser;
import org.springframework.stereotype.Service;

@Service
public class EventOperationsServiceImpl implements EventOperationsService {

  private final EventService eventService;
  private final EventJoinService eventJoinService;
  private final EventUpdateService eventUpdateService;
  private final OtherEventUpdateService otherEventUpdateService;

  public EventOperationsServiceImpl(
      final EventService eventService,
      final EventJoinService eventJoinService,
      final EventUpdateService eventUpdateService,
      final OtherEventUpdateService otherEventUpdateService) {
    this.eventService = eventService;
    this.eventJoinService = eventJoinService;
    this.eventUpdateService = eventUpdateService;
    this.otherEventUpdateService = otherEventUpdateService;
  }

  @Override
  public DataForCreateEventResponse getDataForCreateEvent() {
    return eventService.getDataForCreateEvent();
  }

  @Override
  public CreateStreamResponse createEvent(final CreateEventDto createEventDto, final RegisteredUser user) throws CalendarNotFoundException {
    return eventService.createEvent(createEventDto, user);
  }

  @Override
  public CreateStreamResponse createInstantEvent(final CreateInstantEventDto createInstantEventDto, final RegisteredUser user) throws CalendarNotFoundException {
    return eventService.createInstantEvent(createInstantEventDto, user);
  }

  @Override
  public void sendInvitationToPendingAttendeesBasedOnCurrentStreamStatus(final String calendarExternalId, final FleenStream stream, final StreamVisibility previousStreamVisibility) throws FailedOperationException {
    eventService.sendInvitationToPendingAttendeesBasedOnCurrentStreamStatus(calendarExternalId, stream, previousStreamVisibility);
  }

  @Override
  public void createEventInGoogleCalendar(final FleenStream stream, final CreateCalendarEventRequest createCalendarEventRequest) {
    otherEventUpdateService.createEventInGoogleCalendar(stream, createCalendarEventRequest);
  }

  @Override
  public void addOrganizerOrAnyoneAsAttendeeOrGuestOfEvent(final String calendarId, final String eventId, final String organizerEmail, final String organizerDisplayName) {
    otherEventUpdateService.addOrganizerOrAnyoneAsAttendeeOrGuestOfEvent(calendarId, eventId, organizerEmail, organizerDisplayName);
  }

  @Override
  public void broadcastEventOrStreamCreated(final FleenStream stream) {
    otherEventUpdateService.broadcastEventOrStreamCreated(stream);
  }

  @Override
  public void addNewAttendeeToCalendarEvent(final AddNewEventAttendeeRequest addNewEventAttendeeRequest) {
    otherEventUpdateService.addNewAttendeeToCalendarEvent(addNewEventAttendeeRequest);
  }

  @Override
  public void createEventInGoogleCalendarAndAnnounceInSpace(final FleenStream stream, final CreateCalendarEventRequest createCalendarEventRequest) {
    eventUpdateService.createEventInGoogleCalendarAndAnnounceInSpace(stream, createCalendarEventRequest);
  }

  @Override
  public void createInstantEventInGoogleCalendar(final FleenStream stream, final CreateInstantCalendarEventRequest createInstantCalendarEventRequest) {
    eventUpdateService.createInstantEventInGoogleCalendar(stream, createInstantCalendarEventRequest);
  }

  @Override
  public void updateEventInGoogleCalendar(final FleenStream stream, final PatchCalendarEventRequest patchCalendarEventRequest) {
    eventUpdateService.updateEventInGoogleCalendar(stream, patchCalendarEventRequest);
  }

  @Override
  public void deleteEventInGoogleCalendar(final DeleteCalendarEventRequest deleteCalendarEventRequest) {
    eventUpdateService.deleteEventInGoogleCalendar(deleteCalendarEventRequest);
  }

  @Override
  public void cancelEventInGoogleCalendar(final CancelCalendarEventRequest cancelCalendarEventRequest) {
    eventUpdateService.cancelEventInGoogleCalendar(cancelCalendarEventRequest);
  }

  @Override
  public void rescheduleEventInGoogleCalendar(final RescheduleCalendarEventRequest rescheduleCalendarEventRequest) {
    eventUpdateService.rescheduleEventInGoogleCalendar(rescheduleCalendarEventRequest);
  }

  @Override
  public void updateEventVisibility(final UpdateCalendarEventVisibilityRequest updateCalendarEventVisibilityRequest) {
    eventUpdateService.updateEventVisibility(updateCalendarEventVisibilityRequest);
  }

  @Override
  public void notAttendingEvent(final NotAttendingEventRequest notAttendingEventRequest) {
    eventUpdateService.notAttendingEvent(notAttendingEventRequest);
  }

  @Override
  public void handleJoinRequestForPrivateStreamBasedOnChatSpaceMembership(final FleenStream stream, final StreamAttendee streamAttendee, final String comment, final RegisteredUser user) {
    eventJoinService.handleJoinRequestForPrivateStreamBasedOnChatSpaceMembership(stream, streamAttendee, comment, user);
  }

  @Override
  public AddNewStreamAttendeeResponse addEventAttendee(final Long eventId, final AddNewStreamAttendeeDto addNewStreamAttendeeDto, final RegisteredUser user)
    throws StreamNotFoundException, CalendarNotFoundException, StreamNotCreatedByUserException,
      StreamAlreadyHappenedException, StreamAlreadyCanceledException, FailedOperationException {
    return eventJoinService.addEventAttendee(eventId, addNewStreamAttendeeDto, user);
  }

  @Override
  public void addAttendeeToEventExternally(final String calendarExternalId, final String streamExternalId, final String attendeeEmailAddress, final String displayOrAliasName) {
    eventJoinService.addAttendeeToEventExternally(calendarExternalId, streamExternalId, attendeeEmailAddress, displayOrAliasName);
  }
}
