package com.fleencorp.feen.service.impl.common;

import com.fleencorp.feen.constant.stream.StreamType;
import com.fleencorp.feen.calendar.exception.CalendarNotFoundException;
import com.fleencorp.feen.model.contract.HasId;
import com.fleencorp.feen.model.contract.HasOrganizer;
import com.fleencorp.feen.model.contract.Updatable;
import com.fleencorp.feen.calendar.model.domain.Calendar;
import com.fleencorp.feen.model.response.security.GetEncodedPasswordResponse;
import com.fleencorp.feen.calendar.repository.CalendarRepository;
import com.fleencorp.feen.service.auth.PasswordService;
import com.fleencorp.feen.country.service.CountryService;
import com.fleencorp.feen.service.common.MiscService;
import com.fleencorp.feen.user.model.domain.Member;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

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
@Slf4j
@Service
@Qualifier("misc")
public class MiscServiceImpl implements MiscService, PasswordService {

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
  public Calendar findCalendarByStreamType(final String countryTitle, final StreamType streamType) {
    if (nonNull(countryTitle) && nonNull(streamType) && StreamType.isEvent(streamType))  {
      return findCalendar(countryTitle);
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

  /**
   * Determines whether the given user is the organizer of each {@code SetIsOrganizer} entry in the provided list.
   * If the user is the organizer, the {@code isOrganizer} field of the {@code SetIsOrganizer} entry is set accordingly.
   *
   * <p>This method ensures that the list of entries, the user, and the user's ID are not null before proceeding.
   * It then iterates over the list, checking whether the {@code organizerId} of each entry matches the given user's ID
   * and updates the {@code isOrganizer} field accordingly.</p>
   *
   * @param entries the list of {@code SetIsOrganizer} objects to process
   * @param member    the user whose organizer status is to be determined
   */
  public static void determineIfUserIsTheOrganizerOfEntity(final Collection<? extends HasOrganizer> entries, final Member member) {
    if (nonNull(entries) && !entries.isEmpty() && nonNull(member) && nonNull(member.getMemberId())) {
      entries.stream()
        .filter(Objects::nonNull)
        .forEach(entry -> determineIfUserIsTheOrganizerOfEntity(entry, member));
    }
  }

  /**
   * Determines if the provided user is the organizer of the given entity.
   *
   * <p>This method checks if the {@code setIsOrganizer} and {@code user} are non-null and if the user has a valid ID.
   * It then compares the user's ID with the organizer's ID from {@code setIsOrganizer} and sets the {@code isOrganizer}
   * flag accordingly.
   *
   * @param entry the object representing the entity whose organizer is being checked
   * @param member the user whose role is being verified as the organizer of the entity
   */
  public static void determineIfUserIsTheOrganizerOfEntity(final HasOrganizer entry, final Member member) {
    if (nonNull(entry) && nonNull(member) && nonNull(member.getMemberId())) {
      final boolean isOrganizer = entry.getOrganizerId().equals(member.getMemberId());
      entry.setIsOrganizer(isOrganizer);
    }
  }

  /**
   * <p>Marks a single entity as updatable if the provided user is the organizer of the entity.</p>
   *
   * <p>This method checks whether the given entry and user ID are non-null.
   * If the user's ID matches the entity's organizer ID, the entity is marked as updatable.</p>
   *
   * @param entry  the entity to check and potentially mark as updatable
   * @param userId the ID of the user to verify as the organizer
   */
  public static void setEntityUpdatableByUser(final Updatable entry, final Long userId) {
    if (nonNull(entry) && nonNull(userId)) {
      final boolean isAuthor = entry.getAuthorId().equals(userId) || entry.getOrganizerId().equals(userId);
      if (isAuthor) {
        entry.markAsUpdatable();
      }
    }
  }

  /**
   * Extracts and returns the numeric IDs from a collection of objects that implement {@link HasId}.
   *
   * <p>Returns an empty list if the input collection is null or empty. Null elements within
   * the collection are safely ignored during processing.</p>
   *
   * @param entries the collection of ID-holding objects
   * @return a list of numeric IDs, or an empty list if input is null or empty
   */
  public static <T extends HasId> List<Long> extractAndGetEntriesIds(final Collection<T> entries) {
    // Filter null responses and map to the corresponding stream ID
    if (nonNull(entries) && !entries.isEmpty()) {
      return entries.stream()
        .filter(Objects::nonNull)
        .map(HasId::getNumberId)
        .toList();
    }
    // Return an empty list if the input list is null
    return List.of();
  }

  /**
   * Groups a collection of items implementing {@link HasId} by their numeric ID.
   *
   * <p>This is typically used to map stream IDs to their corresponding attendance
   * or membership objects. Null entries are filtered out. Returns an empty map
   * if the input collection is null or empty.</p>
   *
   * @param entries the collection of items to group
   * @param <T> a type that implements {@link HasId}
   * @return a map of numeric IDs to their corresponding items
   */
  public static <T extends HasId> Map<Long, T> groupMembershipByEntriesId(final Collection<T> entries) {
    // Filter null values and map stream ID to request-to-join status
    if (nonNull(entries) && !entries.isEmpty()) {
      return entries.stream()
        .filter(Objects::nonNull)
        .collect(Collectors.toMap(HasId::getNumberId, Function.identity()));
    }
    // Return an empty map if the input list is null or empty
    return Map.of();
  }

}
