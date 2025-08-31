package com.fleencorp.feen.service.impl.external.google.calendar;

import com.fleencorp.feen.calendar.model.request.calendar.*;
import com.fleencorp.feen.common.aspect.MeasureExecutionTime;
import com.fleencorp.feen.common.constant.external.google.calendar.ConferenceSolutionType;
import com.fleencorp.feen.common.exception.UnableToCompleteOperationException;
import com.fleencorp.feen.common.service.report.ReporterService;
import com.fleencorp.feen.model.response.external.google.calendar.calendar.*;
import com.fleencorp.feen.oauth2.service.external.impl.external.GoogleOauth2ServiceImpl;
import com.fleencorp.feen.service.external.google.calendar.GoogleCalendarService;
import com.fleencorp.feen.service.external.google.calendar.update.GoogleCalendarUpdateService;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.CalendarList;
import com.google.api.services.calendar.model.CalendarListEntry;
import com.google.api.services.calendar.model.ConferenceProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static com.fleencorp.feen.calendar.mapper.external.GoogleCalendarMapper.mapToCalendarResponse;
import static com.fleencorp.feen.calendar.mapper.external.GoogleCalendarMapper.mapToCalendarsResponse;
import static com.fleencorp.feen.common.constant.base.ReportMessageType.GOOGLE_CALENDAR;
import static com.fleencorp.feen.common.util.common.LoggingUtil.logIfEnabled;
import static com.fleencorp.feen.common.util.external.google.GoogleApiUtil.getHttpRequestInitializer;
import static com.fleencorp.feen.user.model.domain.Member.isInternalEmailOrEmailIsAnOriginEmail;
import static java.util.Objects.nonNull;
import static java.util.Objects.requireNonNull;

/**
 * A service class for interacting with Google Calendar to create, update, retrieve, and delete calendar.
 *
 * @author Yusuf Alamu Musa
 * @version 1.0
 *
 * @see <a href="https://developers.google.com/calendar/api/guides/overview">
 *   Google Calendar API overview</a>
 */
@Service
@Slf4j
public class GoogleCalendarServiceImpl implements GoogleCalendarService {

  private final GoogleCalendarUpdateService googleCalendarUpdateService;
  private final ReporterService reporterService;
  private final String applicationName;
  private final String serviceAccountDelegatedAuthorityEmail;
  private final String originDomain;

  /**
   * Constructs a new {@link GoogleCalendarService} configured to interact with the Google Calendar API.
   * This service facilitates operations such as creating, sharing, and managing calendars and events
   * using a delegated service account.
   *
   * @param googleCalendarUpdateService the GoogleCalendarUpdateService instance for updating Google Calendar events
   * @param applicationName the name of the application, used in the User-Agent header for requests to the Google Calendar API.
   *                        This helps identify the application in API usage logs, and is typically specified via the
   *                        {@code application.name} property.
   * @param serviceAccountDelegatedAuthorityEmail the email address of the service account with domain-wide delegation.
   *                                              This account is used to perform actions on behalf of users within the domain,
   *                                              as configured via the {@code service.account.delegated.authority.email} property.
   * @param originDomain the domain that is used for defining the context of operations within the service, such as setting
   *                     the appropriate origin for access and permissions. This is specified via the {@code origin-domain} property.
   * @param reporterService the service responsible for reporting events, errors, or other significant actions taken by
   *                        this service. It helps in monitoring and logging interactions with the Google Calendar API.
   */

  public GoogleCalendarServiceImpl(
      final GoogleCalendarUpdateService googleCalendarUpdateService,
      @Value("${application.name}") final String applicationName,
      @Value("${service.account.delegated.authority.email}") final String serviceAccountDelegatedAuthorityEmail,
      @Value("${origin-domain}") final String originDomain,
      final ReporterService reporterService) {
    this.googleCalendarUpdateService = googleCalendarUpdateService;
    this.applicationName = applicationName;
    this.serviceAccountDelegatedAuthorityEmail = serviceAccountDelegatedAuthorityEmail;
    this.originDomain = originDomain;
    this.reporterService = reporterService;
  }

  /**
   * Creates a new calendar on Google Calendar based on the provided request parameters.
   *
   * <p>This method creates a new calendar with the specified title, description, and timezone.
   * It also sets the allowed conference solution types for the new calendar. If an error occurs during
   * the creation process, it is logged.</p>
   *
   * @param createCalendarRequest the request object containing the title, description, and timezone for the new calendar
   * @return {@link GoogleCreateCalendarResponse} the response containing the created calendar
   * @throws UnableToCompleteOperationException the operation cannot be completed
   */
  @Override
  @MeasureExecutionTime
  public GoogleCreateCalendarResponse createCalendar(final CreateCalendarRequest createCalendarRequest) {
    try {
      // Create a new calendar object and set its properties
      final com.google.api.services.calendar.model.Calendar newCalendar = new com.google.api.services.calendar.model.Calendar();
      newCalendar.setSummary(createCalendarRequest.getTitle());
      newCalendar.setDescription(createCalendarRequest.getDescription());
      newCalendar.setTimeZone(createCalendarRequest.getTimezone());

      // Set allowed conference solution types for the new calendar
      final List<String> conferenceSolutionTypes = Arrays.stream(ConferenceSolutionType.values())
        .map(ConferenceSolutionType::getValue)
        .toList();
      final ConferenceProperties conferenceProperties = new ConferenceProperties();
      conferenceProperties.setAllowedConferenceSolutionTypes(conferenceSolutionTypes);
      newCalendar.setConferenceProperties(conferenceProperties);

      // Insert the new calendar into Google Calendar
      final com.google.api.services.calendar.model.Calendar calendar = getService(createCalendarRequest.getAccessToken()).calendars()
              .insert(newCalendar)
              .execute();
      if (nonNull(calendar)) {
        shareCalendarWithServiceAccountEmail(calendar.getId(), createCalendarRequest.getCreatorEmailAddress(), createCalendarRequest.getAccessToken());
        return GoogleCreateCalendarResponse.of(calendar.getId(), requireNonNull(mapToCalendarResponse(calendar)));
      }
    } catch (final IOException ex) {
      final String errorMessage = String.format("Error occurred while creating calendar. Reason: %s", ex.getMessage());
      reporterService.sendMessage(errorMessage, GOOGLE_CALENDAR);
    }
    throw new UnableToCompleteOperationException();
  }

  /**
   * Asynchronously shares a calendar with the service account's email address if the calendar creator's
   * email is internal or matches the specified origin domain. This method checks if the calendar creator's
   * email belongs to the internal domain and, if so, proceeds to share the calendar with the service account
   * using the provided access token.
   *
   * @param calendarId the ID of the calendar to be shared. This uniquely identifies the calendar in the Google Calendar API.
   * @param calendarCreatorEmailAddress the email address of the calendar creator. This is checked against the
   *                                    origin domain to determine if it is internal or authorized.
   * @param accessToken the OAuth 2.0 access token used for authentication and authorization of the calendar sharing request.
   *                    This token must have the necessary permissions to modify the calendar's ACL (Access Control List).
   */
  @Async
  public void shareCalendarWithServiceAccountEmail(final String calendarId, final String calendarCreatorEmailAddress, final String accessToken) {
    if (isInternalEmailOrEmailIsAnOriginEmail(originDomain, calendarCreatorEmailAddress)) {
      final ShareCalendarWithUserRequest scheduleWithUserRequest = ShareCalendarWithUserRequest.of(calendarId, serviceAccountDelegatedAuthorityEmail, accessToken);
      googleCalendarUpdateService.shareCalendarWithUser(scheduleWithUserRequest);
    }
  }

  /**
   * Retrieves a list of calendars from Google Calendar based on the provided request parameters.
   *
   * <p>This method lists calendars from the authenticated user's account based on the specified
   * page token, and whether to show deleted or hidden calendars. If an error occurs during the
   * retrieval process, it is logged.</p>
   *
   * @param listCalendarRequest the request object containing parameters for listing calendars
   * @return {@link GoogleListCalendarResponse} the response containing the list of calendars
   * @throws UnableToCompleteOperationException the operation cannot be completed
   *
   * @see <a href="https://developers.google.com/calendar/api/v3/reference/calendarList/list">
   *   CalendarList: list</a>
   */
  public GoogleListCalendarResponse listCalendars(final ListCalendarRequest listCalendarRequest) {
    try {
      // Retrieve calendar list from the Google Calendar service based on request parameters
      final CalendarList calendarList = getService(listCalendarRequest.getAccessToken()).calendarList()
              .list()
              .setPageToken(listCalendarRequest.getPageToken())
              .setShowDeleted(listCalendarRequest.getShowDeleted())
              .setShowHidden(listCalendarRequest.getShowHidden())
              .execute();

      if (nonNull(calendarList)) {
        // Get the list of calendar entries from the retrieved calendar list
        final List<CalendarListEntry> calendarListEntries = calendarList.getItems();
        return GoogleListCalendarResponse.of(mapToCalendarsResponse(calendarListEntries));
      }
    } catch (final IOException ex) {
      final String errorMessage = String.format("Error occurred while listing calendar. Reason: %s", ex.getMessage());
      reporterService.sendMessage(errorMessage, GOOGLE_CALENDAR);
    }
    return GoogleListCalendarResponse.of();
  }

  /**
   * Retrieves a specific calendar from Google Calendar based on the provided request parameters.
   *
   * <p>This method retrieves the calendar identified by {@code calendarId} from the Google Calendar service.
   * If an error occurs during the retrieval process, it is logged.</p>
   *
   * @param retrieveCalendarRequest the request object containing the calendar ID to retrieve
   * @return {@link GoogleRetrieveCalendarResponse} the response containing the retrieved calendar
   *
   * @see <a href="https://developers.google.com/calendar/api/v3/reference/calendars/get">
   *   Calendars: get</a>
   */
  @Override
  @MeasureExecutionTime
  public GoogleRetrieveCalendarResponse retrieveCalendar(final RetrieveCalendarRequest retrieveCalendarRequest) {
    try {
      // Retrieve the calendar from Google Calendar service based on the calendar ID
      final com.google.api.services.calendar.model.Calendar calendar = getService(retrieveCalendarRequest.getAccessToken())
              .calendars()
              .get(retrieveCalendarRequest.getCalendarId())
              .execute();

      if (nonNull(calendar)) {
        return GoogleRetrieveCalendarResponse.of(calendar.getId(), mapToCalendarResponse(calendar));
      }
    } catch (final IOException ex) {
      final String errorMessage = String.format("Error occurred while retrieving calendar. Reason: %s", ex.getMessage());
      reporterService.sendMessage(errorMessage, GOOGLE_CALENDAR);
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
   * @return {@link GoogleDeleteCalendarResponse} the response containing the calendar that was deleted
   * @throws UnableToCompleteOperationException the operation cannot be completed
   *
   * @see <a href="https://developers.google.com/calendar/api/v3/reference/calendars/delete">
   *   Calendars: delete</a>
   */
  @Override
  @MeasureExecutionTime
  public GoogleDeleteCalendarResponse deleteCalendar(final DeleteCalendarRequest deleteCalendarRequest) {
    try {
      final RetrieveCalendarRequest retrieveCalendarRequest = RetrieveCalendarRequest.of(deleteCalendarRequest.getCalendarId(), deleteCalendarRequest.getAccessToken());
      final GoogleRetrieveCalendarResponse googleRetrieveCalendarResponse = retrieveCalendar(retrieveCalendarRequest);

      if (nonNull(googleRetrieveCalendarResponse.calendarResponse())) {
        // Delete the calendar from Google Calendar service based on the calendar ID
        getService(deleteCalendarRequest.getAccessToken())
          .calendars()
          .delete(deleteCalendarRequest.getCalendarId())
          .execute();

        return GoogleDeleteCalendarResponse.of(deleteCalendarRequest.getCalendarId(), googleRetrieveCalendarResponse.calendarResponse());
      }
      logIfEnabled(log::isErrorEnabled, () -> log.error("Cannot delete. Calendar does not exist or cannot be found. {}", deleteCalendarRequest.getCalendarId()));
    } catch (final IOException ex) {
      final String errorMessage = String.format("Error occurred while deleting calendar. Reason: %s", ex.getMessage());
      reporterService.sendMessage(errorMessage, GOOGLE_CALENDAR);
    }
    throw new UnableToCompleteOperationException();
  }

  /**
   * Patches a specific calendar on Google Calendar based on the provided request parameters.
   *
   * <p>This method retrieves the calendar identified by {@code calendarId} from the Google Calendar service,
   * updates its summary, description, and timezone based on the provided request, and patches the calendar.
   * If an error occurs during the patching process, it is logged.</p>
   *
   * @param patchCalendarRequest the request object containing the calendar ID, new title, description, and timezone
   * @return {@link GooglePatchCalendarResponse} the response containing the patched calendar
   * @throws UnableToCompleteOperationException the operation cannot be completed
   *
   * @see <a href="https://developers.google.com/calendar/api/v3/reference/calendars/patch">
   *   Calendars: patch</a>
   */
  @Override
  @MeasureExecutionTime
  public GooglePatchCalendarResponse patchCalendar(final PatchCalendarRequest patchCalendarRequest) {
    try {
      // Retrieve the calendar from Google Calendar service based on the calendar ID
      final com.google.api.services.calendar.model.Calendar calendar = getService(patchCalendarRequest.getAccessToken())
              .calendars()
              .get(patchCalendarRequest.getCalendarId())
              .execute();

      // If the calendar exists, update its properties and patch it
      if (nonNull(calendar)) {
        calendar.setSummary(patchCalendarRequest.getTitle());
        calendar.setDescription(patchCalendarRequest.getDescription());
        calendar.setTimeZone(patchCalendarRequest.getTimezone());

        // Patch the calendar with updated properties
        final com.google.api.services.calendar.model.Calendar patchedCalendar = getService(patchCalendarRequest.getAccessToken())
                .calendars()
                .patch(patchCalendarRequest.getCalendarId(), calendar)
                .execute();

        return GooglePatchCalendarResponse.of(patchCalendarRequest.getCalendarId(), mapToCalendarResponse(patchedCalendar));
      }
      logIfEnabled(log::isErrorEnabled, () -> log.error("Cannot update. Calendar does not exist or cannot be found. {}", patchCalendarRequest.getCalendarId()));
    } catch (final IOException ex) {
      final String errorMessage = String.format("Error occurred while patching calendar. Reason: %s", ex.getMessage());
      reporterService.sendMessage(errorMessage, GOOGLE_CALENDAR);
    }
    throw new UnableToCompleteOperationException();
  }

  /**
   * Creates and returns a Google Calendar service instance authenticated with the provided access token.
   *
   * <p>This method initializes the Google Calendar API client using the given access token. It configures the
   * HTTP transport, JSON factory, and HTTP request initializer to build a {@link Calendar} service instance.</p>
   *
   * @param accessToken the OAuth2 access token used to authenticate the API requests
   * @return a {@link Calendar} service instance configured for the authenticated user
   */
  private Calendar getService(final String accessToken) {
    return new Calendar.Builder(GoogleOauth2ServiceImpl.getTransport(), GoogleOauth2ServiceImpl.getJsonFactory(), getHttpRequestInitializer(accessToken))
      .setApplicationName(applicationName)
      .build();
  }
}
