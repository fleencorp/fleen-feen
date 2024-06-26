package com.fleencorp.feen.model.request.calendar.event;

import com.fleencorp.feen.constant.external.google.calendar.event.EventVisibility;
import static com.fleencorp.feen.model.dto.event.CreateCalendarEventDto.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    return location != null ? location : getDefaultLocation();
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
}
