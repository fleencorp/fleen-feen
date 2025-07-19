package com.fleencorp.feen.softask.model.response.reply.core;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.feen.model.contract.Updatable;
import com.fleencorp.feen.model.info.ParentInfo;
import com.fleencorp.feen.model.response.base.FleenFeenResponse;
import com.fleencorp.feen.softask.contract.SoftAskCommonResponse;
import com.fleencorp.feen.softask.model.info.vote.SoftAskUserVoteInfo;
import com.fleencorp.feen.softask.model.info.vote.SoftAskVoteCountInfo;
import com.fleencorp.feen.softask.model.response.participant.SoftAskParticipantResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static java.util.Objects.nonNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
  "id",
  "content",
  "author_id",
  "vote_count_info",
  "user_vote_info",
  "parent_info",
  "user",
  "created_on",
  "updated_on",
  "is_updatable"
})
public class SoftAskReplyResponse extends FleenFeenResponse
  implements SoftAskCommonResponse, Updatable {

  @JsonProperty("content")
  private String content;

  @JsonProperty("vote_count_info")
  private SoftAskVoteCountInfo voteCountInfo;

  @JsonProperty("user_vote_info")
  private SoftAskUserVoteInfo softAskUserVoteInfo;

  @JsonProperty("parent_info")
  private ParentInfo parentInfo;

  @JsonProperty("user")
  private SoftAskParticipantResponse softAskParticipantResponse;

  @JsonProperty("is_updatable")
  private Boolean isUpdatable;

  @JsonIgnore
  private Long memberId;

  @JsonIgnore
  public Long getParentId() {
    return nonNull(parentInfo) ? parentInfo.getParentId() : null;
  }

  @Override
  public void setIsUpdatable(final boolean isUpdatable) {
    this.isUpdatable = isUpdatable;
  }

  @Override
  public void markAsUpdatable() {
    setIsUpdatable(true);
  }
}

