package com.fleencorp.feen.mapper.external;

import com.fleencorp.feen.model.response.external.google.calendar.event.GoogleListCalendarEventResponse;
import com.fleencorp.feen.model.response.external.google.calendar.event.base.GoogleCalendarEventResponse;
import com.google.api.services.calendar.model.*;

import java.util.List;
import java.util.Objects;

import static com.fleencorp.feen.service.impl.external.google.calendar.GoogleCalendarEventService.toLocalDateTime;
import static java.util.Objects.nonNull;

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
  * Maps the {@link Events} object from the Google Calendar API to a {@link GoogleListCalendarEventResponse} object.
  *
  * <p>This method converts the various properties of the {@link Events} object into corresponding properties
  * of the {@link GoogleListCalendarEventResponse} object, including kind, etag, summary, description, updated time,
  * time zone, access role, and items. Each event item is mapped using the {@code mapToEvent} method.</p>
  *
  * @param calendarEvents the {@link Events} object from the Google Calendar API
  * @return a {@link GoogleListCalendarEventResponse} object containing the mapped data
  */
  public static GoogleListCalendarEventResponse mapToCalendarEventResponse(final Events calendarEvents) {
    // Use the builder to construct the ListCalendarEventResponse
    return GoogleListCalendarEventResponse.builder()
            .kind(calendarEvents.getKind())
            .etag(calendarEvents.getEtag())
            .summary(calendarEvents.getSummary())
            .description(calendarEvents.getDescription())
            .updated(calendarEvents.getUpdated())
            .updatedOn(toLocalDateTime(calendarEvents.getUpdated()))
            .timeZone(calendarEvents.getTimeZone())
            .accessRole(calendarEvents.getAccessRole())
            .items(GoogleCalendarEventMapper.mapToEvents(calendarEvents.getItems()))
            .nextPageToken(calendarEvents.getNextPageToken())
            .nextSyncToken(calendarEvents.getNextSyncToken())
            .build();
  }

  /**
  * Maps a list of Google Calendar Events to a list of custom Event responses.
  *
  * <p>This method converts a list of {@link Event} objects from the Google Calendar API
  * to a list of {@link GoogleCalendarEventResponse} objects, using the
  * {@link GoogleCalendarEventMapper#mapToEvent} method for individual mappings.</p>
  *
  * @param events The list of {@link Event} objects to be mapped.
  * @return A list of {@link GoogleCalendarEventResponse} objects, or an empty list if the input is null.
  */
  private static List<GoogleCalendarEventResponse> mapToEvents(final List<Event> events) {
    if (nonNull(events)) {
      return events
          .stream()
          .map(GoogleCalendarEventMapper::mapToEvent)
          .filter(Objects::nonNull)
          .toList();
    }
    return List.of();
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
  private static GoogleCalendarEventResponse mapToEvent(final Event calendarEvent) {
    if (nonNull(calendarEvent)) {
      return GoogleCalendarEventResponse.builder()
          .kind(calendarEvent.getKind())
          .etag(calendarEvent.getEtag())
          .id(calendarEvent.getId())
          .status(calendarEvent.getStatus())
          .htmlLink(calendarEvent.getHtmlLink())
          .hangoutLink(calendarEvent.getHangoutLink())
          .created(calendarEvent.getCreated())
          .createdOn(toLocalDateTime(calendarEvent.getCreated()))
          .updated(calendarEvent.getUpdated())
          .updatedOn(toLocalDateTime(calendarEvent.getUpdated()))
          .summary(calendarEvent.getSummary())
          .description(calendarEvent.getDescription())
          .location(calendarEvent.getLocation())
          .creator(GoogleCalendarEventMapper.mapToCreator(calendarEvent.getCreator()))
          .organizer(GoogleCalendarEventMapper.mapToOrganizer(calendarEvent.getOrganizer()))
          .start(GoogleCalendarEventMapper.mapToStart(calendarEvent.getStart()))
          .end(GoogleCalendarEventMapper.mapToEnd(calendarEvent.getEnd()))
          .iCalUID(calendarEvent.getICalUID())
          .sequence(calendarEvent.getSequence())
          .reminders(GoogleCalendarEventMapper.mapToReminders(calendarEvent.getReminders()))
          .totalAttendeesOrGuests(nonNull(calendarEvent.getAttendees()) ? calendarEvent.getAttendees().size() : 0)
          .build();
    }
    return null;
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
  public static GoogleCalendarEventResponse mapToEventExpanded(final Event calendarEvent) {
    if (nonNull(calendarEvent)) {
      return GoogleCalendarEventResponse.builder()
          .kind(calendarEvent.getKind())
          .etag(calendarEvent.getEtag())
          .id(calendarEvent.getId())
          .status(calendarEvent.getStatus())
          .htmlLink(calendarEvent.getHtmlLink())
          .hangoutLink(calendarEvent.getHangoutLink())
          .created(calendarEvent.getCreated())
          .updated(calendarEvent.getUpdated())
          .summary(calendarEvent.getSummary())
          .description(calendarEvent.getDescription())
          .location(calendarEvent.getLocation())
          .creator(GoogleCalendarEventMapper.mapToCreator(calendarEvent.getCreator()))
          .organizer(GoogleCalendarEventMapper.mapToOrganizer(calendarEvent.getOrganizer()))
          .start(GoogleCalendarEventMapper.mapToStart(calendarEvent.getStart()))
          .end(GoogleCalendarEventMapper.mapToEnd(calendarEvent.getEnd()))
          .iCalUID(calendarEvent.getICalUID())
          .sequence(calendarEvent.getSequence())
          .reminders(GoogleCalendarEventMapper.mapToReminders(calendarEvent.getReminders()))
          .attendees(GoogleCalendarEventMapper.mapToAttendees(calendarEvent.getAttendees()))
          .conferenceData(GoogleCalendarEventMapper.mapToConferenceData(calendarEvent.getConferenceData()))
          .extendedProperties(GoogleCalendarEventMapper.mapToExtendedProperties(calendarEvent.getExtendedProperties()))
          .totalAttendeesOrGuests((null != calendarEvent.getAttendees()) ? calendarEvent.getAttendees().size() : 0)
          .build();
    }
    return null;
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
  private static GoogleCalendarEventResponse.Creator mapToCreator(final Event.Creator eventCreator) {
    if (nonNull(eventCreator)) {
      return GoogleCalendarEventResponse.Creator.builder()
          .id(eventCreator.getId())
          .email(eventCreator.getEmail())
          .displayName(eventCreator.getDisplayName())
          .self(eventCreator.getSelf())
          .build();
    }
    return null;
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
  private static GoogleCalendarEventResponse.Organizer mapToOrganizer(final Event.Organizer eventOrganizer) {
    if (nonNull(eventOrganizer)) {
      return GoogleCalendarEventResponse.Organizer.builder()
          .id(eventOrganizer.getId())
          .email(eventOrganizer.getEmail())
          .displayName(eventOrganizer.getDisplayName())
          .self(eventOrganizer.getSelf())
          .build();
    }
    return null;
  }

  /**
  * Maps an {@link EventDateTime} object from the Google Calendar API to a {@link GoogleCalendarEventResponse.Start} object.
  *
  * <p>This method converts the properties of the {@link EventDateTime} object into corresponding properties
  * of the {@link GoogleCalendarEventResponse.Start} object, including dateTime and timeZone.</p>
  *
  * @param eventStartTime the {@link EventDateTime} object representing the start time of the event
  * @return a {@link GoogleCalendarEventResponse.Start} object containing the mapped data
  */
  private static GoogleCalendarEventResponse.Start mapToStart(final EventDateTime eventStartTime) {
    if (nonNull(eventStartTime)) {
      return GoogleCalendarEventResponse.Start.builder()
          .dateTime(eventStartTime.getDateTime())
          .timeZone(eventStartTime.getTimeZone())
          .actualDateTime(toLocalDateTime(eventStartTime.getDateTime()))
          .build();
    }
    return null;
  }

  /**
  * Maps an {@link EventDateTime} object from the Google Calendar API to a {@link GoogleCalendarEventResponse.End} object.
  *
  * <p>This method converts the properties of the {@link EventDateTime} object into corresponding properties
  * of the {@link GoogleCalendarEventResponse.End} object, including dateTime, timeZone, and actualDateTime.</p>
  *
  * @param eventEndTime the {@link EventDateTime} object representing the end time of the event
  * @return a {@link GoogleCalendarEventResponse.End} object containing the mapped data
  */
  private static GoogleCalendarEventResponse.End mapToEnd(final EventDateTime eventEndTime) {
    if (nonNull(eventEndTime)) {
      return GoogleCalendarEventResponse.End.builder()
          .dateTime(eventEndTime.getDateTime())
          .timeZone(eventEndTime.getTimeZone())
          .actualDateTime(toLocalDateTime(eventEndTime.getDateTime()))
          .build();
    }
    return null;
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
  private static GoogleCalendarEventResponse.Reminders mapToReminders(final Event.Reminders eventReminders) {
    if (nonNull(eventReminders)) {
      return GoogleCalendarEventResponse.Reminders.builder()
          .useDefault(eventReminders.getUseDefault())
          .overrides(GoogleCalendarEventMapper.mapToOverrides(eventReminders.getOverrides()))
          .build();
    }
    return null;
  }

  /**
  * Maps a list of Google Calendar Event Reminders to a list of custom Reminder Override responses.
  *
  * <p>This method converts a list of {@link EventReminder} objects from the Google Calendar API
  * to a list of {@link GoogleCalendarEventResponse.Reminders.Override} objects, using the
  * {@link GoogleCalendarEventMapper#mapToOverride} method for individual mappings.</p>
  *
  * @param eventReminders The list of {@link EventReminder} objects to be mapped.
  * @return A list of {@link GoogleCalendarEventResponse.Reminders.Override} objects,
  *         or an empty list if the input is null.
  */
  private static List<GoogleCalendarEventResponse.Reminders.Override> mapToOverrides(final List<EventReminder> eventReminders) {
    if (nonNull(eventReminders)) {
      return eventReminders.stream()
          .map(GoogleCalendarEventMapper::mapToOverride)
          .filter(Objects::nonNull)
          .toList();
    }
    return List.of();
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
  private static GoogleCalendarEventResponse.Reminders.Override mapToOverride(final EventReminder eventReminderOverride) {
    if (nonNull(eventReminderOverride)) {
      return GoogleCalendarEventResponse.Reminders.Override.builder()
          .method(eventReminderOverride.getMethod())
          .minutes(eventReminderOverride.getMinutes())
          .build();
    }
    return null;
  }

  /**
  * Maps a list of Google Calendar Event Attendees to a list of custom Attendee responses.
  *
  * <p>This method converts a list of {@link EventAttendee} objects from the Google Calendar API
  * to a list of {@link GoogleCalendarEventResponse.Attendee} objects, using the
  * {@link GoogleCalendarEventMapper#mapToAttendee} method for individual mappings.</p>
  *
  * @param eventAttendees The list of {@link EventAttendee} objects to be mapped.
  * @return A list of {@link GoogleCalendarEventResponse.Attendee} objects, or an empty list if the input is null.
  */
  private static List<GoogleCalendarEventResponse.Attendee> mapToAttendees(final List<EventAttendee> eventAttendees) {
    if (nonNull(eventAttendees)) {
      return eventAttendees.stream()
          .map(GoogleCalendarEventMapper::mapToAttendee)
          .filter(Objects::nonNull)
          .toList();
    }
    return List.of();
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
  private static GoogleCalendarEventResponse.Attendee mapToAttendee(final EventAttendee eventAttendee) {
    if (nonNull(eventAttendee)) {
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
    return null;
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
  private static GoogleCalendarEventResponse.ConferenceData mapToConferenceData(final ConferenceData conferenceData) {
    if (nonNull(conferenceData)) {
      return GoogleCalendarEventResponse.ConferenceData.builder()
          .conferenceSolution(GoogleCalendarEventMapper.mapToConferenceSolution(conferenceData.getConferenceSolution()))
          .createRequest(GoogleCalendarEventMapper.mapToCreateConferenceRequest(conferenceData.getCreateRequest()))
          .build();
    }
    return null;
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
  private static GoogleCalendarEventResponse.ConferenceData.ConferenceSolution mapToConferenceSolution(final ConferenceSolution conferenceSolution) {
    if (nonNull(conferenceSolution)) {
      return GoogleCalendarEventResponse.ConferenceData.ConferenceSolution.builder()
          .name(conferenceSolution.getName())
          .build();
    }
    return null;
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
  private static GoogleCalendarEventResponse.ConferenceData.CreateConferenceRequest mapToCreateConferenceRequest(final CreateConferenceRequest createConferenceRequest) {
    if (nonNull(createConferenceRequest)) {
      return GoogleCalendarEventResponse.ConferenceData.CreateConferenceRequest.builder()
          .requestId(createConferenceRequest.getRequestId())
          .conferenceSolutionKey(GoogleCalendarEventMapper.mapToConferenceSolutionKey(createConferenceRequest.getConferenceSolutionKey()))
          .build();
    }
    return null;
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
  private static GoogleCalendarEventResponse.ConferenceData.ConferenceSolutionKey mapToConferenceSolutionKey(final ConferenceSolutionKey conferenceSolutionKey) {
    if (nonNull(conferenceSolutionKey)) {
      return GoogleCalendarEventResponse.ConferenceData.ConferenceSolutionKey.builder()
          .type(conferenceSolutionKey.getType())
          .build();
    }
    return null;
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
  private static GoogleCalendarEventResponse.ExtendedProperties mapToExtendedProperties(final Event.ExtendedProperties eventExtendedProperties) {
    if (nonNull(eventExtendedProperties)) {
      return GoogleCalendarEventResponse.ExtendedProperties.builder()
          .shared(eventExtendedProperties.getShared())
          .build();
    }
    return null;
  }
}

