package com.fleencorp.feen.softask.model.info.vote;

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
  "vote_other_text"
})
public class SoftAskUserVoteInfo {

  @JsonProperty("voted")
  private Boolean voted;

  @JsonProperty("vote_other_text")
  private String voteOtherText;

  public static SoftAskUserVoteInfo of(final boolean voted, final String voteOtherText) {
    return new SoftAskUserVoteInfo(voted, voteOtherText);
  }
}
