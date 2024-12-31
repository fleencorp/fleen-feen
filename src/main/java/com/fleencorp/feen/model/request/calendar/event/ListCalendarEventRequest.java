package com.fleencorp.feen.model.request.calendar.event;

import com.fleencorp.feen.constant.external.google.calendar.event.EventOrderBy;
import lombok.*;

import java.time.LocalDateTime;

import static java.util.Objects.nonNull;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ListCalendarEventRequest {

  private String calendarId;
  private Integer maxResultOrLimit;
  private String q;
  private String timezone;
  private String pageToken;
  private Boolean singleEvents;
  private Boolean showDeleted;
  private LocalDateTime from;
  private LocalDateTime to;
  private EventOrderBy orderBy;

  public String getOrderBy() {
    return nonNull(orderBy) ? orderBy.getValue() : null;
  }
}
