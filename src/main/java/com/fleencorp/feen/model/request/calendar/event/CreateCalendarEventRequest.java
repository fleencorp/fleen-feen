package com.fleencorp.feen.model.request.calendar.event;

import com.fleencorp.feen.constant.external.google.calendar.event.EventVisibility;
import com.fleencorp.feen.constant.stream.StreamVisibility;
import com.fleencorp.feen.model.dto.event.CreateCalendarEventDto;
import lombok.*;

import java.time.LocalDateTime;
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
    return true;
  }

  public static CreateCalendarEventRequest by(final CreateCalendarEventDto dto) {
    final String tags = dto.getTags();
    return CreateCalendarEventRequest.builder()
            .title(dto.getTitle())
            .description(dto.getDescription())
            .location(dto.getLocation())
            .timezone(dto.getTimezone())
            .startDateTime(dto.getStartDateTime())
            .endDateTime(dto.getEndDateTime())
            .organizerDisplayName(dto.getOrganizerAliasOrDisplayName())
            .visibility(EventVisibility.valueOf(CreateCalendarEventRequest.getVisibility(dto.getVisibility())))
            .attendeeOrGuestEmailAddresses(dto.getEventAttendeesOrGuests())
            .eventMetaData(Map.of(TAGS.getValue(), tags))
            .build();
  }

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
