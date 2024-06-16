package com.fleencorp.feen.model.response.calendar.event;


import com.fleencorp.feen.model.response.calendar.event.base.GoogleCalendarEventResponse;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ListCalendarEventResponse {

  private String kind;
  private String etag;
  private String summary;
  private String description;
  private Object updated;
  private LocalDateTime updatedOn;
  private String timeZone;
  private String accessRole;

  @Builder.Default
  private List<GoogleCalendarEventResponse> items = new ArrayList<>();
  private String nextPageToken;
  private String nextSyncToken;


}
