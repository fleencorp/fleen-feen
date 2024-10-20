package com.fleencorp.feen.model.response.external.google.calendar.event;

import com.fleencorp.feen.model.response.external.google.calendar.event.base.GoogleCalendarEventResponse;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GoogleCreateCalendarEventResponse {

  private String eventId;
  private String eventLinkOrUri;
  private GoogleCalendarEventResponse event;

  public static GoogleCreateCalendarEventResponse of(final String eventId, final GoogleCalendarEventResponse event) {
    return GoogleCreateCalendarEventResponse.builder()
      .eventId(eventId)
      .eventLinkOrUri(event.getHangoutLink())
      .event(event)
      .build();
  }
}
