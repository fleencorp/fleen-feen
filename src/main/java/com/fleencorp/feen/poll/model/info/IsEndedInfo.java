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
  "ended_text_2"
})
public class IsEndedInfo {

  @JsonProperty("ended")
  private Boolean ended;

  @JsonProperty("ended_text")
  private String endedText;

  @JsonProperty("ended_text_2")
  private String endedText2;

  public static IsEndedInfo of(final Boolean ended, final String endedText, final String endedText2) {
    return new IsEndedInfo(ended, endedText, endedText2);
  }
}
