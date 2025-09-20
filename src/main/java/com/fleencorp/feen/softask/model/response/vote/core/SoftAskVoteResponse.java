package com.fleencorp.feen.softask.model.response.vote.core;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.feen.common.model.info.ParentInfo;
import com.fleencorp.feen.common.model.response.core.FleenFeenResponse;
import com.fleencorp.feen.softask.model.info.vote.SoftAskUserVoteInfo;
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
  "parent_total_votes",
  "parent_info",
  "user_vote_info",
  "created_on",
  "updated_on"
})
public class SoftAskVoteResponse extends FleenFeenResponse {

  @JsonProperty("parent_total_votes")
  private Integer parentTotalVotes;

  @JsonProperty("parent_info")
  private ParentInfo parentInfo;

  @JsonProperty("user_vote_info")
  private SoftAskUserVoteInfo userVoteInfo;

  public static SoftAskVoteResponse of(final Long parentId, final Integer parentTotalVotes, final SoftAskUserVoteInfo softAskUserVoteInfo) {
    final ParentInfo parentInfo = ParentInfo.of(parentId);
    return new SoftAskVoteResponse(parentTotalVotes, parentInfo, softAskUserVoteInfo);
  }
}
