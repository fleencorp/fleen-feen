package com.fleencorp.feen.calendar.model.request.search;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fleencorp.base.model.request.search.SearchRequest;
import com.fleencorp.feen.calendar.constant.CalendarStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.Collections;
import java.util.List;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CalendarSearchRequest extends SearchRequest {

  @JsonProperty("title")
  private String title;

  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private CalendarStatus status;

  public boolean hasTitle() {
    return title != null && !title.isEmpty();
  }

  public boolean hasStatus() {
    return status != null;
  }

  public List<CalendarStatus> getStatuses() {
    return hasStatus()
      ? Collections.singletonList(getStatus())
      : Collections.singletonList(CalendarStatus.ACTIVE);
  }

}
