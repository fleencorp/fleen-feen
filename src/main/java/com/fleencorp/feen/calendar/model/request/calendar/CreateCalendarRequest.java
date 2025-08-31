package com.fleencorp.feen.calendar.model.request.calendar;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateCalendarRequest extends CalendarRequest {

  private String title;
  private String description;
  private String timezone;
  private String creatorEmailAddress;

  public static CreateCalendarRequest of(
      final String title,
      final String description,
      final String timezone,
      final String accessToken,
      final String creatorEmailAddress) {
    final CreateCalendarRequest request = new CreateCalendarRequest();
    request.setTitle(title);
    request.setDescription(description);
    request.setTimezone(timezone);
    request.setCreatorEmailAddress(creatorEmailAddress);
    request.setAccessToken(accessToken);

    return request;
  }
}
