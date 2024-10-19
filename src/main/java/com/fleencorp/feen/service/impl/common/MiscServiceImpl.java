package com.fleencorp.feen.service.impl.common;

import com.fleencorp.feen.exception.base.FailedOperationException;
import com.fleencorp.feen.exception.calendar.CalendarNotFoundException;
import com.fleencorp.feen.model.domain.calendar.Calendar;
import com.fleencorp.feen.model.domain.user.Member;
import com.fleencorp.feen.model.response.security.GetEncodedPasswordResponse;
import com.fleencorp.feen.model.security.FleenUser;
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
      .orElseThrow(() -> new CalendarNotFoundException(countryTitle));

    // Find the calendar by code or throw an exception if not found
    return calendarRepository.findDistinctByCodeIgnoreCase(countryCode)
      .orElseThrow(() -> new CalendarNotFoundException(countryCode));
  }

  /**
   * Verifies if the provided user is the owner of a chat space, an event or stream and is attempting to perform a restricted action.
   *
   * <p>If the provided {@code owner}'s ID matches the {@code user}'s ID, this method throws
   * a {@link FailedOperationException}, indicating that the owner is trying to perform an action they are not allowed to.</p>
   *
   * <p>This method ensures that certain actions cannot be performed by the chat space or event or stream owner, enforcing
   * restrictions based on ownership.</p>
   *
   * @param owner the {@link Member} representing the owner of a chat space, an event or stream
   * @param user the {@link FleenUser} attempting to perform the action
   * @throws FailedOperationException if the user is the owner of a chat space, event or stream and tries to perform a restricted action
   */
  @Override
  public void verifyIfUserIsAuthorOrCreatorOrOwnerTryingToPerformAction(final Member owner, final FleenUser user) {
    // Check if both the owner and user are provided
    if (nonNull(owner) && nonNull(user)) {
      final Long ownerUserId = owner.getMemberId();
      // Get the user's ID
      final Long userId = user.getId();
      // If the user is the owner, throw an exception
      if (ownerUserId.equals(userId)) {
        throw new FailedOperationException();
      }
    }
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
