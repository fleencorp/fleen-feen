package com.fleencorp.feen.calendar.model.event;

import lombok.*;

import java.util.Set;

import static com.fleencorp.feen.stream.model.dto.event.CreateEventDto.EventAttendeeOrGuest;

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
