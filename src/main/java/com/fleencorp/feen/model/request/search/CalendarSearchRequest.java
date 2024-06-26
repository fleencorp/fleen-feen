package com.fleencorp.feen.model.request.search;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fleencorp.base.model.request.search.SearchRequest;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CalendarSearchRequest extends SearchRequest {

  @JsonProperty("show_hidden")
  private Boolean showHidden;

  @JsonProperty("show_deleted")
  private Boolean showDeleted;
}
