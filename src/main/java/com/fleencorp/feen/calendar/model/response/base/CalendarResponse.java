package com.fleencorp.feen.calendar.model.response.base;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.feen.calendar.model.info.CalendarStatusInfo;
import com.fleencorp.feen.common.model.response.core.FleenFeenResponse;
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
  "id",
  "title",
  "timezone",
  "code",
  "status_info",
  "description",
  "created_on",
  "updated_on"
})
public class CalendarResponse extends FleenFeenResponse {

  @JsonProperty("title")
  private String title;

  @JsonProperty("description")
  private String description;

  @JsonProperty("timezone")
  private String timezone;

  @JsonProperty("code")
  private String code;

  @JsonProperty("status_info")
  private CalendarStatusInfo statusInfo;
}
