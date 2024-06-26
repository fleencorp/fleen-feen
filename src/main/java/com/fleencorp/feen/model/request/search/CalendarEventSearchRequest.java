package com.fleencorp.feen.model.request.search;

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
public class CalendarEventSearchRequest extends SearchRequest {

  @JsonProperty("timezone")
  private String timezone;

  @JsonProperty("single_events")
  private Boolean singleEvents;

  @JsonProperty("show_deleted")
  private Boolean showDeleted;

  @JsonProperty("order_by")
  private String orderBy;

}
