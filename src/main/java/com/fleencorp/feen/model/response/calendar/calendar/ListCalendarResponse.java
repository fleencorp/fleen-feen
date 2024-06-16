package com.fleencorp.feen.model.response.calendar.calendar;

import com.fleencorp.feen.model.response.calendar.calendar.base.GoogleCalendarResponse;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ListCalendarResponse {

  @Builder.Default
  private List<GoogleCalendarResponse> calendars = new ArrayList<>();
}
