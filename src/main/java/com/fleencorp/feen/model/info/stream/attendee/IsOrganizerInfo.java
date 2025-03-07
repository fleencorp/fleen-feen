package com.fleencorp.feen.model.info.stream.attendee;

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
  "is_organizer",
  "is_organizer_test"
})
public class IsOrganizerInfo {

  @JsonProperty("is_organizer")
  private Boolean isOrganizer;

  @JsonProperty("is_organizer_text")
  private String isOrganizerText;

  @JsonProperty("is_organizer_text_2")
  private String isOrganizerText2;

  public static IsOrganizerInfo of(final Boolean isOrganizer, final String isOrganizerText, final String isOrganizerText2) {
    return new IsOrganizerInfo(isOrganizer, isOrganizerText, isOrganizerText2);
  }

  public static IsOrganizerInfo of() {
    return new IsOrganizerInfo();
  }
}
