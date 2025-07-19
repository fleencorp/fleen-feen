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
  "vote_count",
  "vote_count_text",
})
public class SoftAskVoteCountInfo {

  @JsonProperty("vote_count")
  private Integer voteCount;

  @JsonProperty("vote_count_text")
  private String voteCountText;

  @JsonProperty("vote_count_text_2")
  private String voteCountText2;

  public static SoftAskVoteCountInfo of(final Integer voteCount, final String voteCountText, final String voteCountText2) {
    return new SoftAskVoteCountInfo(voteCount, voteCountText, voteCountText2);
  }
}
