package com.fleencorp.feen.model.info.stream.attendee;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.*;

@Builder
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
    return IsAttendingInfo.builder()
      .attending(isAttending)
      .attendingText(isAttendingText)
      .build();
  }

  public static IsAttendingInfo of() {
    return new IsAttendingInfo();
  }
}
