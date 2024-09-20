package com.fleencorp.feen.model.response.stream;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.feen.constant.security.mask.MaskedEmailAddress;
import com.fleencorp.feen.constant.security.mask.MaskedPhoneNumber;
import com.fleencorp.feen.constant.stream.StreamSource;
import com.fleencorp.feen.constant.stream.StreamStatus;
import com.fleencorp.feen.constant.stream.StreamVisibility;
import com.fleencorp.feen.model.response.base.FleenFeenResponse;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.List;

import static com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING;
import static com.fleencorp.base.util.datetime.DateFormatUtil.DATE_TIME;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
  "id",
  "message",
  "title",
  "description",
  "location",
  "timezone",
  "created_on",
  "updated_on",
  "organizer",
  "for_kids",
  "stream_link",
  "stream_source",
  "visibility",
  "scheduled_start_date",
  "scheduled_end_date",
  "total_attending",
  "some_attendees"
})
public class FleenStreamResponse extends FleenFeenResponse {

  @JsonProperty("title")
  private String title;

  @JsonProperty("description")
  private String description;

  @JsonProperty("location")
  private String location;

  @JsonProperty("timezone")
  private String timezone;

  @JsonProperty("organizer")
  private Organizer organizer;

  @JsonProperty("for_kids")
  private Boolean forKids;

  @JsonProperty("stream_link")
  private String streamLink;

  @JsonFormat(shape = STRING)
  @JsonProperty("stream_source")
  private StreamSource streamSource;

  @JsonFormat(shape = STRING)
  @JsonProperty("visibility")
  private StreamVisibility visibility;

  @JsonFormat(shape = STRING, pattern = DATE_TIME)
  @JsonProperty("scheduled_start_date")
  private LocalDateTime scheduledStartDate;

  @JsonFormat(shape = STRING, pattern = DATE_TIME)
  @JsonProperty("scheduled_end_date")
  private LocalDateTime scheduledEndDate;

  @JsonFormat(shape = STRING)
  @JsonProperty("status")
  private StreamStatus status;

  @JsonProperty("total_attending")
  private long totalAttending;

  @JsonProperty("some_attendees")
  private List<StreamAttendeeResponse> someAttendees;

  @Builder
  @Getter
  @Setter
  @NoArgsConstructor
  @AllArgsConstructor
  @JsonInclude(JsonInclude.Include.NON_NULL)
  @JsonPropertyOrder({
    "name",
    "email",
    "phone"
  })
  public static class Organizer {

    @JsonProperty("name")
    private String organizerName;

    @JsonProperty("email")
    private MaskedEmailAddress organizerEmail;

    @JsonProperty("phone")
    private MaskedPhoneNumber organizerPhone;

    public static Organizer of(final String organizerName, final String organizerEmail, final String organizerPhone) {
      return Organizer.builder()
        .organizerName(organizerName)
        .organizerEmail(MaskedEmailAddress.of(organizerEmail))
        .organizerPhone(MaskedPhoneNumber.of(organizerPhone))
        .build();
    }
  }
}
