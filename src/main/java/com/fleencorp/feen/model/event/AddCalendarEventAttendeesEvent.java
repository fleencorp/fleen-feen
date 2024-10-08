package com.fleencorp.feen.model.event;

import lombok.*;

import java.util.Set;

import static com.fleencorp.feen.model.dto.event.CreateCalendarEventDto.EventAttendeeOrGuest;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AddCalendarEventAttendeesEvent {

  private String calendarId;
  private String eventId;
  private Set<String> attendeesOrGuestsEmailAddresses;
  private Set<EventAttendeeOrGuest> attendeeOrGuests;

  public static AddCalendarEventAttendeesEvent of(final String calendarId, final String eventId, final Set<String> attendeesOrGuestsEmailAddresses,
      final Set<EventAttendeeOrGuest> attendeeOrGuests) {
    return AddCalendarEventAttendeesEvent.builder()
            .calendarId(calendarId)
            .eventId(eventId)
            .attendeesOrGuestsEmailAddresses(attendeesOrGuestsEmailAddresses)
            .attendeeOrGuests(attendeeOrGuests)
            .build();
  }
}
