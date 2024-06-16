package com.fleencorp.feen.model.response.calendar.calendar.base;


import com.fleencorp.feen.model.response.calendar.event.ListCalendarEventResponse;
import com.fleencorp.feen.model.response.calendar.event.base.GoogleCalendarEventResponse;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Builder
@Getter
@Setter
public class GoogleCalendarResponse {

  private String kind;
  private String etag;
  private String id;
  private String summary;
  private String description;
  private String timeZone;
  private String location;
  private String summaryOverride;
  private String colorId;
  private Boolean hidden;
  private Boolean selected;
  private Boolean primary;
  private String foregroundColor;
  private String backgroundColor;
  private String borderColor;
  private String accessRole;
  private List<Reminders> defaultReminders;
  private Boolean deleted;
  private ConferenceProperties conferenceProperties;

  @Builder
  @Getter
  @Setter
  public static class ConferenceProperties {
    private String conferenceId;
    private String signature;
    private String notes;
    private List<String> conferenceSolutionTypes;
  }

  @Builder
  @Getter
  @Setter
  public static class Reminders {
    private Boolean useDefault;
    private String method;
    private Integer minutes;
  }
}
