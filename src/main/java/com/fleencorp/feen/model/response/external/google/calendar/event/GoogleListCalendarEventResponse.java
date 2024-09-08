package com.fleencorp.feen.model.response.external.google.calendar.event;


import com.fleencorp.feen.model.response.external.google.calendar.event.base.GoogleCalendarEventResponse;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GoogleListCalendarEventResponse {

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


  public static GoogleListCalendarEventResponse of() {
    return new GoogleListCalendarEventResponse();
  }
}
