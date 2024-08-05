package com.fleencorp.feen.model.response.external.google.calendar.calendar;

import com.fleencorp.feen.model.response.external.google.calendar.calendar.base.GoogleCalendarResponse;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GoogleListCalendarResponse {

  @Builder.Default
  private List<GoogleCalendarResponse> calendars = new ArrayList<>();

  public static GoogleListCalendarResponse of(final List<GoogleCalendarResponse> calendars) {
    return GoogleListCalendarResponse.builder()
      .calendars(calendars)
      .build();
  }

  public static GoogleListCalendarResponse of() {
    return new GoogleListCalendarResponse();
  }
}
