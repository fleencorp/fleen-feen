package com.fleencorp.feen.calendar.model.info;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.feen.calendar.constant.CalendarStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
  "status",
  "status_text",
  "status_text_2"
})
public class CalendarStatusInfo {

  @JsonFormat(shape = STRING)
  @JsonProperty("status")
  private CalendarStatus calendarStatus;

  @JsonProperty("status_text")
  private String statusText;

  @JsonProperty("status_text_2")
  private String statusText2;

  public static CalendarStatusInfo of(final CalendarStatus status, final String statusText, final String statusText2) {
    return new CalendarStatusInfo(status, statusText, statusText2);
  }
}
