package com.fleencorp.feen.calendar.service.impl;

import com.fleencorp.base.model.view.search.SearchResult;
import com.fleencorp.feen.calendar.exception.core.CalendarAlreadyActiveException;
import com.fleencorp.feen.calendar.exception.core.CalendarAlreadyExistException;
import com.fleencorp.feen.calendar.exception.core.CalendarNotFoundException;
import com.fleencorp.feen.calendar.model.domain.Calendar;
import com.fleencorp.feen.calendar.model.dto.CreateCalendarDto;
import com.fleencorp.feen.calendar.model.dto.ShareCalendarWithUserDto;
import com.fleencorp.feen.calendar.model.dto.UpdateCalendarDto;
import com.fleencorp.feen.calendar.model.request.calendar.CreateCalendarRequest;
import com.fleencorp.feen.calendar.model.request.calendar.DeleteCalendarRequest;
import com.fleencorp.feen.calendar.model.request.calendar.PatchCalendarRequest;
import com.fleencorp.feen.calendar.model.request.calendar.ShareCalendarWithUserRequest;
import com.fleencorp.feen.calendar.model.request.search.CalendarSearchRequest;
import com.fleencorp.feen.calendar.model.response.*;
import com.fleencorp.feen.calendar.model.response.base.CalendarResponse;
import com.fleencorp.feen.calendar.model.search.CalendarSearchResult;
import com.fleencorp.feen.calendar.repository.CalendarRepository;
import com.fleencorp.feen.calendar.service.CalendarService;
import com.fleencorp.feen.country.model.search.CountrySearchResult;
import com.fleencorp.feen.country.service.CountryService;
import com.fleencorp.feen.model.request.search.CountrySearchRequest;
import com.fleencorp.feen.model.response.external.google.calendar.calendar.GoogleCreateCalendarResponse;
import com.fleencorp.feen.model.response.external.google.calendar.calendar.GoogleDeleteCalendarResponse;
import com.fleencorp.feen.model.response.external.google.calendar.calendar.GooglePatchCalendarResponse;
import com.fleencorp.feen.model.response.external.google.calendar.calendar.GoogleShareCalendarWithUserResponse;
import com.fleencorp.feen.oauth2.constant.Oauth2ServiceType;
import com.fleencorp.feen.oauth2.model.domain.Oauth2Authorization;
import com.fleencorp.feen.oauth2.service.external.GoogleOauth2Service;
import com.fleencorp.feen.service.external.google.calendar.GoogleCalendarService;
import com.fleencorp.feen.service.external.google.calendar.update.GoogleCalendarUpdateService;
import com.fleencorp.feen.user.model.security.RegisteredUser;
import com.fleencorp.localizer.service.Localizer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import static com.fleencorp.base.util.FleenUtil.toSearchResult;
import static com.fleencorp.feen.calendar.mapper.CalendarMapper.toCalendarResponse;
import static com.fleencorp.feen.calendar.mapper.CalendarMapper.toCalendarResponses;
import static com.fleencorp.feen.common.util.LoggingUtil.logIfEnabled;
import static com.fleencorp.feen.common.validator.impl.TimezoneValidValidator.getAvailableTimezones;
import static java.util.Objects.nonNull;

/**
 * Implementation of the CalendarService interface.
 * This class provides methods for calendar-related operations.
 *
 * @author Yusuf Àlàmù Mùsà
 * @version 1.0
 */
@Slf4j
@Service
public class CalendarServiceImpl implements CalendarService {

  private final CountryService countryService;
  private final GoogleCalendarService googleCalendarService;
  private final GoogleCalendarUpdateService googleCalendarUpdateService;
  private final CalendarRepository calendarRepository;
  private final GoogleOauth2Service googleOauth2Service;
  private final Localizer localizer;

  /**
   * Creates an instance of CalendarServiceImpl with the specified services and repository.
   *
   * <p>This constructor initializes the CalendarServiceImpl instance with the provided
   * CountryService for handling country-related operations, GoogleCalendarService and
   * GoogleCalendarUpdateService for interacting with Google Calendar, GoogleOauth2Service
   * for OAuth2 authentication, CalendarRepository for managing calendar data, and Localizer
   * for localization tasks.</p>
   *
   * @param countryService the CountryService instance for country-related functionality
   * @param googleCalendarService the GoogleCalendarService instance for interacting with Google Calendar
   * @param googleCalendarUpdateService the GoogleCalendarUpdateService instance for updating Google Calendar events
   * @param googleOauth2Service the GoogleOauth2Service instance for handling OAuth2 authentication
   * @param calendarRepository the CalendarRepository instance for accessing and storing calendar data
   * @param localizer the Localizer instance for localization operations
   */
  public CalendarServiceImpl(
      final CountryService countryService,
      final GoogleCalendarService googleCalendarService,
      final GoogleCalendarUpdateService googleCalendarUpdateService,
      final GoogleOauth2Service googleOauth2Service,
      final CalendarRepository calendarRepository,
      final Localizer localizer) {
    this.countryService = countryService;
    this.googleCalendarService = googleCalendarService;
    this.googleCalendarUpdateService = googleCalendarUpdateService;
    this.googleOauth2Service = googleOauth2Service;
    this.calendarRepository = calendarRepository;
    this.localizer = localizer;
  }

  /**
   * Retrieves data required for creating a calendar, including a list of countries and available timezones.
   *
   * @return a DataForCreateCalendarResponse object containing a list of countries and a set of timezones
   */
  @Override
  public DataForCreateCalendarResponse getDataForCreateCalendar() {
    // Create the search request
    final CountrySearchRequest countrySearchRequest = CountrySearchRequest.ofPageSize(1000);
    // Fetch a list of countries with a large number of entries (1000 in this case).
    final CountrySearchResult searchResult = countryService.findCountries(countrySearchRequest);
    // Get the countries in the search result
    final Collection<?> countries = searchResult.getResult().getValues();
    // Get the set of available timezones.
    final Set<String> timezones = getAvailableTimezones();
    // Create the response
    final DataForCreateCalendarResponse dataForCreateCalendarResponse = DataForCreateCalendarResponse.of(timezones, countries);
    // Return the response object containing both the countries and timezones.
    return localizer.of(dataForCreateCalendarResponse);
  }

  /**
  * Finds calendars based on the search criteria provided in the CalendarSearchRequest.
  *
  * @param searchRequest the request containing the search criteria
  * @return a CalendarSearchResult containing the search results
  */
  @Override
  public CalendarSearchResult findCalendars(final CalendarSearchRequest searchRequest) {
    final Page<Calendar> page;
    final Pageable pageable = searchRequest.getPage();
    final Boolean active = searchRequest.getActive();
    final String title = searchRequest.getTitle();
    final LocalDateTime startDate = searchRequest.getStartDateTime();
    final LocalDateTime endDate = searchRequest.getEndDateTime();


    if (searchRequest.areAllDatesSet()) {
      page = calendarRepository.findByDateBetween(startDate, endDate, pageable);
    } else if (nonNull(title))  {
      page = calendarRepository.findByTitle(title, pageable);
    } else if (nonNull(active)) {
      page = calendarRepository.findByIsActive(active, pageable);
    } else {
      page = calendarRepository.findMany(pageable);
    }

    final List<CalendarResponse> calendarResponses = toCalendarResponses(page.getContent());
    // Create a search result
    final SearchResult searchResult = toSearchResult(calendarResponses, page);
    // Create a search result with the responses and pagination details
    final CalendarSearchResult calendarSearchResult = CalendarSearchResult.of(searchResult);
    // Return the search result
    return localizer.of(calendarSearchResult);
  }

  /**
  * Finds a calendar by its ID.
  *
  * @param calendarId the ID of the calendar to find
  * @return {@link RetrieveCalendarResponse} containing the calendar details
  * @throws CalendarNotFoundException if the calendar with the specified ID is not found
  */
  @Override
  public RetrieveCalendarResponse findCalendar(final Long calendarId) {
    final Calendar calendar = calendarRepository.findById(calendarId)
      .orElseThrow(CalendarNotFoundException.of(calendarId));
    return localizer.of(RetrieveCalendarResponse.of(calendarId, toCalendarResponse(calendar)));
  }

  /**
  * Creates a new calendar based on the information provided in the {@link CreateCalendarDto}.
  *
  * <p>This method creates a new calendar using the details from the {@code CreateCalendarDto},
  * interacts with an external service (Google Calendar) to create the calendar,
  * and then saves the created calendar to the local repository.</p>
  *
  * <p>It first converts the DTO to a {@link Calendar} entity, retrieves the corresponding
  * country using the {@link CountryService}, and sets the country code for the calendar.
  * Then, it constructs a {@link CreateCalendarRequest} and sends it to the Google Calendar
  * service to create the calendar. After receiving the response, it sets the external ID
  * for the calendar, saves it to the local repository, and returns a {@link CalendarResponse}
  * with the newly created calendar details.</p>
  *
  * @param createCalendarDto the DTO containing the details for creating the calendar
  * @return a {@link CreateCalendarResponse} containing the newly created calendar details
  */
  @Override
  @Transactional
  public CreateCalendarResponse createCalendar(final CreateCalendarDto createCalendarDto, final RegisteredUser user) {
    Calendar calendar = createCalendarDto.toCalendar();

    // Check no calendar exist with matching country code or else throw an exception
    calendarRepository.findDistinctByCodeIgnoreCase(calendar.getCode())
      .ifPresent((existingCalendar) -> {
        throw CalendarAlreadyExistException.of(existingCalendar.getCode());
      });

    // Retrieve user oauth2 authorization details associated with Google Calendar
    final Oauth2Authorization oauth2Authorization = validateAccessTokenExpiryTimeOrRefreshToken(createCalendarDto.getOauth2ServiceType(), user);
    // Create a create calendar request to be used in external service
    final CreateCalendarRequest createCalendarRequest = CreateCalendarRequest.of(
      createCalendarDto.getTitle(),
      createCalendarDto.getDescription(),
      createCalendarDto.getTimezone(),
      oauth2Authorization.getAccessToken(),
      user.getEmailAddress()
    );
    // Save new calendar
    calendar = calendarRepository.save(calendar);

    // Create a calendar using Google Calendar API Service
    final GoogleCreateCalendarResponse googleCreateCalendarResponse = googleCalendarService.createCalendar(createCalendarRequest);
    calendar.setExternalId(googleCreateCalendarResponse.calendarId());

    // Create the response
    final CreateCalendarResponse createCalendarResponse = CreateCalendarResponse.of(calendar.getCalendarId(), toCalendarResponse(calendar));
    // Return the response
    return localizer.of(createCalendarResponse);
  }

  /**
  * Updates an existing calendar identified by the calendarId with the details provided in the UpdateCalendarDto.
  *
  * <p>This method updates the calendar's details both locally and in the external service (Google Calendar)
  * using the provided information from the UpdateCalendarDto.</p>
  *
  * @param calendarId the ID of the calendar to update
  * @param updateCalendarDto the DTO containing the updated details for the calendar
  * @return {@link UpdateCalendarResponse} containing the updated calendar details
  * @throws CalendarNotFoundException if the calendar with the specified ID is not found
  */
  @Override
  @Transactional
  public UpdateCalendarResponse updateCalendar(final Long calendarId, final UpdateCalendarDto updateCalendarDto, final RegisteredUser user) {
    Calendar calendar = calendarRepository.findById(calendarId)
      .orElseThrow(CalendarNotFoundException.of(calendarId));

    // Retrieve user oauth2 authorization details associated with Google Calendar
    // Validate access token expiry time and refresh if expired
    final Oauth2Authorization oauth2Authorization = validateAccessTokenExpiryTimeOrRefreshToken(updateCalendarDto.getOauth2ServiceType(), user);

    // Prepare request to patch calendar in external service
    final PatchCalendarRequest patchCalendarRequest = PatchCalendarRequest.of(
      calendar.getExternalId(),
      updateCalendarDto.getTitle(),
      updateCalendarDto.getDescription(),
      updateCalendarDto.getTimezone(),
      oauth2Authorization.getAccessToken()
    );

    // Update calendar details locally
    calendar.update(
      updateCalendarDto.getTitle(),
      updateCalendarDto.getDescription(),
      updateCalendarDto.getTimezone()
    );
    // Save updated calendar
    calendar = calendarRepository.save(calendar);

    // Update the calendar through Google Calendar API Service
    final GooglePatchCalendarResponse googlePatchCalendarResponse = googleCalendarService.patchCalendar(patchCalendarRequest);
    logIfEnabled(log::isInfoEnabled, () -> log.info("Patch calendar response: {}", googlePatchCalendarResponse));

    // Create the response
    final UpdateCalendarResponse updateCalendarResponse = UpdateCalendarResponse.of(calendar.getCalendarId(), toCalendarResponse(calendar));
    // Return the response
    return localizer.of(updateCalendarResponse);
  }

  /**
   * Reactivates a calendar by its ID for the specified user. This method validates the user's OAuth2 authorization
   * for Google Calendar, updates the calendar details in the system, and then reactivates the calendar through the
   * Google Calendar API. The updated calendar is saved and its external ID is set based on the response from Google.
   *
   * @param calendarId The ID of the calendar to be reactivated.
   * @param user       The user who owns the calendar and whose OAuth2 authorization is to be validated.
   * @return A {@link ReactivateCalendarResponse} object containing the reactivated calendar's details.
   * @throws CalendarNotFoundException if no calendar is found with the specified ID.
   */
  @Override
  @Transactional
  public ReactivateCalendarResponse reactivateCalendar(final Long calendarId, final RegisteredUser user) {
    Calendar calendar = calendarRepository.findById(calendarId)
      .orElseThrow(CalendarNotFoundException.of(calendarId));

    // Verify calendar is not already active
    verifyCalendarIsNotAlreadyActive(calendar);
    // Retrieve user oauth2 authorization details associated with Google Calendar
    // Validate access token expiry time and refresh if expired
    final Oauth2Authorization oauth2Authorization = validateAccessTokenExpiryTimeOrRefreshToken(Oauth2ServiceType.googleCalendar(), user);

    // Create a create calendar request to be used in external service
    final CreateCalendarRequest createCalendarRequest = CreateCalendarRequest.of(
      calendar.getTitle(),
      calendar.getDescription(),
      calendar.getTimezone(),
      oauth2Authorization.getAccessToken(),
      user.getEmailAddress()
    );

    calendar.markAsActive();
    // Save updated calendar
    calendar = calendarRepository.save(calendar);

    // Update the calendar through Google Calendar API Service
    final GoogleCreateCalendarResponse googleCreateCalendarResponse = googleCalendarService.createCalendar(createCalendarRequest);
    calendar.setExternalId(googleCreateCalendarResponse.calendarId());

    // Create the response
    final ReactivateCalendarResponse reactivateCalendarResponse = ReactivateCalendarResponse.of(calendar.getCalendarId(), toCalendarResponse(calendar));
    // Return the response
    return localizer.of(reactivateCalendarResponse);
  }

  /**
  * Deletes a calendar identified by the calendarId.
  *
  * <p>This method first deletes the calendar from the external service (Google Calendar),
  * logs the deletion response, and then deletes the calendar from the local repository.</p>
  *
  * @param calendarId the ID of the calendar to delete
  * @return a DeleteResponse indicating the deletion of the calendar
  * @throws CalendarNotFoundException if the calendar with the specified ID is not found
  */
  @Override
  @Transactional
  public DeletedCalendarResponse deleteCalendar(final Long calendarId, final RegisteredUser user) {
    final Calendar calendar = calendarRepository.findById(calendarId)
      .orElseThrow(CalendarNotFoundException.of(calendarId));

    // Retrieve user oauth2 authorization details associated with Google Calendar
    // Validate access token expiry time and refresh if expired
    final Oauth2Authorization oauth2Authorization = validateAccessTokenExpiryTimeOrRefreshToken(Oauth2ServiceType.googleCalendar(), user);

    // Prepare request to delete calendar from external service
    final DeleteCalendarRequest deleteCalendarRequest = DeleteCalendarRequest.of(calendar.getExternalId(), oauth2Authorization.getAccessToken());
    final GoogleDeleteCalendarResponse deleteCalendarResponse = googleCalendarService.deleteCalendar(deleteCalendarRequest);

    // Log deletion response from external service
    logIfEnabled(log::isInfoEnabled, () -> log.info("Deleted calendar: {}", deleteCalendarResponse));
    calendar.markAsInactive();

    calendarRepository.save(calendar);
    return localizer.of(DeletedCalendarResponse.of(calendarId));
  }

  /**
  * Shares a calendar with a user based on the details provided in the {@link ShareCalendarWithUserDto}.
  *
  * <p>This method retrieves the calendar by its ID from the repository. If the calendar is not found,
  * it throws a {@link CalendarNotFoundException}. It then constructs a {@link ShareCalendarWithUserRequest}
  * using the details from the {@code ShareCalendarWithUserDto} and sends this request to the Google Calendar
  * service to share the calendar with the specified user. The response from the Google Calendar service
  * is logged for auditing purposes. Finally, it returns a {@link ShareCalendarWithUserResponse} containing
  * the email address of the user the calendar was shared with and the calendar details.</p>
  *
  * @param calendarId the unique identifier of the calendar to be shared
  * @param shareCalendarWithUserDto the DTO containing the details for sharing the calendar
  * @return a {@link ShareCalendarWithUserResponse} containing the email address of the user and the calendar details
  * @throws CalendarNotFoundException if the calendar with the specified ID is not found
  */
  @Override
  public ShareCalendarWithUserResponse shareCalendarWithUser(final Long calendarId, final ShareCalendarWithUserDto shareCalendarWithUserDto, final RegisteredUser user) {
    final Calendar calendar = calendarRepository.findById(calendarId)
      .orElseThrow(CalendarNotFoundException.of(calendarId));

    // Retrieve user oauth2 authorization details associated with Google Calendar
    final Oauth2Authorization oauth2Authorization = validateAccessTokenExpiryTimeOrRefreshToken(Oauth2ServiceType.googleCalendar(), user);

    // Construct the request for sharing the calendar
    final ShareCalendarWithUserRequest shareCalendarWithUserRequest = ShareCalendarWithUserRequest.of(
      calendar.getExternalId(),
      shareCalendarWithUserDto.getEmailAddress(),
      shareCalendarWithUserDto.getAclScopeType(),
      shareCalendarWithUserDto.getAclRole(),
      oauth2Authorization.getAccessToken()
    );

    // Share the calendar with the user using the Google Calendar service
    final GoogleShareCalendarWithUserResponse googleShareCalendarWithUserResponse = googleCalendarUpdateService.shareCalendarWithUser(shareCalendarWithUserRequest);
    logIfEnabled(log::isInfoEnabled, () -> log.info("Shared calendar: {} with user {}", googleShareCalendarWithUserResponse, shareCalendarWithUserDto.getEmailAddress()));

    // Return share calendar response
    final ShareCalendarWithUserResponse shareCalendarWithUserResponse = ShareCalendarWithUserResponse
      .of(calendarId,
        shareCalendarWithUserDto.getEmailAddress(),
        toCalendarResponse(calendar)
      );
    return localizer.of(shareCalendarWithUserResponse);
  }

  /**
   * Validates the expiry time of the access token for the specified OAuth2 service type and user,
   * or refreshes the token if necessary.
   *
   * @param oauth2ServiceType the type of OAuth2 service (e.g., Google, Facebook) to validate or refresh the token for
   * @param user the user whose access token is being validated or refreshed
   * @return an {@link Oauth2Authorization} object containing updated authorization details
   */
  public Oauth2Authorization validateAccessTokenExpiryTimeOrRefreshToken(final Oauth2ServiceType oauth2ServiceType, final RegisteredUser user) {
    return googleOauth2Service.validateAccessTokenExpiryTimeOrRefreshToken(oauth2ServiceType, user);
  }

  /**
   * Verifies that the given calendar is not already active. If the calendar is active, an exception is thrown.
   *
   * @param calendar The calendar to be checked for active status. If the calendar is {@code null} or the active
   *                 status is not set, the method does nothing.
   * @throws CalendarAlreadyActiveException if the calendar is already active.
   */
  public void verifyCalendarIsNotAlreadyActive(final Calendar calendar) {
    if (nonNull(calendar) && nonNull(calendar.getIsActive()) && calendar.getIsActive()) {
      throw new CalendarAlreadyActiveException();
    }
  }
}
