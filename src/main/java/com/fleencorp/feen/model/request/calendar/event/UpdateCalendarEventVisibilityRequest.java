package com.fleencorp.feen.model.request.calendar.event;

import com.fleencorp.feen.constant.external.google.calendar.event.EventVisibility;
import lombok.*;

import static java.util.Objects.nonNull;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCalendarEventVisibilityRequest {

  private String calendarId;
  private String eventId;
  private EventVisibility visibility;

  public String getVisibility() {
    return nonNull(visibility) ? visibility.getValue() : null;
  }

  public static UpdateCalendarEventVisibilityRequest of(final String calendarId, final String eventId, final String visibility) {
    return UpdateCalendarEventVisibilityRequest.builder()
            .calendarId(calendarId)
            .eventId(eventId)
            .visibility(EventVisibility.valueOf(visibility))
            .build();
  }
}
