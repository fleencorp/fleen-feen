package com.fleencorp.feen.poll.model.response.base;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.feen.model.response.base.FleenFeenResponse;
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
  "option_text",
  "vote_count",
  "stat"
})
public class PollOptionResponse extends FleenFeenResponse {

  @JsonProperty("option_text")
  private String optionText;

  @JsonProperty("vote_count")
  private Integer voteCount;

  @JsonProperty("stat")
  private PollStatResponse stat;
}
