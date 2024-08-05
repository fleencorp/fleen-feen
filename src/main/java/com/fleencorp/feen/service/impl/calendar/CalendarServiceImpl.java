package com.fleencorp.feen.service.impl.calendar;

import com.fleencorp.base.model.view.search.SearchResultView;
import com.fleencorp.feen.exception.calendar.CalendarAlreadyExistException;
import com.fleencorp.feen.exception.calendar.CalendarNotFoundException;
import com.fleencorp.feen.model.domain.calendar.Calendar;
import com.fleencorp.feen.model.domain.other.Country;
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
import com.fleencorp.feen.model.response.other.DeleteResponse;
import com.fleencorp.feen.repository.calendar.CalendarRepository;
import com.fleencorp.feen.service.calendar.CalendarService;
import com.fleencorp.feen.service.common.CountryService;
import com.fleencorp.feen.service.external.google.calendar.GoogleCalendarService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.fleencorp.base.util.FleenUtil.areNotEmpty;
import static com.fleencorp.base.util.FleenUtil.toSearchResult;
import static com.fleencorp.feen.mapper.CalendarMapper.toCalendarResponse;
import static com.fleencorp.feen.mapper.CalendarMapper.toCalendarResponses;
import static com.fleencorp.feen.validator.impl.TimezoneValidValidator.getAvailableTimezones;
import static java.lang.Long.parseLong;
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

  /**
  * Constructs a new CalendarServiceImpl with the specified GoogleCalendarService and CalendarRepository.
  *
  * @param googleCalendarService the service to interact with Google Calendar
  * @param countryService the service for managing and retrieving countries
  * @param calendarRepository the repository to manage calendar data
  */
  public CalendarServiceImpl(
      final GoogleCalendarService googleCalendarService,
      final CountryService countryService,
      final CalendarRepository calendarRepository) {
    this.googleCalendarService = googleCalendarService;
    this.countryService = countryService;
    this.calendarRepository = calendarRepository;
  }

  /**
   * Retrieves data required for creating a calendar, including a list of countries and available timezones.
   *
   * @return a DataForCreateCalendarResponse object containing a list of countries and a set of timezones
   */
  @Override
  public DataForCreateCalendarResponse getDataForCreateCalendar() {
    // Fetch a list of countries with a large number of entries (1000 in this case).
    SearchResultView searchResult = countryService.findCountries(CountrySearchRequest.of(1000));
    // Get the countries in the search result
    List<?> countries = searchResult.getValues();
    // Get the set of available timezones.
    Set<String> timezones = getAvailableTimezones();
    // Return the response object containing both the countries and timezones.
    return DataForCreateCalendarResponse.of(timezones, countries);
  }

  /**
  * Finds calendars based on the search criteria provided in the CalendarSearchRequest.
  *
  * @param searchRequest the request containing the search criteria
  * @return a SearchResultView containing the search results
  */
  @Override
  public SearchResultView findCalendars(final CalendarSearchRequest searchRequest) {
    final Page<Calendar> page;
    if (areNotEmpty(searchRequest.getStartDate(), searchRequest.getEndDate())) {
      page = calendarRepository.findByDateBetween(searchRequest.getStartDate().atStartOfDay(), searchRequest.getEndDate().atStartOfDay(), searchRequest.getPage());
    } else if (nonNull(searchRequest.getTitle()))  {
      page = calendarRepository.findByTitle(searchRequest.getTitle(), searchRequest.getPage());
    } else {
      page = calendarRepository.findMany(searchRequest.getPage());
    }

    final List<CalendarResponse> views = toCalendarResponses(page.getContent());
    return toSearchResult(views, page);
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
            .orElseThrow(() -> new CalendarNotFoundException(calendarId));
    return RetrieveCalendarResponse.of(calendarId, toCalendarResponse(calendar));
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
  public CreateCalendarResponse createCalendar(final CreateCalendarDto createCalendarDto) {
    Calendar calendar = createCalendarDto.toCalendar();

    // Retrieve the country and use the country's code as the calendar code
    final Country country = countryService.getCountry(parseLong(createCalendarDto.getCountry()));
    calendar.setCode(country.getCode());

    // Check no calendar exist with matching country code or else throw an exception
    Optional<Calendar> existingCalendar = calendarRepository.findDistinctByCodeIgnoreCase(country.getCode());
    if (existingCalendar.isPresent()) {
      throw new CalendarAlreadyExistException(country.getCode());
    }

    final CreateCalendarRequest createCalendarRequest = CreateCalendarRequest
      .of(createCalendarDto.getTitle(),
          createCalendarDto.getDescription(),
          createCalendarDto.getTimezone());

    // Create a calendar using Google Calendar API Service
    final GoogleCreateCalendarResponse googleCreateCalendarResponse = googleCalendarService.createCalendar(createCalendarRequest);
    calendar.setExternalId(googleCreateCalendarResponse.getCalendarId());
    log.info("Created calendar: {}", googleCreateCalendarResponse);

    calendar = calendarRepository.save(calendar);
    return CreateCalendarResponse.of(calendar.getCalendarId(), toCalendarResponse(calendar));
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
  public UpdateCalendarResponse updateCalendar(final Long calendarId, final UpdateCalendarDto updateCalendarDto) {
    Calendar calendar = calendarRepository.findById(calendarId)
            .orElseThrow(() -> new CalendarNotFoundException(calendarId));

    // Prepare request to patch calendar in external service
    final PatchCalendarRequest patchCalendarRequest = PatchCalendarRequest
      .of(calendar.getExternalId(),
          updateCalendarDto.getTitle(),
          updateCalendarDto.getDescription(),
          updateCalendarDto.getTimezone());

    // Update the calendar through Google Calendar API Service
    final GooglePatchCalendarResponse googlePatchCalendarResponse = googleCalendarService.patchCalendar(patchCalendarRequest);
    log.info("Updated calendar: {}", googlePatchCalendarResponse);

    // Update calendar details locally
    calendar.update(
              updateCalendarDto.getTitle(),
              updateCalendarDto.getDescription(),
              updateCalendarDto.getTimezone());
    calendar = calendarRepository.save(calendar);

    return UpdateCalendarResponse.of(calendar.getCalendarId(), toCalendarResponse(calendar));
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
  public DeleteResponse deleteCalendar(final Long calendarId) {
    final Calendar calendar = calendarRepository.findById(calendarId)
            .orElseThrow(() -> new CalendarNotFoundException(calendarId));

    // Prepare request to delete calendar from external service
    final DeleteCalendarRequest deleteCalendarRequest = new DeleteCalendarRequest(calendar.getExternalId());
    final GoogleDeleteCalendarResponse deleteCalendarResponse = googleCalendarService.deleteCalendar(deleteCalendarRequest);

    // Log deletion response from external service
    log.info("Deleted calendar: {}", deleteCalendarResponse);
    calendar.setIsActive(false);

    calendarRepository.save(calendar);
    return DeleteResponse.of(calendarId);
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
  public ShareCalendarWithUserResponse shareCalendarWithUser(final Long calendarId, final ShareCalendarWithUserDto shareCalendarWithUserDto) {
    final Calendar calendar = calendarRepository.findById(calendarId)
            .orElseThrow(() -> new CalendarNotFoundException(calendarId));

    // Construct the request for sharing the calendar
    final ShareCalendarWithUserRequest shareCalendarWithUserRequest = ShareCalendarWithUserRequest
      .of(calendar.getExternalId(),
          shareCalendarWithUserDto.getEmailAddress(),
          shareCalendarWithUserDto.getActualAclScopeType(),
          shareCalendarWithUserDto.getActualAclRole());

    // Share the calendar with the user using the Google Calendar service
    final GoogleShareCalendarWithUserResponse googleShareCalendarWithUserResponse = googleCalendarService.shareCalendarWithUser(shareCalendarWithUserRequest);
    log.info("Shared calendar: {} with user {}", googleShareCalendarWithUserResponse, shareCalendarWithUserDto.getEmailAddress());

    return ShareCalendarWithUserResponse
            .of(calendarId,
                shareCalendarWithUserDto.getEmailAddress(),
                toCalendarResponse(calendar));
  }
}
