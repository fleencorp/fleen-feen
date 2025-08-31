package com.fleencorp.feen.poll.model.response.core;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.feen.common.model.response.core.FleenFeenResponse;
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
  "id",
  "option_text",
  "vote_count",
  "stat",
  "user_voted",
  "created_on",
  "updated_on",
})
public class PollOptionResponse extends FleenFeenResponse {

  @JsonProperty("option_text")
  private String optionText;

  @JsonProperty("vote_count")
  private Integer voteCount;

  @JsonProperty("stat")
  private PollStatResponse stat;

  @JsonProperty("user_voted")
  private Boolean userVoted = false;

  public void markUserVoted() {
    userVoted = true;
  }
}
