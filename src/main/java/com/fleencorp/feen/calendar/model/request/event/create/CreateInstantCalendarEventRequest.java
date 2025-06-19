package com.fleencorp.feen.calendar.model.request.event.create;

import com.fleencorp.feen.model.dto.event.CreateInstantEventDto;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateInstantCalendarEventRequest {

  private String calendarId;
  private String title;
  private Boolean sendNotifications;

  public static CreateInstantCalendarEventRequest by(final CreateInstantEventDto createInstantEventDto) {
    return new CreateInstantCalendarEventRequest(null, createInstantEventDto.getTitle(), true);
  }

  public void update(final String calendarId) {
    this.calendarId = calendarId;
  }
}
