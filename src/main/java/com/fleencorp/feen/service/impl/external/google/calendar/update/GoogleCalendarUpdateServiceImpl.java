package com.fleencorp.feen.service.impl.external.google.calendar.update;

import com.fleencorp.feen.aspect.MeasureExecutionTime;
import com.fleencorp.feen.calendar.model.request.calendar.ShareCalendarWithUserRequest;
import com.fleencorp.feen.exception.base.UnableToCompleteOperationException;
import com.fleencorp.feen.model.response.external.google.calendar.calendar.GoogleShareCalendarWithUserResponse;
import com.fleencorp.feen.oauth2.service.external.impl.external.GoogleOauth2ServiceImpl;
import com.fleencorp.feen.service.external.google.calendar.update.GoogleCalendarUpdateService;
import com.fleencorp.feen.service.report.ReporterService;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.AclRule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static com.fleencorp.feen.constant.base.ReportMessageType.GOOGLE_CALENDAR;
import static com.fleencorp.feen.mapper.external.GoogleCalendarMapper.mapToCalendarResponse;
import static com.fleencorp.feen.util.LoggingUtil.logIfEnabled;
import static com.fleencorp.feen.util.external.google.GoogleApiUtil.getHttpRequestInitializer;
import static java.util.Objects.nonNull;

/**
 * Implementation of the GoogleCalendarUpdateService interface.
 *
 * <p>This class provides the functionality for updating events in Google Calendar.
 * It implements the methods defined in the GoogleCalendarUpdateService interface to
 * interact with Google's API for calendar updates.</p>
 *
 * @author Yusuf Àlàmú Musa
 * @version 1.0
 */
@Slf4j
@Component
public class GoogleCalendarUpdateServiceImpl implements GoogleCalendarUpdateService {

  private final ReporterService reporterService;
  private final String applicationName;

  /**
   * Constructs a new {@link GoogleCalendarUpdateServiceImpl} configured to interact with the Google Calendar API.
   * This service facilitates operations such as creating, sharing, and managing calendars and events
   * using a delegated service account.
   *
   * @param applicationName the name of the application, used in the User-Agent header for requests to the Google Calendar API.
   *                        This helps identify the application in API usage logs, and is typically specified via the
   *                        {@code application.name} property.
   * @param reporterService the service responsible for reporting events, errors, or other significant actions taken by
   *                        this service. It helps in monitoring and logging interactions with the Google Calendar API.
   */
  public GoogleCalendarUpdateServiceImpl(
    @Value("${application.name}") final String applicationName,
    final ReporterService reporterService) {
    this.applicationName = applicationName;
    this.reporterService = reporterService;
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
   * @return {@link GoogleShareCalendarWithUserResponse} the response containing the calendar shared with the user
   * @throws UnableToCompleteOperationException the operation cannot be completed
   *
   * @see <a href="https://developers.google.com/calendar/api/v3/reference/acl">
   *   Acl</a>
   */
  @MeasureExecutionTime
  @Override
  public GoogleShareCalendarWithUserResponse shareCalendarWithUser(final ShareCalendarWithUserRequest shareCalendarWithUserRequest) {
    try {
      // Retrieve the calendar from Google Calendar service based on the calendar ID
      final com.google.api.services.calendar.model.Calendar calendar = getService(shareCalendarWithUserRequest.getAccessToken()).calendars()
        .get(shareCalendarWithUserRequest.getCalendarId())
        .execute();

      if (nonNull(calendar)) {
        // Create an ACL rule to specify access rights for the user
        final AclRule aclRule = new AclRule();
        final AclRule.Scope scope = new AclRule.Scope();
        scope.setType(shareCalendarWithUserRequest.getAclScopeType().getValue())
          .setValue(shareCalendarWithUserRequest.getEmailAddress());

        // Set the AclRule scope for the user the calendar is to be shared with
        aclRule
          .setScope(scope)
          .setRole(shareCalendarWithUserRequest.getAclRole().getValue());

        // Insert the ACL rule to share the calendar with the user
        getService(shareCalendarWithUserRequest.getAccessToken())
          .acl()
          .insert(shareCalendarWithUserRequest.getCalendarId(), aclRule)
          .execute();

        return GoogleShareCalendarWithUserResponse.of(
          shareCalendarWithUserRequest.getCalendarId(),
          shareCalendarWithUserRequest.getEmailAddress(),
          mapToCalendarResponse(calendar)
        );
      }
      logIfEnabled(log::isErrorEnabled, () -> log.error("Cannot share calendar with user. Calendar does not exist or cannot be found. {}", shareCalendarWithUserRequest.getCalendarId()));
    } catch (final IOException ex) {
      final String errorMessage = String.format("Error occurred while sharing calendar. Reason: %s", ex.getMessage());
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
