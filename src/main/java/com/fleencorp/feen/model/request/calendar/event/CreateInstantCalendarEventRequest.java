package com.fleencorp.feen.model.request.calendar.event;

import com.fleencorp.feen.model.dto.event.CreateInstantCalendarEventDto;
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

  public static CreateInstantCalendarEventRequest by(final CreateInstantCalendarEventDto createInstantCalendarEventDto) {
    return CreateInstantCalendarEventRequest.builder()
            .title(createInstantCalendarEventDto.getTitle())
            .sendNotifications(true)
            .build();
  }

  public void update(final String calendarId) {
    this.calendarId = calendarId;
  }
}
