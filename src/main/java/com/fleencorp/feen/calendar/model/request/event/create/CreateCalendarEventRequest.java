package com.fleencorp.feen.calendar.model.request.event.create;

import com.fleencorp.feen.common.constant.external.google.calendar.event.EventVisibility;
import com.fleencorp.feen.stream.constant.core.StreamVisibility;
import com.fleencorp.feen.stream.model.dto.core.CreateStreamDto;
import com.fleencorp.feen.stream.model.dto.event.CreateEventDto;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.fleencorp.feen.common.constant.external.google.calendar.event.EventMetaDataKeys.TAGS;
import static com.fleencorp.feen.stream.model.dto.event.CreateEventDto.EventAttendeeOrGuest;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateCalendarEventRequest {

  private String title;
  private String description;
  private String location;
  private String timezone;
  private LocalDateTime startDateTime;
  private LocalDateTime endDateTime;
  private String creatorEmail;
  private String creatorDisplayName;
  private String organizerEmail;
  private String organizerDisplayName;
  private EventVisibility visibility;
  private List<EventAttendeeOrGuest> attendeeOrGuestEmailAddresses;
  private String calendarIdOrName;
  private String eventId;
  private String eventLinkOrUri;

  @Builder.Default
  private Map<String, String> eventMetaData = new HashMap<>();

  public String getLocation() {
    return (null != location) ? location : getDefaultLocation();
  }

  public String getDefaultLocation() {
    return "Remote or Global";
  }

  public boolean getCanGuestsInviteOtherGuests() {
    return false;
  }

  public boolean getCanGuestsCanSeeOtherGuests() {
    return false;
  }

  public String getVisibility() {
    return nonNull(visibility) ? visibility.getValue() : null;
  }

  public List<EventAttendeeOrGuest> getAttendeeOrGuests() {
    return nonNull(attendeeOrGuestEmailAddresses) ? attendeeOrGuestEmailAddresses : new ArrayList<>();
  }

  public static CreateCalendarEventRequest by(final CreateEventDto dto) {
    final CreateCalendarEventRequest createCalendarEventRequest = bySuper(dto);
    createCalendarEventRequest.setAttendeeOrGuestEmailAddresses(dto.getEventAttendeesOrGuests());
    return createCalendarEventRequest;
  }

  public static CreateCalendarEventRequest bySuper(final CreateStreamDto dto) {
    final String tags = dto.getTags();

    final CreateCalendarEventRequest createCalendarEventRequest = new CreateCalendarEventRequest();
    createCalendarEventRequest.setTitle(dto.getTitle());
    createCalendarEventRequest.setDescription(dto.getDescription());
    createCalendarEventRequest.setLocation(dto.getLocation());
    createCalendarEventRequest.setTimezone(dto.getTimezone());
    createCalendarEventRequest.setStartDateTime(dto.getStartDateTime());
    createCalendarEventRequest.setEndDateTime(dto.getEndDateTime());
    createCalendarEventRequest.setOrganizerDisplayName(dto.getOrganizerAliasOrDisplayName());
    createCalendarEventRequest.setVisibility(EventVisibility.of(getVisibility(dto.getVisibility())));
    if (nonNull(tags)) {
      createCalendarEventRequest.addMeta(TAGS.getValue(), tags);
    }

    return createCalendarEventRequest;
  }

  /**
   * Adds an attendee or guest to the list of email addresses.
   * Initializes the list if it is null.
   */
  public void addAttendeeOrGuest(final EventAttendeeOrGuest eventAttendeeOrGuest) {
    // Check if the list of attendee or guest email addresses is null
    if (isNull(attendeeOrGuestEmailAddresses)) {
      // Initialize the list if it is null
      this.attendeeOrGuestEmailAddresses = new ArrayList<>();
    }

    // Add the provided eventAttendeeOrGuest to the list of email addresses
    attendeeOrGuestEmailAddresses.add(eventAttendeeOrGuest);
  }

  /**
   * Updates the properties of the provided {@link CreateCalendarEventRequest} object with the specified
   * calendar details. This method sets the calendar ID or name, the creator's email, and the organizer's
   * email for the event request, allowing these details to be updated before processing the event creation.
   *
   * @param calendarIdOrName the identifier or name of the calendar where the event will be created.
   *                         This value is assigned to the calendar event request to specify the target calendar.
   * @param creatorEmail the email address of the event creator. This parameter sets the email of the user who is
   *                     creating the event, which may be used for tracking or authorization purposes.
   * @param organizerEmail the email address of the event organizer. This email is used to identify the primary
   *                       organizer of the event, potentially affecting event visibility and access permissions.
   */
  public void update(final String calendarIdOrName, final String creatorEmail, final String organizerEmail) {
    this.calendarIdOrName = calendarIdOrName;
    this.creatorEmail = creatorEmail;
    this.organizerEmail = organizerEmail;
  }

  /**
   * Updates the calendar event with the provided identifiers and metadata.
   *
   * <p>This method sets the calendar ID or name, creator email, and organizer email,
   * and merges the given metadata into the existing {@code eventMetaData} map.</p>
   *
   * @param calendarIdOrName The calendar ID or display name associated with the event.
   * @param creatorEmail The email address of the event's creator.
   * @param organizerEmail The email address of the event's organizer.
   * @param metadata A map containing additional metadata to associate with the event.
   */
  public void update(final String calendarIdOrName, final String creatorEmail, final String organizerEmail, final Map<String, String> metadata) {
    this.calendarIdOrName = calendarIdOrName;
    this.creatorEmail = creatorEmail;
    this.organizerEmail = organizerEmail;
    eventMetaData.putAll(metadata);
  }

  /**
   * Adds a single metadata entry to the event.
   *
   * <p>If the key already exists, its value will be replaced with the new one.</p>
   *
   * @param key The metadata key.
   * @param value The metadata value associated with the key.
   */
  public void addMeta(final String key, final String value) {
    eventMetaData.put(key, value);
  }

  /**
   * Updates the details of an event by setting the event ID and its associated link or URI.
   *
   * @param eventId the unique identifier of the event
   * @param eventLinkOrUri the link or URI related to the event
   */
  public void update(final String eventId, final String eventLinkOrUri) {
    this.eventId = eventId;
    this.eventLinkOrUri = eventLinkOrUri;
  }

  /**
   * Converts a visibility value from application-specific StreamVisibility to YouTubeLiveBroadcastVisibility format.

   * <p>This method is useful for mapping visibility settings between different systems, ensuring consistent handling
   * of privacy settings across platforms.</p>
   *
   * @param visibility the visibility value to convert
   * @return the corresponding YouTube visibility value
   */
  public static String getVisibility(final String visibility) {
    if (StreamVisibility.isPrivateOrProtected(visibility)) {
      return StreamVisibility.PRIVATE.getValue();
    } else if (StreamVisibility.isPublic(visibility)) {
      return visibility;
    }
    return StreamVisibility.PUBLIC.getValue();
  }
}
