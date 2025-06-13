package com.fleencorp.feen.calendar.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.feen.calendar.model.response.base.CalendarResponse;
import com.fleencorp.localizer.model.response.LocalizedResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
  "message",
  "calendar_id",
  "user_email_address",
  "calendar"
})
public class ShareCalendarWithUserResponse extends LocalizedResponse {

  @JsonProperty("calendar_id")
  private Long calendarId;

  @JsonProperty("user_email_address")
  private String userEmailAddress;

  @JsonProperty("calendar")
  private CalendarResponse calendar;

  @Override
  public String getMessageCode() {
    return "share.calendar.with.user";
  }

  public static ShareCalendarWithUserResponse of(final Long calendarId, final String userEmailAddress, final CalendarResponse calendar) {
    return new ShareCalendarWithUserResponse(calendarId, userEmailAddress, calendar);
  }
}
