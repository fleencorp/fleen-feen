package com.fleencorp.feen.softask.model.response.vote;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.feen.softask.model.response.vote.core.SoftAskVoteResponse;
import com.fleencorp.localizer.model.response.LocalizedResponse;
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
  "vote_id",
  "vote"
})
public class SoftAskVoteUpdateResponse extends LocalizedResponse {

  @JsonProperty("vote_id")
  private Long voteId;

  @JsonProperty("vote")
  private SoftAskVoteResponse voteResponse;

  @Override
  public String getMessageCode() {
    return "soft.ask.vote.update";
  }

  public static SoftAskVoteUpdateResponse of(final Long voteId, final SoftAskVoteResponse voteResponse) {
    return new SoftAskVoteUpdateResponse(voteId, voteResponse);
  }
}
