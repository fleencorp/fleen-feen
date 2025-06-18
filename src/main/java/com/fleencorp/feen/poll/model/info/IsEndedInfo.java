package com.fleencorp.feen.poll.model.info;

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
  "ended",
  "ended_text",
  "ended_other_text"
})
public class IsEndedInfo {

  @JsonProperty("ended")
  private Boolean ended;

  @JsonProperty("ended_text")
  private String endedText;

  @JsonProperty("ended_other_text")
  private String endedOtherText;

  public static IsEndedInfo of(final Boolean ended, final String endedText, final String endedOtherText) {
    return new IsEndedInfo(ended, endedText, endedOtherText);
  }
}
