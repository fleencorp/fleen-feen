package com.fleencorp.feen.model.event;

import lombok.*;

import java.util.Set;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AddCalendarEventAttendeesEvent {

  private String calendarId;
  private String eventId;
  private Set<String> attendeesOrGuestsEmailAddresses;

  public static AddCalendarEventAttendeesEvent of(final String calendarId, final String eventId, final Set<String> attendeesOrGuestsEmailAddresses) {
    return AddCalendarEventAttendeesEvent.builder()
            .calendarId(calendarId)
            .eventId(eventId)
            .attendeesOrGuestsEmailAddresses(attendeesOrGuestsEmailAddresses)
            .build();
  }
}
