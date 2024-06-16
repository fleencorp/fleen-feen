package com.fleencorp.feen.model.request.calendar.calendar;


import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DeleteCalendarRequest {

  private String calendarId;
}
