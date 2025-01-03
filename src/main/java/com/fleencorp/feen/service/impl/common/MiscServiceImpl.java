package com.fleencorp.feen.service.impl.common;

import com.fleencorp.feen.constant.stream.StreamType;
import com.fleencorp.feen.exception.calendar.CalendarNotFoundException;
import com.fleencorp.feen.model.domain.calendar.Calendar;
import com.fleencorp.feen.model.response.security.GetEncodedPasswordResponse;
import com.fleencorp.feen.repository.calendar.CalendarRepository;
import com.fleencorp.feen.service.auth.PasswordService;
import com.fleencorp.feen.service.common.CountryService;
import com.fleencorp.feen.service.common.MiscService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static java.util.Objects.nonNull;

/**
 * Implementation of the {@link MiscService} and {@link PasswordService} interfaces.
 *
 * <p>This class provides various miscellaneous services including country management,
 * calendar operations, and password encoding. It acts as a bridge between the application
 * logic and the underlying data repositories.</p>
 *
 * @author Yusuf Alamu Musa
 * @version 1.0
 */
@Service
@Qualifier("misc")
public class MiscServiceImpl implements
    MiscService, PasswordService {

  private final CountryService countryService;
  private final CalendarRepository calendarRepository;
  private final PasswordEncoder passwordEncoder;

  /**
   * Constructs a new instance of {@link MiscServiceImpl}.
   *
   * <p>This constructor initializes the service with the required dependencies.
   * These components are essential for the operations provided by the Misc service.</p>
   *
   * @param countryService     the service for managing country-related operations.
   * @param calendarRepository the repository instance for accessing calendar data.
   * @param passwordEncoder    the service instance for encoding passwords.
   */
  public MiscServiceImpl(
      final CountryService countryService,
      final CalendarRepository calendarRepository,
      final PasswordEncoder passwordEncoder) {
    this.countryService = countryService;
    this.calendarRepository = calendarRepository;
    this.passwordEncoder = passwordEncoder;
  }

  /**
   * Generates an encoded version of the provided password.
   *
   * <p>This method takes a plaintext password, encodes it using a secure hashing algorithm,
   * and returns a response containing both the encoded password and the original password.
   * This can be useful for scenarios where you need to store the encoded password
   * securely while retaining access to the original plaintext password for validation purposes.</p>
   *
   * @param password the plaintext password to be encoded.
   * @return a {@link GetEncodedPasswordResponse} containing the encoded password and the original password.
   * @throws IllegalArgumentException if the provided password is null or empty.
   */
  @Override
  public GetEncodedPasswordResponse getEncodedPassword(final String password) {
    return GetEncodedPasswordResponse.of(createEncodedPassword(password), password);
  }

  /**
   * Retrieves a calendar based on the specified country title.
   *
   * <p>This method first fetches the country code associated with the provided
   * country title. If the country code is found, it then retrieves the calendar
   * associated with that code from the repository. If either the country code
   * or the calendar is not found, it throws a {@link CalendarNotFoundException}.</p>
   *
   * @param countryTitle The title of the country for which the calendar is to be retrieved.
   * @return The calendar associated with the specified country title.
   * @throws CalendarNotFoundException if the country code or the corresponding calendar is not found.
   */
  @Override
  public Calendar findCalendar(final String countryTitle) {
    // Retrieve the country code by its title or throw an exception if not found
    final String countryCode = countryService.getCountryCodeByTitle(countryTitle)
      .orElseThrow(CalendarNotFoundException.of(countryTitle));

    // Find the calendar by code or throw an exception if not found
    return calendarRepository.findDistinctByCodeIgnoreCase(countryCode)
      .orElseThrow(CalendarNotFoundException.of(countryCode));
  }

  /**
   * Finds a calendar associated with the specified country and stream type.
   *
   * <p>If the provided country title and stream type are non-null and the stream type is an event,
   * the method proceeds to find the calendar associated with the country. If the conditions are not met,
   * it returns null.</p>
   *
   * @param countryTitle the title of the country for which the calendar is to be found
   * @param streamType the type of stream associated with the calendar
   * @return the {@link Calendar} associated with the specified country and stream type,
   *         or null if the conditions are not satisfied
   */
  @Override
  public Calendar findCalendar(final String countryTitle, final StreamType streamType) {
    if (nonNull(countryTitle) && nonNull(streamType) && StreamType.isEvent(streamType))  {
      findCalendar(countryTitle);
    }
    return null;
  }

  /**
   * Retrieves the {@link PasswordEncoder} instance used for encoding passwords.
   *
   * <p>This method provides access to the password encoder configured for the application,
   * allowing other components to encode passwords consistently. It is useful in scenarios
   * where password encoding is required for user authentication or password storage.</p>
   *
   * @return the {@link PasswordEncoder} instance used in the application.
   */
  @Override
  public PasswordEncoder getPasswordEncoder() {
    return passwordEncoder;
  }
}
