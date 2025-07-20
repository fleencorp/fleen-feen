package com.fleencorp.feen.calendar.mapper.external;

import com.fleencorp.feen.model.response.external.google.calendar.calendar.base.GoogleCalendarResponse;
import com.google.api.services.calendar.model.Calendar;
import com.google.api.services.calendar.model.CalendarListEntry;
import com.google.api.services.calendar.model.ConferenceProperties;
import com.google.api.services.calendar.model.EventReminder;

import java.util.List;
import java.util.Objects;

import static java.util.Objects.nonNull;

/**
* Utility class to map Google Calendar API models to custom response models.
*
* @author Yusuf Alamu Musa
* @version 1.0
*/
public final class GoogleCalendarMapper {

  private GoogleCalendarMapper() {}

  /**
  * Maps a {@link CalendarListEntry} object to a {@link GoogleCalendarResponse} object.
  *
  * @param entry The {@link CalendarListEntry} object to map.
  * @return The mapped {@link GoogleCalendarResponse} object.
  */
  public static GoogleCalendarResponse mapToCalendarResponse(final CalendarListEntry entry) {
    if (nonNull(entry)) {
      return GoogleCalendarResponse.builder()
          .kind(entry.getKind())
          .etag(entry.getEtag())
          .id(entry.getId())
          .summary(entry.getSummary())
          .description(entry.getDescription())
          .timeZone(entry.getTimeZone())
          .location(entry.getLocation())
          .summaryOverride(entry.getSummaryOverride())
          .colorId(entry.getColorId())
          .hidden(entry.getHidden())
          .selected(entry.getSelected())
          .primary(entry.getPrimary())
          .foregroundColor(entry.getForegroundColor())
          .backgroundColor(entry.getBackgroundColor())
          .accessRole(entry.getAccessRole())
          .defaultReminders(entry.getDefaultReminders().stream()
              .filter(Objects::nonNull)
              .map(GoogleCalendarMapper::mapToReminders)
              .toList())
          .deleted(entry.getDeleted())
          .conferenceProperties(mapToConferenceProperties(entry.getConferenceProperties()))
          .build();
    }
    return null;
  }

  /**
  * Maps a {@link Calendar} object to a {@link GoogleCalendarResponse} object.
  *
  * @param calendar The {@link Calendar} object to map.
  * @return The mapped {@link GoogleCalendarResponse} object.
  */
  public static GoogleCalendarResponse mapToCalendarResponse(final Calendar calendar) {
    if (nonNull(calendar)) {
      return GoogleCalendarResponse.builder()
          .kind(calendar.getKind())
          .etag(calendar.getEtag())
          .id(calendar.getId())
          .summary(calendar.getSummary())
          .description(calendar.getDescription())
          .timeZone(calendar.getTimeZone())
          .location(calendar.getLocation())
          .conferenceProperties(mapToConferenceProperties(calendar.getConferenceProperties()))
          .build();
    }
    return null;
  }

  /**
  * Maps an {@link EventReminder} object to a {@link GoogleCalendarResponse.Reminders} object.
  *
  * @param eventReminder The {@link EventReminder} object to map.
  * @return The mapped {@link GoogleCalendarResponse.Reminders} object, or {@code null} if {@code eventReminder} is {@code null}.
  */
  private static GoogleCalendarResponse.Reminders mapToReminders(final EventReminder eventReminder) {
    if (nonNull(eventReminder)) {
      return GoogleCalendarResponse.Reminders.builder()
          .minutes(eventReminder.getMinutes())
          .method(eventReminder.getMethod())
          .build();
    }
    return null;
  }

  /**
  * Maps a {@link com.google.api.services.calendar.model.ConferenceProperties} object to a
  * {@link GoogleCalendarResponse.ConferenceProperties} object.
  *
  * @param conferenceProperties The {@link com.google.api.services.calendar.model.ConferenceProperties} object to map.
  * @return The mapped {@link GoogleCalendarResponse.ConferenceProperties} object, or {@code null} if {@code conferenceProperties} is {@code null}.
  */
  private static GoogleCalendarResponse.ConferenceProperties mapToConferenceProperties(final ConferenceProperties conferenceProperties) {
    if (nonNull(conferenceProperties)) {
      return GoogleCalendarResponse.ConferenceProperties.builder()
          .conferenceSolutionTypes(conferenceProperties.getAllowedConferenceSolutionTypes())
          .build();
    }
    return null;
  }

  /**
  * Maps a list of {@link CalendarListEntry} objects to a list of {@link GoogleCalendarResponse} objects.
  *
  * @param entries The list of {@link CalendarListEntry} objects to map.
  * @return A list of mapped {@link GoogleCalendarResponse} objects, or an empty list if {@code calendarListEntries} is {@code null}.
  */
  public static List<GoogleCalendarResponse> mapToCalendarsResponse(final List<CalendarListEntry> entries) {
    if (nonNull(entries)) {
      return entries.stream()
          .filter(Objects::nonNull)
          .map(GoogleCalendarMapper::mapToCalendarResponse)
          .toList();
    }
    return List.of();
  }
}
