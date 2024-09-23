package com.fleencorp.feen.model.request.calendar.event;

import com.fleencorp.feen.constant.external.google.calendar.event.EventVisibility;
import com.fleencorp.feen.constant.stream.StreamVisibility;
import com.fleencorp.feen.model.dto.event.CreateCalendarEventDto;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.fleencorp.feen.constant.external.google.calendar.event.EventMetaDataKeys.TAGS;
import static com.fleencorp.feen.model.dto.event.CreateCalendarEventDto.EventAttendeeOrGuest;
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

  public List<EventAttendeeOrGuest> getAttendeeOrGuests() {
    return nonNull(attendeeOrGuestEmailAddresses) ? attendeeOrGuestEmailAddresses : new ArrayList<>();
  }

  public static CreateCalendarEventRequest by(final CreateCalendarEventDto dto) {
    final String tags = dto.getTags();
    return CreateCalendarEventRequest.builder()
            .title(dto.getTitle())
            .description(dto.getDescription())
            .location(dto.getLocation())
            .timezone(dto.getTimezone())
            .startDateTime(dto.getActualStartDateTime())
            .endDateTime(dto.getActualEndDateTime())
            .organizerDisplayName(dto.getOrganizerAliasOrDisplayName())
            .visibility(EventVisibility.of(getVisibility(dto.getVisibility())))
            .attendeeOrGuestEmailAddresses(dto.getEventAttendeesOrGuests())
            .eventMetaData(Map.of(TAGS.getValue(), tags))
            .build();
  }

  /**
   * Updates the properties of the provided {@link CreateCalendarEventRequest} object with the specified
   * calendar details. This method sets the calendar ID or name, the creator's email, and the organizer's
   * email for the event request, allowing these details to be updated before processing the event creation.
   *
   * @param createCalendarEventRequest the request object representing the calendar event to be created.
   *                                   This object will be updated with the new calendar ID or name, creator email,
   *                                   and organizer email.
   * @param calendarIdOrName the identifier or name of the calendar where the event will be created.
   *                         This value is assigned to the calendar event request to specify the target calendar.
   * @param creatorEmail the email address of the event creator. This parameter sets the email of the user who is
   *                     creating the event, which may be used for tracking or authorization purposes.
   * @param organizerEmail the email address of the event organizer. This email is used to identify the primary
   *                       organizer of the event, potentially affecting event visibility and access permissions.
   */
  public void update(final CreateCalendarEventRequest createCalendarEventRequest,
     final String calendarIdOrName, final String creatorEmail, final String organizerEmail) {
    createCalendarEventRequest.setCalendarIdOrName(calendarIdOrName);
    createCalendarEventRequest.setCreatorEmail(creatorEmail);
    createCalendarEventRequest.setOrganizerEmail(organizerEmail);
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
    if (nonNull(visibility) &&
        (visibility.equalsIgnoreCase(StreamVisibility.PRIVATE.getValue()) ||
         visibility.equalsIgnoreCase(StreamVisibility.PROTECTED.getValue()))) {
      return StreamVisibility.PRIVATE.getValue();
    } else if (StreamVisibility.PUBLIC.getValue().equals(visibility)) {
      return visibility;
    }
    return StreamVisibility.PUBLIC.getValue();
  }
}
