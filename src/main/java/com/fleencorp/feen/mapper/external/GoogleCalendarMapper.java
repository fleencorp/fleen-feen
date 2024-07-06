package com.fleencorp.feen.mapper.external;

import com.fleencorp.feen.model.response.external.google.calendar.calendar.base.GoogleCalendarResponse;
import com.google.api.services.calendar.model.Calendar;
import com.google.api.services.calendar.model.CalendarListEntry;
import com.google.api.services.calendar.model.ConferenceProperties;
import com.google.api.services.calendar.model.EventReminder;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;

/**
* Utility class to map Google Calendar API models to custom response models.
*
* @author Yusuf Alamu Musa
* @version 1.0
*/
public class GoogleCalendarMapper {

  /**
  * Maps a {@link CalendarListEntry} object to a {@link GoogleCalendarResponse} object.
  *
  * @param calendar The {@link CalendarListEntry} object to map.
  * @return The mapped {@link GoogleCalendarResponse} object.
  */
  public static GoogleCalendarResponse mapToCalendarResponse(final CalendarListEntry calendar) {
    if (nonNull(calendar)) {
      return GoogleCalendarResponse.builder()
          .kind(calendar.getKind())
          .etag(calendar.getEtag())
          .id(calendar.getId())
          .summary(calendar.getSummary())
          .description(calendar.getDescription())
          .timeZone(calendar.getTimeZone())
          .location(calendar.getLocation())
          .summaryOverride(calendar.getSummaryOverride())
          .colorId(calendar.getColorId())
          .hidden(calendar.getHidden())
          .selected(calendar.getSelected())
          .primary(calendar.getPrimary())
          .foregroundColor(calendar.getForegroundColor())
          .backgroundColor(calendar.getBackgroundColor())
          .accessRole(calendar.getAccessRole())
          .defaultReminders(calendar.getDefaultReminders().stream()
              .map(GoogleCalendarMapper::mapToReminders)
              .collect(Collectors.toList()))
          .deleted(calendar.getDeleted())
          .conferenceProperties(GoogleCalendarMapper.mapToConferenceProperties(calendar.getConferenceProperties()))
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
          .conferenceProperties(GoogleCalendarMapper.mapToConferenceProperties(calendar.getConferenceProperties()))
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
  * @param calendarListEntries The list of {@link CalendarListEntry} objects to map.
  * @return A list of mapped {@link GoogleCalendarResponse} objects, or an empty list if {@code calendarListEntries} is {@code null}.
  */
  public static List<GoogleCalendarResponse> mapToCalendarsResponse(final List<CalendarListEntry> calendarListEntries) {
    if (nonNull(calendarListEntries)) {
      return calendarListEntries.stream()
          .map(GoogleCalendarMapper::mapToCalendarResponse)
          .filter(Objects::nonNull)
          .collect(Collectors.toList());
    }
    return Collections.emptyList();
  }
}
