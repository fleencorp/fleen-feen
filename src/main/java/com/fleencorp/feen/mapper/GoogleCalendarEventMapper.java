package com.fleencorp.feen.mapper;

import com.fleencorp.feen.model.response.calendar.event.ListCalendarEventResponse;
import com.fleencorp.feen.model.response.calendar.event.base.GoogleCalendarEventResponse;
import com.google.api.services.calendar.model.*;

import java.util.stream.Collectors;

import static com.fleencorp.feen.service.external.google.GoogleCalendarEventService.toLocalDateTime;

/**
 * A utility class that maps Google Calendar API models to custom models.
 *
 * <p>This class provides methods to convert between the models used by the Google Calendar API
 * and custom models that have a similar structure. It facilitates the integration of Google Calendar
 * functionality into the custom application.</p>
 *
 * @author Yusuf Alamu Musa
 * @version 1.0
 */
public class GoogleCalendarEventMapper {

  private GoogleCalendarEventMapper() {}

  /**
   * Maps the {@link Events} object from the Google Calendar API to a {@link ListCalendarEventResponse} object.
   *
   * <p>This method converts the various properties of the {@link Events} object into corresponding properties
   * of the {@link ListCalendarEventResponse} object, including kind, etag, summary, description, updated time,
   * time zone, access role, and items. Each event item is mapped using the {@code mapToEvent} method.</p>
   *
   * @param calendarEvents the {@link Events} object from the Google Calendar API
   * @return a {@link ListCalendarEventResponse} object containing the mapped data
   */
  public static ListCalendarEventResponse mapToCalendarEventResponse(Events calendarEvents) {
    // Use the builder to construct the ListCalendarEventResponse
    return ListCalendarEventResponse.builder()
            .kind(calendarEvents.getKind())
            .etag(calendarEvents.getEtag())
            .summary(calendarEvents.getSummary())
            .description(calendarEvents.getDescription())
            .updated(calendarEvents.getUpdated())
            .updatedOn(toLocalDateTime(calendarEvents.getUpdated()))
            .timeZone(calendarEvents.getTimeZone())
            .accessRole(calendarEvents.getAccessRole())
            .items(calendarEvents.getItems()
                    .stream()
                    .map(GoogleCalendarEventMapper::mapToEvent)
                    .collect(Collectors.toList()))
            .nextPageToken(calendarEvents.getNextPageToken())
            .nextSyncToken(calendarEvents.getNextSyncToken())
            .build();
  }

  /**
   * Maps an {@link Event} object from the Google Calendar API to a {@link GoogleCalendarEventResponse} object.
   *
   * <p>This method converts the various properties of the {@link Event} object into corresponding properties
   * of the {@link GoogleCalendarEventResponse} object, including kind, etag, id, status, HTML link,
   * creation and update timestamps, summary, description, location, creator, organizer, start and end times,
   * iCal UID, sequence, reminders, and the number of attendees.</p>
   *
   * @param calendarEvent the {@link Event} object from the Google Calendar API
   * @return a {@link GoogleCalendarEventResponse} object containing the mapped data
   */
  private static GoogleCalendarEventResponse mapToEvent(Event calendarEvent) {
    return GoogleCalendarEventResponse.builder()
            .kind(calendarEvent.getKind())
            .etag(calendarEvent.getEtag())
            .id(calendarEvent.getId())
            .status(calendarEvent.getStatus())
            .htmlLink(calendarEvent.getHtmlLink())
            .created(calendarEvent.getCreated())
            .createdOn(toLocalDateTime(calendarEvent.getCreated()))
            .updated(calendarEvent.getUpdated())
            .updatedOn(toLocalDateTime(calendarEvent.getUpdated()))
            .summary(calendarEvent.getSummary())
            .description(calendarEvent.getDescription())
            .location(calendarEvent.getLocation())
            .creator(mapToCreator(calendarEvent.getCreator()))
            .organizer(mapToOrganizer(calendarEvent.getOrganizer()))
            .start(mapToStart(calendarEvent.getStart()))
            .end(mapToEnd(calendarEvent.getEnd()))
            .iCalUID(calendarEvent.getICalUID())
            .sequence(calendarEvent.getSequence())
            .reminders(mapToReminders(calendarEvent.getReminders()))
            .totalAttendeesOrGuests(calendarEvent.getAttendees() != null ? calendarEvent.getAttendees().size() : 0)
            .build();
  }

  /**
   * Maps an expanded {@link Event} object from the Google Calendar API to a {@link GoogleCalendarEventResponse} object.
   *
   * <p>This method converts the various properties of the {@link Event} object into corresponding properties
   * of the {@link GoogleCalendarEventResponse} object, including kind, etag, id, status, HTML link,
   * creation and update timestamps, summary, description, location, creator, organizer, start and end times,
   * iCal UID, sequence, reminders, attendees, conference data, and extended properties.</p>
   *
   * @param calendarEvent the {@link Event} object from the Google Calendar API
   * @return a {@link GoogleCalendarEventResponse} object containing the mapped data
   */
  public static GoogleCalendarEventResponse mapToEventExpanded(Event calendarEvent) {
    return GoogleCalendarEventResponse.builder()
            .kind(calendarEvent.getKind())
            .etag(calendarEvent.getEtag())
            .id(calendarEvent.getId())
            .status(calendarEvent.getStatus())
            .htmlLink(calendarEvent.getHtmlLink())
            .created(calendarEvent.getCreated())
            .updated(calendarEvent.getUpdated())
            .summary(calendarEvent.getSummary())
            .description(calendarEvent.getDescription())
            .location(calendarEvent.getLocation())
            .creator(mapToCreator(calendarEvent.getCreator()))
            .organizer(mapToOrganizer(calendarEvent.getOrganizer()))
            .start(mapToStart(calendarEvent.getStart()))
            .end(mapToEnd(calendarEvent.getEnd()))
            .iCalUID(calendarEvent.getICalUID())
            .sequence(calendarEvent.getSequence())
            .reminders(mapToReminders(calendarEvent.getReminders()))
            .attendees(calendarEvent.getAttendees().stream().map(GoogleCalendarEventMapper::mapToAttendee).collect(Collectors.toList()))
            .conferenceData(mapToConferenceData(calendarEvent.getConferenceData()))
            .extendedProperties(mapToExtendedProperties(calendarEvent.getExtendedProperties()))
            .totalAttendeesOrGuests(calendarEvent.getAttendees() != null ? calendarEvent.getAttendees().size() : 0)
            .build();
  }

  /**
   * Maps an {@link Event.Creator} object from the Google Calendar API to a {@link GoogleCalendarEventResponse.Creator} object.
   *
   * <p>This method converts the properties of the {@link Event.Creator} object into corresponding properties
   * of the {@link GoogleCalendarEventResponse.Creator} object, including id, email, displayName, and self status.</p>
   *
   * @param eventCreator the {@link Event.Creator} object from the Google Calendar API
   * @return a {@link GoogleCalendarEventResponse.Creator} object containing the mapped data
   */
  private static GoogleCalendarEventResponse.Creator mapToCreator(Event.Creator eventCreator) {
    return GoogleCalendarEventResponse.Creator.builder()
            .id(eventCreator.getId())
            .email(eventCreator.getEmail())
            .displayName(eventCreator.getDisplayName())
            .self(eventCreator.getSelf())
            .build();
  }

  /**
   * Maps an {@link Event.Organizer} object from the Google Calendar API to a {@link GoogleCalendarEventResponse.Organizer} object.
   *
   * <p>This method converts the properties of the {@link Event.Organizer} object into corresponding properties
   * of the {@link GoogleCalendarEventResponse.Organizer} object, including id, email, displayName, and self status.</p>
   *
   * @param eventOrganizer the {@link Event.Organizer} object from the Google Calendar API
   * @return a {@link GoogleCalendarEventResponse.Organizer} object containing the mapped data
   */
  private static GoogleCalendarEventResponse.Organizer mapToOrganizer(Event.Organizer eventOrganizer) {
    return GoogleCalendarEventResponse.Organizer.builder()
            .id(eventOrganizer.getId())
            .email(eventOrganizer.getEmail())
            .displayName(eventOrganizer.getDisplayName())
            .self(eventOrganizer.getSelf())
            .build();
  }

  /**
   * Maps an {@link EventDateTime} object from the Google Calendar API to a {@link GoogleCalendarEventResponse.Start} object.
   *
   * <p>This method converts the properties of the {@link EventDateTime} object into corresponding properties
   * of the {@link GoogleCalendarEventResponse.Start} object, including dateTime and timeZone.</p>
   *
   * @param eventEndTime the {@link EventDateTime} object representing the start time of the event
   * @return a {@link GoogleCalendarEventResponse.Start} object containing the mapped data
   */
  private static GoogleCalendarEventResponse.Start mapToStart(EventDateTime eventEndTime) {
    return GoogleCalendarEventResponse.Start.builder()
            .dateTime(eventEndTime.getDateTime())
            .timeZone(eventEndTime.getTimeZone())
            .build();
  }

  /**
   * Maps an {@link EventDateTime} object from the Google Calendar API to a {@link GoogleCalendarEventResponse.End} object.
   *
   * <p>This method converts the properties of the {@link EventDateTime} object into corresponding properties
   * of the {@link GoogleCalendarEventResponse.End} object, including dateTime, timeZone, and actualDateTime.</p>
   *
   * @param eventEndDateTime the {@link EventDateTime} object representing the end time of the event
   * @return a {@link GoogleCalendarEventResponse.End} object containing the mapped data
   */
  private static GoogleCalendarEventResponse.End mapToEnd(EventDateTime eventEndDateTime) {
    return GoogleCalendarEventResponse.End.builder()
            .dateTime(eventEndDateTime.getDateTime())
            .timeZone(eventEndDateTime.getTimeZone())
            .actualDateTime(toLocalDateTime(eventEndDateTime.getDateTime()))
            .build();
  }

  /**
   * Maps an {@link Event.Reminders} object from the Google Calendar API to a {@link GoogleCalendarEventResponse.Reminders} object.
   *
   * <p>This method converts the properties of the {@link Event.Reminders} object into corresponding properties
   * of the {@link GoogleCalendarEventResponse.Reminders} object, including useDefault and overrides.</p>
   *
   * @param eventReminders the {@link Event.Reminders} object representing reminders for the event
   * @return a {@link GoogleCalendarEventResponse.Reminders} object containing the mapped data
   */
  private static GoogleCalendarEventResponse.Reminders mapToReminders(Event.Reminders eventReminders) {
    return GoogleCalendarEventResponse.Reminders.builder()
            .useDefault(eventReminders.getUseDefault())
            .overrides(eventReminders.getOverrides().stream()
              .map(GoogleCalendarEventMapper::mapToOverride)
              .collect(Collectors.toList()))
            .build();
  }

  /**
   * Maps an {@link EventReminder} object from the Google Calendar API to a {@link GoogleCalendarEventResponse.Reminders.Override} object.
   *
   * <p>This method converts the properties of the {@link EventReminder} object into corresponding properties
   * of the {@link GoogleCalendarEventResponse.Reminders.Override} object, including method and minutes.</p>
   *
   * @param eventReminderOverride the {@link EventReminder} object representing a reminder override for the event
   * @return a {@link GoogleCalendarEventResponse.Reminders.Override} object containing the mapped data
   */
  private static GoogleCalendarEventResponse.Reminders.Override mapToOverride(EventReminder eventReminderOverride) {
    return GoogleCalendarEventResponse.Reminders.Override.builder()
            .method(eventReminderOverride.getMethod())
            .minutes(eventReminderOverride.getMinutes())
            .build();
  }

  /**
   * Maps an {@link EventAttendee} object from the Google Calendar API to a {@link GoogleCalendarEventResponse.Attendee} object.
   *
   * <p>This method converts the properties of the {@link EventAttendee} object into corresponding properties
   * of the {@link GoogleCalendarEventResponse.Attendee} object, including id, email, displayName, organizer,
   * self, resource, optional, responseStatus, comment, and additionalGuests.</p>
   *
   * @param eventAttendee the {@link EventAttendee} object representing an attendee of the event
   * @return a {@link GoogleCalendarEventResponse.Attendee} object containing the mapped data
   */
  private static GoogleCalendarEventResponse.Attendee mapToAttendee(EventAttendee eventAttendee) {
    return GoogleCalendarEventResponse.Attendee.builder()
            .id(eventAttendee.getId())
            .email(eventAttendee.getEmail())
            .displayName(eventAttendee.getDisplayName())
            .organizer(eventAttendee.getOrganizer())
            .self(eventAttendee.getSelf())
            .resource(eventAttendee.getResource())
            .optional(eventAttendee.getOptional())
            .responseStatus(eventAttendee.getResponseStatus())
            .comment(eventAttendee.getComment())
            .additionalGuests(eventAttendee.getAdditionalGuests())
            .build();
  }

  /**
   * Maps a {@link ConferenceData} object from the Google Calendar API to a {@link GoogleCalendarEventResponse.ConferenceData} object.
   *
   * <p>This method converts the properties of the {@link ConferenceData} object into corresponding properties
   * of the {@link GoogleCalendarEventResponse.ConferenceData} object, including conferenceSolution and createRequest.</p>
   *
   * @param conferenceData the {@link ConferenceData} object representing conference data associated with the event
   * @return a {@link GoogleCalendarEventResponse.ConferenceData} object containing the mapped data
   */
  private static GoogleCalendarEventResponse.ConferenceData mapToConferenceData(ConferenceData conferenceData) {
    return GoogleCalendarEventResponse.ConferenceData.builder()
            .conferenceSolution(mapToConferenceSolution(conferenceData.getConferenceSolution()))
            .createRequest(mapToCreateConferenceRequest(conferenceData.getCreateRequest()))
            .build();
  }

  /**
   * Maps a {@link ConferenceSolution} object from the Google Calendar API to a {@link GoogleCalendarEventResponse.ConferenceData.ConferenceSolution} object.
   *
   * <p>This method converts the name property of the {@link ConferenceSolution} object into the name property
   * of the {@link GoogleCalendarEventResponse.ConferenceData.ConferenceSolution} object.</p>
   *
   * @param conferenceSolution the {@link ConferenceSolution} object representing conference solution data associated with the event
   * @return a {@link GoogleCalendarEventResponse.ConferenceData.ConferenceSolution} object containing the mapped data
   */
  private static GoogleCalendarEventResponse.ConferenceData.ConferenceSolution mapToConferenceSolution(ConferenceSolution conferenceSolution) {
    return GoogleCalendarEventResponse.ConferenceData.ConferenceSolution.builder()
            .name(conferenceSolution.getName())
            .build();
  }

  /**
   * Maps a {@link CreateConferenceRequest} object from the Google Calendar API to a {@link GoogleCalendarEventResponse.ConferenceData.CreateConferenceRequest} object.
   *
   * <p>This method converts the requestId and conferenceSolutionKey properties of the {@link CreateConferenceRequest}
   * object into corresponding properties of the {@link GoogleCalendarEventResponse.ConferenceData.CreateConferenceRequest} object.</p>
   *
   * @param createConferenceRequest the {@link CreateConferenceRequest} object representing conference creation request data associated with the event
   * @return a {@link GoogleCalendarEventResponse.ConferenceData.CreateConferenceRequest} object containing the mapped data
   */
  private static GoogleCalendarEventResponse.ConferenceData.CreateConferenceRequest mapToCreateConferenceRequest(CreateConferenceRequest createConferenceRequest) {
    return GoogleCalendarEventResponse.ConferenceData.CreateConferenceRequest.builder()
            .requestId(createConferenceRequest.getRequestId())
            .conferenceSolutionKey(mapToConferenceSolutionKey(createConferenceRequest.getConferenceSolutionKey()))
            .build();
  }

  /**
   * Maps a {@link ConferenceSolutionKey} object from the Google Calendar API to a {@link GoogleCalendarEventResponse.ConferenceData.ConferenceSolutionKey} object.
   *
   * <p>This method converts the type property of the {@link ConferenceSolutionKey} object into the type property
   * of the {@link GoogleCalendarEventResponse.ConferenceData.ConferenceSolutionKey} object.</p>
   *
   * @param conferenceSolutionKey the {@link ConferenceSolutionKey} object representing conference solution key data associated with the event
   * @return a {@link GoogleCalendarEventResponse.ConferenceData.ConferenceSolutionKey} object containing the mapped data
   */
  private static GoogleCalendarEventResponse.ConferenceData.ConferenceSolutionKey mapToConferenceSolutionKey(ConferenceSolutionKey conferenceSolutionKey) {
    return GoogleCalendarEventResponse.ConferenceData.ConferenceSolutionKey.builder()
            .type(conferenceSolutionKey.getType())
            .build();
  }

  /**
   * Maps an {@link Event.ExtendedProperties} object from the Google Calendar API to a {@link GoogleCalendarEventResponse.ExtendedProperties} object.
   *
   * <p>This method converts the shared property of the {@link Event.ExtendedProperties} object into the shared property
   * of the {@link GoogleCalendarEventResponse.ExtendedProperties} object.</p>
   *
   * @param eventExtendedProperties the {@link Event.ExtendedProperties} object representing extended properties associated with the event
   * @return a {@link GoogleCalendarEventResponse.ExtendedProperties} object containing the mapped data
   */
  private static GoogleCalendarEventResponse.ExtendedProperties mapToExtendedProperties(Event.ExtendedProperties eventExtendedProperties) {
    return GoogleCalendarEventResponse.ExtendedProperties.builder()
            .shared(eventExtendedProperties.getShared())
            .build();
  }
}

