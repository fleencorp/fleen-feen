package com.fleencorp.feen.model.response.calendar;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.feen.model.response.calendar.base.CalendarResponse;
import com.fleencorp.localizer.model.response.ApiResponse;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
  "message",
  "calendar_id",
  "calendar"
})
public class RetrieveCalendarResponse extends ApiResponse {

  @JsonProperty("calendar_id")
  private Long calendarId;

  @JsonProperty("calendar")
  private CalendarResponse calendar;

  @Override
  public String getMessageCode() {
    return "retrieve.calendar";
  }

  public static RetrieveCalendarResponse of(final Long calendarId, final CalendarResponse calendar) {
    return RetrieveCalendarResponse.builder()
            .calendarId(calendarId)
            .calendar(calendar)
            .build();
  }
}
