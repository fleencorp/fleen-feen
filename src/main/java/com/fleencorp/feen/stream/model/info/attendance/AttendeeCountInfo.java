package com.fleencorp.feen.stream.model.info.attendance;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
  "attendee_count",
  "attendee_text",
  "attendee_text_2",
  "attendee_other_text"
})
public class AttendeeCountInfo {

  @JsonProperty("attendee_count")
  private Integer attendeeCount;

  @JsonProperty("attendee_text")
  private String attendeeText;

  @JsonProperty("attendee_text_2")
  private String attendeeText2;

  @JsonProperty("attendee_other_text")
  private String attendeeOtherText;

  public static AttendeeCountInfo of(final Integer attendeeCount, final String attendeeText, final String attendeeText2, final String attendeeOtherText) {
    return new AttendeeCountInfo(attendeeCount, attendeeText, attendeeText2, attendeeOtherText);
  }
}
