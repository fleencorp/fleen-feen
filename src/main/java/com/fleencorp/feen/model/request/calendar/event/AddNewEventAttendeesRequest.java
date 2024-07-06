package com.fleencorp.feen.model.request.calendar.event;

import lombok.*;

import java.util.Set;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AddNewEventAttendeesRequest {

  private String calendarId;
  private String eventId;
  private Set<String> attendeesOrGuestsEmailAddresses;
}
