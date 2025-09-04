package com.fleencorp.feen.shared.stream.contract;

import com.fleencorp.feen.stream.constant.attendee.StreamAttendeeRequestToJoinStatus;
import com.fleencorp.feen.stream.model.domain.StreamAttendee;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;

public interface IsAttendee {

  Long getAttendeeId();

  Long getStreamId();

  Long getMemberId();

  Boolean getAttending();

  Boolean getASpeaker();

  Boolean getIsOrganizer();

  String getAttendeeComment();

  String getOrganizerComment();

  StreamAttendeeRequestToJoinStatus getRequestToJoinStatus();

  String getEmailAddress();

  String getFullName();

  String getUsername();

  String getProfilePhoto();

  String getFirstName();

  String getLastName();

  boolean isRequestToJoinDisapprovedOrPending();

  default boolean isAttending() {
    return nonNull(getAttending()) && getAttending();
  }

  default boolean isOrganizer() {
    return nonNull(getIsOrganizer()) && getIsOrganizer();
  }

  default boolean isASpeaker() {
    return nonNull(getASpeaker()) && getASpeaker();
  }

  default boolean isRequestToJoinPending() {
    return StreamAttendeeRequestToJoinStatus.isPending(getRequestToJoinStatus());
  }

  default boolean isRequestToJoinDisapproved() {
    return StreamAttendeeRequestToJoinStatus.isDisapproved(getRequestToJoinStatus());
  }

  void approveUserAttendance();

  /**
   * Retrieves email addresses of attendees from a list of StreamAttendee objects.
   *
   * <p>This method filters out null {@link StreamAttendee} objects, retrieves the email addresses of the
   * associated attendees or members, and collects them into a set of strings.</p>
   *
   * @param streamAttendees the list of StreamAttendee objects
   * @return a set of email addresses of attendees, or an empty set if streamAttendees is null
   */
  static Set<String> getAttendeesEmailAddresses(final Collection<IsAttendee> streamAttendees) {
    if (nonNull(streamAttendees)) {
      return streamAttendees.stream()
        .filter(Objects::nonNull)
        .map(IsAttendee::getEmailAddress)
        .collect(Collectors.toSet());
    }

    return Collections.emptySet();
  }

  /**
   * Retrieves the set of attendee IDs from a list of StreamAttendee objects.
   *
   * @param attendees List of StreamAttendees from which the IDs will be extracted.
   * @return A set of attendee IDs.
   */
  static Set<Long> getAttendeeIds(final Collection<IsAttendee> attendees) {
    // Stream over the list of StreamAttendees
    if (nonNull(attendees)) {
      // Map each StreamAttendee to its StreamAttendeeId and collect the IDs into a set
      return attendees.stream()
        .map(IsAttendee::getAttendeeId)
        .collect(Collectors.toSet());
    }
    return Set.of();
  }

  /**
   * Finds an attendee by their attendee ID from a set of attendees.
   *
   * <p>This method searches the provided {@code attendees} set for an attendee whose attendee ID matches the given {@code attendeeId}.
   * If a matching attendee is found, they are returned. If no match is found, {@code null} is returned.</p>
   *
   * @param attendeeId the ID of the attendee to be found
   * @param attendees  the set of attendees to search through
   * @return the attendee whose attendee ID matches the given {@code attendeeId}, or {@code null} if no match is found
   */
  static IsAttendee findAttendeeById(final Long attendeeId, final Set<IsAttendee> attendees) {
    return attendees.stream()
      .filter(Objects::nonNull)
      .filter(attendee -> attendee.getAttendeeId().equals(attendeeId))
      .findFirst()
      .orElse(null);
  }


  /**
   * Retrieves the set of attendees who are either in a DISAPPROVED or PENDING status
   * from the provided set of {@link IsAttendee} objects.
   *
   * @param attendees the set of {@link IsAttendee} objects to filter.
   * @return a set of {@link IsAttendee} objects where the status is either DISAPPROVED or PENDING.
   */
  static Set<IsAttendee> getDisapprovedOrPendingAttendees(final Set<StreamAttendee> attendees) {
    // Retrieve attendees with DISAPPROVED or PENDING statuses for the given stream ID
    if (nonNull(attendees)) {
      return attendees.stream()
        .filter(Objects::nonNull)
        .filter(IsAttendee::isRequestToJoinDisapprovedOrPending)
        .collect(Collectors.toSet());
    }

    return Collections.emptySet();
  }

  /**
   * Retrieves the IDs of attendees with disapproved or pending statuses.
   *
   * @param attendees the set of attendees to filter
   * @return a set of attendee attendee IDs with disapproved or pending statuses
   */
  static Set<Long> getDisapprovedOrPendingAttendeeIds(final Collection<IsAttendee> attendees) {
    // Filter attendees with DISAPPROVED or PENDING status and collect their attendee IDs
    if (nonNull(attendees)) {
      return attendees.stream()
        .filter(Objects::nonNull)
        .filter(IsAttendee::isRequestToJoinDisapprovedOrPending)
        .map(IsAttendee::getAttendeeId)
        .filter(Objects::nonNull)
        .collect(Collectors.toSet());
    }

    return Collections.emptySet();
  }

}

