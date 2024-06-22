package com.fleencorp.feen.service.external.google;

import com.fleencorp.feen.constant.external.google.calendar.ConferenceSolutionType;
import com.fleencorp.feen.mapper.GoogleCalendarMapper;
import com.fleencorp.feen.model.request.calendar.calendar.*;
import com.fleencorp.feen.model.response.google.calendar.calendar.*;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.AclRule;
import com.google.api.services.calendar.model.CalendarList;
import com.google.api.services.calendar.model.CalendarListEntry;
import com.google.api.services.calendar.model.ConferenceProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static java.util.Objects.nonNull;

/**
 * A service class for interacting with Google Calendar to create, update, retrieve, and delete calendar.
 *
 *
 * @author Yusuf Alamu Musa
 * @version 1.0
 *
 * @see <a href="https://developers.google.com/calendar/api/guides/overview">
 *   Google Calendar API overview</a>
 */
@Service
@Slf4j
public class GoogleCalendarService {

  private final Calendar service;

  /**
   * Constructs a new CalendarService with the specified Google Calendar instance.
   *
   * @param calendar the Google Calendar instance used by this service
   */
  public GoogleCalendarService(Calendar calendar) {
    this.service = calendar;
  }

  /**
   * Creates a new calendar on Google Calendar based on the provided request parameters.
   *
   * <p>This method creates a new calendar with the specified title, description, and timezone.
   * It also sets the allowed conference solution types for the new calendar. If an error occurs during
   * the creation process, it is logged.</p>
   *
   * @param createCalendarRequest the request object containing the title, description, and timezone for the new calendar
   * @return {@link CreateCalendarResponse} the response containing the created calendar
   */
  public CreateCalendarResponse createCalendar(CreateCalendarRequest createCalendarRequest) {
    try {
      // Create a new calendar object and set its properties
      com.google.api.services.calendar.model.Calendar newCalendar = new com.google.api.services.calendar.model.Calendar();
      newCalendar.setSummary(createCalendarRequest.getTitle());
      newCalendar.setDescription(createCalendarRequest.getDescription());
      newCalendar.setTimeZone(createCalendarRequest.getTimezone());

      // Set allowed conference solution types for the new calendar
      List<String> conferenceSolutionTypes = Arrays.stream(ConferenceSolutionType.values())
        .map(ConferenceSolutionType::getValue)
        .toList();
      ConferenceProperties conferenceProperties = new ConferenceProperties();
      conferenceProperties.setAllowedConferenceSolutionTypes(conferenceSolutionTypes);
      newCalendar.setConferenceProperties(conferenceProperties);

      // Insert the new calendar into Google Calendar
      com.google.api.services.calendar.model.Calendar calendar = service.calendars()
              .insert(newCalendar)
              .execute();
      return CreateCalendarResponse.builder()
              .calendarId(calendar.getId())
              .calendar(GoogleCalendarMapper.mapToCalendarResponse(calendar))
              .build();
    } catch (IOException ex) {
      log.error("Error occurred while creating calendar. Reason: {}", ex.getMessage());
    }
    return null;
  }

  /**
   * Retrieves a list of calendars from Google Calendar based on the provided request parameters.
   *
   * <p>This method lists calendars from the authenticated user's account based on the specified
   * page token, and whether to show deleted or hidden calendars. If an error occurs during the
   * retrieval process, it is logged.</p>
   *
   * @param listCalendarRequest the request object containing parameters for listing calendars
   * @see <a href="https://developers.google.com/calendar/api/v3/reference/calendarList/list">
   *   CalendarList: list</a>
   *
   * @return {@link ListCalendarResponse} the response containing the list of calendars
   */
  public ListCalendarResponse listCalendars(ListCalendarRequest listCalendarRequest) {
    try {
      // Retrieve calendar list from the Google Calendar service based on request parameters
      CalendarList calendarList = service.calendarList()
              .list()
              .setPageToken(listCalendarRequest.getPageToken())
              .setShowDeleted(listCalendarRequest.getShowDeleted())
              .setShowHidden(listCalendarRequest.getShowHidden())
              .execute();

      if (nonNull(calendarList)) {
        // Get the list of calendar entries from the retrieved calendar list
        List<CalendarListEntry> calendarListEntries = calendarList.getItems();

        return ListCalendarResponse.builder()
                .calendars(GoogleCalendarMapper.mapToCalendarsResponse(calendarListEntries))
                .build();
      }
    } catch (IOException ex) {
      log.error("Error occurred while listing calendar. Reason: {}", ex.getMessage());
    }
    return ListCalendarResponse.builder()
            .build();
  }

  /**
   * Retrieves a specific calendar from Google Calendar based on the provided request parameters.
   *
   * <p>This method retrieves the calendar identified by {@code calendarId} from the Google Calendar service.
   * If an error occurs during the retrieval process, it is logged.</p>
   *
   * @param retrieveCalendarRequest the request object containing the calendar ID to retrieve
   * @return {@link RetrieveCalendarResponse} the response containing the retrieved calendar
   *
   * @see <a href="https://developers.google.com/calendar/api/v3/reference/calendars/get">
   *   Calendars: get</a>
   */
  public RetrieveCalendarResponse retrieveCalendar(RetrieveCalendarRequest retrieveCalendarRequest) {
    try {
      // Retrieve the calendar from Google Calendar service based on the calendar ID
      com.google.api.services.calendar.model.Calendar calendar = service
              .calendars()
              .get(retrieveCalendarRequest.getCalendarId())
              .execute();

      if (nonNull(calendar)) {
        return RetrieveCalendarResponse.builder()
                .calendarId(calendar.getId())
                .calendar(GoogleCalendarMapper.mapToCalendarResponse(calendar))
                .build();
      }
    } catch (IOException ex) {
      log.error("Error occurred while retrieving calendar. Reason: {}", ex.getMessage());
    }
    return null;
  }

  /**
   * Deletes a specific calendar from Google Calendar based on the provided request parameters.
   *
   * <p>This method deletes the calendar identified by {@code calendarId} from the Google Calendar service.
   * If an error occurs during the deletion process, it is logged.</p>
   *
   * @param deleteCalendarRequest the request object containing the calendar ID to delete
   * @return {@link DeleteCalendarResponse} the response containing the calendar that was deleted
   *
   * @see <a href="https://developers.google.com/calendar/api/v3/reference/calendars/delete">
   *   Calendars: delete</a>
   */
  public DeleteCalendarResponse deleteCalendar(DeleteCalendarRequest deleteCalendarRequest) {
    try {
      com.google.api.services.calendar.model.Calendar calendar = service.calendars()
              .get(deleteCalendarRequest.getCalendarId())
              .execute();

      if (nonNull(calendar)) {
        // Delete the calendar from Google Calendar service based on the calendar ID
        service.calendars()
                .delete(deleteCalendarRequest.getCalendarId())
                .execute();

        return DeleteCalendarResponse.builder()
                .calendarId(deleteCalendarRequest.getCalendarId())
                .calendar(GoogleCalendarMapper.mapToCalendarResponse(calendar))
                .build();
      }
    } catch (IOException ex) {
      log.error("Error occurred while deleting calendar. Reason: {}", ex.getMessage());
    }
    return null;
  }

  /**
   * Patches a specific calendar on Google Calendar based on the provided request parameters.
   *
   * <p>This method retrieves the calendar identified by {@code calendarId} from the Google Calendar service,
   * updates its summary, description, and timezone based on the provided request, and patches the calendar.
   * If an error occurs during the patching process, it is logged.</p>
   *
   * @param patchCalendarRequest the request object containing the calendar ID, new title, description, and timezone
   * @return {@link PatchCalendarResponse} the response containing the patched calendar
   *
   * @see <a href="https://developers.google.com/calendar/api/v3/reference/calendars/patch">
   *   Calendars: patch</a>
   */
  public PatchCalendarResponse patchCalendar(PatchCalendarRequest patchCalendarRequest) {
    try {
      // Retrieve the calendar from Google Calendar service based on the calendar ID
      com.google.api.services.calendar.model.Calendar calendar = service.calendars()
              .get(patchCalendarRequest.getCalendarId())
              .execute();

      // If the calendar exists, update its properties and patch it
      if (nonNull(calendar)) {
        calendar.setSummary(patchCalendarRequest.getTitle());
        calendar.setDescription(patchCalendarRequest.getDescription());
        calendar.setTimeZone(patchCalendarRequest.getTimezone());

        // Patch the calendar with updated properties
        com.google.api.services.calendar.model.Calendar patchedCalendar = service.calendars()
                .patch(patchCalendarRequest.getCalendarId(), calendar)
                .execute();

        return PatchCalendarResponse.builder()
                .calendarId(patchCalendarRequest.getCalendarId())
                .calendar(GoogleCalendarMapper.mapToCalendarResponse(patchedCalendar))
                .build();
      }
    } catch (IOException ex) {
      log.error("Error occurred while patching calendar. Reason: {}", ex.getMessage());
    }
    return null;
  }

  /**
   * Shares a specific calendar with a user based on the provided request parameters.
   *
   * <p>This method retrieves the calendar identified by {@code calendarId} from the Google Calendar service,
   * creates an ACL rule to grant specified access rights to the user identified by {@code emailAddress},
   * and shares the calendar accordingly. If an error occurs during the sharing process, it is logged.</p>
   *
   * @param shareCalendarWithUserRequest the request object containing the calendar ID, email address of the user,
   *                                     ACL scope type, and ACL role
   * @return {@link ShareCalendarWithUserResponse} the response containing the calendar shared with the user
   *
   * @see <a href="https://developers.google.com/calendar/api/v3/reference/acl">
   *   Acl</a>
   */
  public ShareCalendarWithUserResponse shareCalendarWithUser(ShareCalendarWithUserRequest shareCalendarWithUserRequest) {
    try {
      // Retrieve the calendar from Google Calendar service based on the calendar ID
      com.google.api.services.calendar.model.Calendar calendar = service.calendars()
              .get(shareCalendarWithUserRequest.getCalendarId())
              .execute();

      if (nonNull(calendar)) {
        // Create an ACL rule to specify access rights for the user
        AclRule aclRule = new AclRule();
        AclRule.Scope scope = new AclRule.Scope();
        scope.setType(shareCalendarWithUserRequest.getAclScopeType().getValue())
                .setValue(shareCalendarWithUserRequest.getEmailAddress());

        aclRule.setScope(scope).setRole(shareCalendarWithUserRequest.getAclRole().getValue());

        // Insert the ACL rule to share the calendar with the user
        service.acl()
                .insert(shareCalendarWithUserRequest.getCalendarId(), aclRule)
                .execute();

        return ShareCalendarWithUserResponse.builder()
                .calendarId(shareCalendarWithUserRequest.getCalendarId())
                .userEmailAddress(shareCalendarWithUserRequest.getEmailAddress())
                .calendar(GoogleCalendarMapper.mapToCalendarResponse(calendar))
                .build();
      }
    } catch (IOException ex) {
      log.error("Error occurred while sharing calendar. Reason: {}", ex.getMessage());
    }
    return null;
  }
}
