package com.fleencorp.feen.model.response.calendar;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.feen.model.response.base.ApiResponse;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
  "message",
  "calendar_id"
})
public class DeletedCalendarResponse extends ApiResponse {

  @JsonProperty("calendar_id")
  private Long calendarId;

  @Override
  public String getMessageKey() {
    return "deleted.calendar";
  }

  public static DeletedCalendarResponse of(Long calendarId) {
    return DeletedCalendarResponse.builder()
      .calendarId(calendarId)
      .build();
  }
}
