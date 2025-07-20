package com.fleencorp.feen.stream.model.info.attendee;

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
  "attending",
  "attending_text"
})
public class IsAttendingInfo {

  @JsonProperty("attending")
  private Boolean attending;

  @JsonProperty("attending_text")
  private String attendingText;

  public static IsAttendingInfo of(final Boolean isAttending, final String isAttendingText) {
    return new IsAttendingInfo(isAttending, isAttendingText);
  }

  public static IsAttendingInfo of() {
    return new IsAttendingInfo();
  }
}

