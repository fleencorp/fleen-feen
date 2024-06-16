package com.fleencorp.feen.model.response.calendar.event.base;

import com.fleencorp.feen.model.response.calendar.event.ListCalendarEventResponse;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GoogleCalendarEventResponse {

  private String kind;
  private String etag;
  private String id;
  private String status;
  private String htmlLink;
  private Object created;
  private LocalDateTime createdOn;
  private Object updated;
  private LocalDateTime updatedOn;
  private String summary;
  private String description;
  private String location;
  private GoogleCalendarEventResponse.Creator creator;
  private GoogleCalendarEventResponse.Organizer organizer;
  private GoogleCalendarEventResponse.Start start;
  private GoogleCalendarEventResponse.End end;
  private String iCalUID;
  private Integer sequence;
  private GoogleCalendarEventResponse.Reminders reminders;
  private Integer totalAttendeesOrGuests;
  private List<GoogleCalendarEventResponse.Attendee> attendees;
  private GoogleCalendarEventResponse.ConferenceData conferenceData;
  private GoogleCalendarEventResponse.ExtendedProperties extendedProperties;

  @Builder
  @Getter
  @Setter
  public static class Creator {
    private String id;
    private String email;
    private String displayName;
    private Boolean self;
  }

  @Builder
  @Getter
  @Setter
  public static class Organizer {
    private String id;
    private String email;
    private String displayName;
    private Boolean self;
  }

  @Builder
  @Getter
  @Setter
  public static class Start {
    private Object dateTime;
    private String timeZone;
    private LocalDateTime actualDateTime;
  }

  @Builder
  @Getter
  @Setter
  public static class End {
    private Object dateTime;
    private String timeZone;
    private LocalDateTime actualDateTime;
  }

  @Builder
  @Getter
  @Setter
  public static class Reminders {
    private Boolean useDefault;
    private List<GoogleCalendarEventResponse.Reminders.Override> overrides;
    private String method;
    private Integer minutes;

    @Builder
    @Getter
    @Setter
    public static class Override {
      private String method;
      private Integer minutes;
    }
  }

  @Builder
  @Getter
  @Setter
  public static class Attendee {
    private String id;
    private String email;
    private String displayName;
    private Boolean organizer;
    private Boolean self;
    private Boolean resource;
    private Boolean optional;
    private String responseStatus;
    private String comment;
    private Integer additionalGuests;
  }

  @Builder
  @Getter
  @Setter
  public static class ConferenceData {
    private GoogleCalendarEventResponse.ConferenceData.ConferenceSolution conferenceSolution;
    private GoogleCalendarEventResponse.ConferenceData.CreateConferenceRequest createRequest;

    @Builder
    @Getter
    @Setter
    public static class ConferenceSolution {
      private String name;
    }

    @Builder
    @Getter
    @Setter
    public static class CreateConferenceRequest {
      private String requestId;
      private GoogleCalendarEventResponse.ConferenceData.ConferenceSolutionKey conferenceSolutionKey;
    }

    @Builder
    @Getter
    @Setter
    public static class ConferenceSolutionKey {
      private String type;
    }
  }

  @Builder
  @Getter
  @Setter
  public static class ExtendedProperties {
    private Map<String, String> shared;
  }

}
