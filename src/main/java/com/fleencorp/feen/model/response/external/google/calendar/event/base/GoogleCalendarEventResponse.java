package com.fleencorp.feen.model.response.external.google.calendar.event.base;

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
  private String hangoutLink;
  private Object created;
  private LocalDateTime createdOn;
  private Object updated;
  private LocalDateTime updatedOn;
  private String summary;
  private String description;
  private String location;
  private Creator creator;
  private Organizer organizer;
  private Start start;
  private End end;
  private String iCalUID;
  private Integer sequence;
  private Reminders reminders;
  private Integer totalAttendeesOrGuests;
  private List<Attendee> attendees;
  private ConferenceData conferenceData;
  private ExtendedProperties extendedProperties;

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
    private List<Override> overrides;
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
    private ConferenceSolution conferenceSolution;
    private CreateConferenceRequest createRequest;

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
      private ConferenceSolutionKey conferenceSolutionKey;
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
