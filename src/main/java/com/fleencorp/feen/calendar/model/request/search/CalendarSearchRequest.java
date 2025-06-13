package com.fleencorp.feen.calendar.model.request.search;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fleencorp.base.model.request.search.SearchRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CalendarSearchRequest extends SearchRequest {

  @JsonProperty("title")
  private String title;

  @JsonProperty("show_hidden")
  private Boolean showHidden;

  @JsonProperty("active")
  private Boolean active;

}
