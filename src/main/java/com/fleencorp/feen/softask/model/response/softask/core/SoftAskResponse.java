package com.fleencorp.feen.softask.model.response.softask.core;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.feen.bookmark.model.info.BookmarkCountInfo;
import com.fleencorp.feen.bookmark.model.info.UserBookmarkInfo;
import com.fleencorp.feen.common.model.info.ParentInfo;
import com.fleencorp.feen.common.model.response.core.FleenFeenResponse;
import com.fleencorp.feen.model.contract.Bookmarkable;
import com.fleencorp.feen.model.contract.HasId;
import com.fleencorp.feen.model.contract.Updatable;
import com.fleencorp.feen.softask.constant.core.SoftAskType;
import com.fleencorp.feen.softask.contract.SoftAskCommonResponse;
import com.fleencorp.feen.softask.model.info.SoftAskReplyCountInfo;
import com.fleencorp.feen.softask.model.info.vote.SoftAskUserVoteInfo;
import com.fleencorp.feen.softask.model.info.vote.SoftAskVoteCountInfo;
import com.fleencorp.feen.softask.model.response.participant.SoftAskParticipantResponse;
import com.fleencorp.feen.softask.model.search.SoftAskReplySearchResult;
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
  "title",
  "content",
  "author_id",
  "reply_count_info",
  "vote_count_info",
  "user_vote_info",
  "bookmark_count_info",
  "user_bookmark_info",
  "share_count",
  "parent_info",
  "user",
  "created_on",
  "updated_on",
  "is_updatable",
  "reply_search_result"
})
public class SoftAskResponse extends FleenFeenResponse
  implements Bookmarkable, HasId, SoftAskCommonResponse, Updatable {

  @JsonProperty("title")
  private String title;

  @JsonProperty("content")
  private String content;

  @JsonProperty("reply_count_info")
  private SoftAskReplyCountInfo replyCountInfo;

  @JsonProperty("vote_count_info")
  private SoftAskVoteCountInfo voteCountInfo;

  @JsonProperty("user_vote_info")
  private SoftAskUserVoteInfo softAskUserVoteInfo;

  @JsonProperty("bookmark_count_info")
  private BookmarkCountInfo bookmarkCountInfo;

  @JsonProperty("user_bookmark_info")
  private UserBookmarkInfo userBookmarkInfo;

  @JsonProperty("share_count")
  private Integer shareCount;

  @JsonProperty("parent_info")
  private ParentInfo parentInfo;

  @JsonProperty("user")
  private SoftAskParticipantResponse softAskParticipantResponse;

  @JsonProperty("reply_search_result")
  private SoftAskReplySearchResult softAskReplySearchResult;

  @JsonProperty("is_updatable")
  private Boolean isUpdatable;

  @JsonIgnore
  private Long authorId;

  @JsonIgnore
  private Long organizerId;

  @JsonIgnore
  private Long memberId;

  @JsonIgnore
  public SoftAskType getSoftAskType() {
    return SoftAskType.SOFT_ASK;
  }

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
