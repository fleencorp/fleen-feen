package com.fleencorp.feen.model.request.calendar.event;

import com.fleencorp.feen.constant.calendar.EventVisibility;
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
  private List<String> attendeeOrGuestEmailAddresses;
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
