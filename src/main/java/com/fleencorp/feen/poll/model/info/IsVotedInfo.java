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
  "voted",
  "voted_text",
  "voted_other_text"
})
public class IsVotedInfo {

  @JsonProperty("voted")
  private Boolean voted;

  @JsonProperty("voted_text")
  private String votedText;

  @JsonProperty("voted_other_text")
  private String votedOtherText;

  public static IsVotedInfo of(final Boolean voted, final String votedText, final String votedOtherText) {
    return new IsVotedInfo(voted, votedText, votedOtherText);
  }
}
