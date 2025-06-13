package com.fleencorp.feen.calendar.model.request.event.create;

import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.fleencorp.feen.model.dto.event.CreateEventDto.EventAttendeeOrGuest;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AddNewEventAttendeesRequest {

  private String calendarId;
  private String eventId;
  private Set<String> attendeesOrGuestsEmailAddresses;
  private List<EventAttendeeOrGuest> attendeeOrGuests;

  public static AddNewEventAttendeesRequest of(final String calendarId, final String eventId, final Set<String> attendeesOrGuestsEmailAddresses,
      final Set<EventAttendeeOrGuest> attendeeOrGuests) {
    return AddNewEventAttendeesRequest.builder()
      .calendarId(calendarId)
      .eventId(eventId)
      .attendeesOrGuestsEmailAddresses(attendeesOrGuestsEmailAddresses)
      .attendeeOrGuests(new ArrayList<>(attendeeOrGuests))
      .build();
  }
}
