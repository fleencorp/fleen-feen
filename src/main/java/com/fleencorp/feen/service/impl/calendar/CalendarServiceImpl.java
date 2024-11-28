package com.fleencorp.feen.service.impl.calendar;

import com.fleencorp.base.service.i18n.LocalizedResponse;
import com.fleencorp.feen.constant.external.google.oauth2.Oauth2ServiceType;
import com.fleencorp.feen.exception.calendar.CalendarAlreadyActiveException;
import com.fleencorp.feen.exception.calendar.CalendarAlreadyExistException;
import com.fleencorp.feen.exception.calendar.CalendarNotFoundException;
import com.fleencorp.feen.model.domain.auth.Oauth2Authorization;
import com.fleencorp.feen.model.domain.calendar.Calendar;
import com.fleencorp.feen.model.dto.calendar.CreateCalendarDto;
import com.fleencorp.feen.model.dto.calendar.ShareCalendarWithUserDto;
import com.fleencorp.feen.model.dto.calendar.UpdateCalendarDto;
import com.fleencorp.feen.model.request.calendar.calendar.CreateCalendarRequest;
import com.fleencorp.feen.model.request.calendar.calendar.DeleteCalendarRequest;
import com.fleencorp.feen.model.request.calendar.calendar.PatchCalendarRequest;
import com.fleencorp.feen.model.request.calendar.calendar.ShareCalendarWithUserRequest;
import com.fleencorp.feen.model.request.search.CountrySearchRequest;
import com.fleencorp.feen.model.request.search.calendar.CalendarSearchRequest;
import com.fleencorp.feen.model.response.calendar.*;
import com.fleencorp.feen.model.response.calendar.base.CalendarResponse;
import com.fleencorp.feen.model.response.external.google.calendar.calendar.GoogleCreateCalendarResponse;
import com.fleencorp.feen.model.response.external.google.calendar.calendar.GoogleDeleteCalendarResponse;
import com.fleencorp.feen.model.response.external.google.calendar.calendar.GooglePatchCalendarResponse;
import com.fleencorp.feen.model.response.external.google.calendar.calendar.GoogleShareCalendarWithUserResponse;
import com.fleencorp.feen.model.search.calendar.CalendarSearchResult;
import com.fleencorp.feen.model.search.calendar.EmptyCalendarSearchResult;
import com.fleencorp.feen.model.search.country.CountrySearchResult;
import com.fleencorp.feen.model.security.FleenUser;
import com.fleencorp.feen.repository.calendar.CalendarRepository;
import com.fleencorp.feen.service.calendar.CalendarService;
import com.fleencorp.feen.service.common.CountryService;
import com.fleencorp.feen.service.impl.external.google.calendar.GoogleCalendarService;
import com.fleencorp.feen.service.impl.external.google.oauth2.GoogleOauth2Service;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

import static com.fleencorp.base.util.FleenUtil.handleSearchResult;
import static com.fleencorp.base.util.FleenUtil.toSearchResult;
import static com.fleencorp.feen.mapper.CalendarMapper.toCalendarResponse;
import static com.fleencorp.feen.mapper.CalendarMapper.toCalendarResponses;
import static com.fleencorp.feen.validator.impl.TimezoneValidValidator.getAvailableTimezones;
import static java.util.Objects.nonNull;

/**
 * Implementation of the CalendarService interface.
 * This class provides methods for calendar-related operations.
 *
 * @author Yusuf Alamu Musa
 * @version 1.0
 */
@Slf4j
@Service
public class CalendarServiceImpl implements CalendarService {

  private final CountryService countryService;
  private final GoogleCalendarService googleCalendarService;
  private final CalendarRepository calendarRepository;
  private final GoogleOauth2Service googleOauth2Service;
  private final LocalizedResponse localizedResponse;

  /**
  * Constructs a new CalendarServiceImpl with the specified GoogleCalendarService and CalendarRepository.
  *
  * @param googleCalendarService the service to interact with Google Calendar
  * @param countryService the service for managing and retrieving countries
  * @param calendarRepository the repository to manage calendar data
  * @param googleOauth2Service the service for interacting with Google Oauth2 service
  * @param localizedResponse the service for setting localized message to responses
  */
  public CalendarServiceImpl(
      final GoogleCalendarService googleCalendarService,
      final CountryService countryService,
      final CalendarRepository calendarRepository,
      final GoogleOauth2Service googleOauth2Service,
      final LocalizedResponse localizedResponse) {
    this.googleCalendarService = googleCalendarService;
    this.countryService = countryService;
    this.calendarRepository = calendarRepository;
    this.googleOauth2Service = googleOauth2Service;
    this.localizedResponse = localizedResponse;
  }

  /**
   * Retrieves data required for creating a calendar, including a list of countries and available timezones.
   *
   * @return a DataForCreateCalendarResponse object containing a list of countries and a set of timezones
   */
  @Override
  public DataForCreateCalendarResponse getDataForCreateCalendar() {
    // Fetch a list of countries with a large number of entries (1000 in this case).
    final CountrySearchResult searchResult = countryService.findCountries(CountrySearchRequest.of(1000));
    // Get the countries in the search result
    final List<?> countries = searchResult.getResult().getValues();
    // Get the set of available timezones.
    final Set<String> timezones = getAvailableTimezones();
    // Return the response object containing both the countries and timezones.
    return localizedResponse.of(DataForCreateCalendarResponse.of(timezones, countries));
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
    if (searchRequest.areAllDatesSet()) {
      page = calendarRepository.findByDateBetween(searchRequest.getStartDateTime(), searchRequest.getEndDateTime(), searchRequest.getPage());
    } else if (nonNull(searchRequest.getTitle()))  {
      page = calendarRepository.findByTitle(searchRequest.getTitle(), searchRequest.getPage());
    } else if (nonNull(searchRequest.getActive())) {
      page = calendarRepository.findByIsActive(searchRequest.getActive(), searchRequest.getPage());
    } else {
      page = calendarRepository.findMany(searchRequest.getPage());
    }

    final List<CalendarResponse> views = toCalendarResponses(page.getContent());
    // Return a search result view with the calendar responses and pagination details
    return handleSearchResult(
      page,
      localizedResponse.of(CalendarSearchResult.of(toSearchResult(views, page))),
      localizedResponse.of(EmptyCalendarSearchResult.of(toSearchResult(List.of(), page)))
    );
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
    return localizedResponse.of(RetrieveCalendarResponse.of(calendarId, toCalendarResponse(calendar)));
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
  public CreateCalendarResponse createCalendar(final CreateCalendarDto createCalendarDto, final FleenUser user) {
    Calendar calendar = createCalendarDto.toCalendar();

    // Check no calendar exist with matching country code or else throw an exception
    calendarRepository.findDistinctByCodeIgnoreCase(calendar.getCode())
      .ifPresent((existingCalendar) -> {
        throw CalendarAlreadyExistException.of(existingCalendar.getCode());
      });

    // Retrieve user oauth2 authorization details associated with Google Calendar
    final Oauth2Authorization oauth2Authorization = validateAccessTokenExpiryTimeOrRefreshToken(createCalendarDto.getOauth2ServiceType(), user);

    // Create a create calendar request to be used in external service
    final CreateCalendarRequest createCalendarRequest = CreateCalendarRequest
      .of(createCalendarDto.getTitle(),
          createCalendarDto.getDescription(),
          createCalendarDto.getTimezone(),
          oauth2Authorization.getAccessToken(),
          user.getEmailAddress());
    // Save new calendar
    calendar = calendarRepository.save(calendar);

    // Create a calendar using Google Calendar API Service
    final GoogleCreateCalendarResponse googleCreateCalendarResponse = googleCalendarService.createCalendar(createCalendarRequest);
    calendar.setExternalId(googleCreateCalendarResponse.getCalendarId());

    return localizedResponse.of(CreateCalendarResponse.of(calendar.getCalendarId(), toCalendarResponse(calendar)));
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
  public UpdateCalendarResponse updateCalendar(final Long calendarId, final UpdateCalendarDto updateCalendarDto, final FleenUser user) {
    Calendar calendar = calendarRepository.findById(calendarId)
            .orElseThrow(CalendarNotFoundException.of(calendarId));

    // Retrieve user oauth2 authorization details associated with Google Calendar
    // Validate access token expiry time and refresh if expired
    final Oauth2Authorization oauth2Authorization = validateAccessTokenExpiryTimeOrRefreshToken(updateCalendarDto.getOauth2ServiceType(), user);

    // Prepare request to patch calendar in external service
    final PatchCalendarRequest patchCalendarRequest = PatchCalendarRequest
      .of(calendar.getExternalId(),
          updateCalendarDto.getTitle(),
          updateCalendarDto.getDescription(),
          updateCalendarDto.getTimezone(),
          oauth2Authorization.getAccessToken());

    // Update calendar details locally
    calendar.update(
              updateCalendarDto.getTitle(),
              updateCalendarDto.getDescription(),
              updateCalendarDto.getTimezone());
    // Save updated calendar
    calendar = calendarRepository.save(calendar);

    // Update the calendar through Google Calendar API Service
    final GooglePatchCalendarResponse googlePatchCalendarResponse = googleCalendarService.patchCalendar(patchCalendarRequest);
    log.info("Patch calendar response: {}", googlePatchCalendarResponse);

    return localizedResponse.of(UpdateCalendarResponse.of(calendar.getCalendarId(), toCalendarResponse(calendar)));
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
  public ReactivateCalendarResponse reactivateCalendar(final Long calendarId, final FleenUser user) {
    Calendar calendar = calendarRepository.findById(calendarId)
      .orElseThrow(CalendarNotFoundException.of(calendarId));

    // Verify calendar is not already active
    verifyCalendarIsNotAlreadyActive(calendar);
    // Retrieve user oauth2 authorization details associated with Google Calendar
    // Validate access token expiry time and refresh if expired
    final Oauth2Authorization oauth2Authorization = validateAccessTokenExpiryTimeOrRefreshToken(Oauth2ServiceType.googleCalendar(), user);

    // Create a create calendar request to be used in external service
    final CreateCalendarRequest createCalendarRequest = CreateCalendarRequest
      .of(calendar.getTitle(),
        calendar.getDescription(),
        calendar.getTimezone(),
        oauth2Authorization.getAccessToken(),
        user.getEmailAddress());

    calendar.markAsActive();
    // Save updated calendar
    calendar = calendarRepository.save(calendar);

    // Update the calendar through Google Calendar API Service
    final GoogleCreateCalendarResponse googleCreateCalendarResponse = googleCalendarService.createCalendar(createCalendarRequest);
    calendar.setExternalId(googleCreateCalendarResponse.getCalendarId());

    return localizedResponse.of(ReactivateCalendarResponse.of(calendar.getCalendarId(), toCalendarResponse(calendar)));
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
  public DeletedCalendarResponse deleteCalendar(final Long calendarId, final FleenUser user) {
    final Calendar calendar = calendarRepository.findById(calendarId)
            .orElseThrow(CalendarNotFoundException.of(calendarId));

    // Retrieve user oauth2 authorization details associated with Google Calendar
    // Validate access token expiry time and refresh if expired
    final Oauth2Authorization oauth2Authorization = validateAccessTokenExpiryTimeOrRefreshToken(Oauth2ServiceType.googleCalendar(), user);

    // Prepare request to delete calendar from external service
    final DeleteCalendarRequest deleteCalendarRequest = DeleteCalendarRequest.of(calendar.getExternalId(), oauth2Authorization.getAccessToken());
    final GoogleDeleteCalendarResponse deleteCalendarResponse = googleCalendarService.deleteCalendar(deleteCalendarRequest);

    // Log deletion response from external service
    log.info("Deleted calendar: {}", deleteCalendarResponse);
    calendar.markAsInactive();

    calendarRepository.save(calendar);
    return localizedResponse.of(DeletedCalendarResponse.of(calendarId));
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
  public ShareCalendarWithUserResponse shareCalendarWithUser(final Long calendarId, final ShareCalendarWithUserDto shareCalendarWithUserDto, final FleenUser user) {
    final Calendar calendar = calendarRepository.findById(calendarId)
            .orElseThrow(CalendarNotFoundException.of(calendarId));

    // Retrieve user oauth2 authorization details associated with Google Calendar
    final Oauth2Authorization oauth2Authorization = validateAccessTokenExpiryTimeOrRefreshToken(Oauth2ServiceType.googleCalendar(), user);

    // Construct the request for sharing the calendar
    final ShareCalendarWithUserRequest shareCalendarWithUserRequest = ShareCalendarWithUserRequest
      .of(calendar.getExternalId(),
          shareCalendarWithUserDto.getEmailAddress(),
          shareCalendarWithUserDto.getActualAclScopeType(),
          shareCalendarWithUserDto.getActualAclRole(),
          oauth2Authorization.getAccessToken());

    // Share the calendar with the user using the Google Calendar service
    final GoogleShareCalendarWithUserResponse googleShareCalendarWithUserResponse = googleCalendarService.shareCalendarWithUser(shareCalendarWithUserRequest);
    log.info("Shared calendar: {} with user {}", googleShareCalendarWithUserResponse, shareCalendarWithUserDto.getEmailAddress());

    // Return share calendar response
    final ShareCalendarWithUserResponse shareCalendarWithUserResponse = ShareCalendarWithUserResponse
      .of(calendarId,
        shareCalendarWithUserDto.getEmailAddress(),
        toCalendarResponse(calendar));
    return localizedResponse.of(shareCalendarWithUserResponse);
  }

  /**
   * Validates the expiry time of the access token for the specified OAuth2 service type and user,
   * or refreshes the token if necessary.
   *
   * @param oauth2ServiceType the type of OAuth2 service (e.g., Google, Facebook) to validate or refresh the token for
   * @param user the user whose access token is being validated or refreshed
   * @return an {@link Oauth2Authorization} object containing updated authorization details
   */
  public Oauth2Authorization validateAccessTokenExpiryTimeOrRefreshToken(final Oauth2ServiceType oauth2ServiceType, final FleenUser user) {
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
