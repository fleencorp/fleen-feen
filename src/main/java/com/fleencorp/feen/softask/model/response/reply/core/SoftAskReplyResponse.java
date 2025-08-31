package com.fleencorp.feen.softask.model.response.reply.core;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.feen.bookmark.model.info.BookmarkCountInfo;
import com.fleencorp.feen.bookmark.model.info.UserBookmarkInfo;
import com.fleencorp.feen.common.model.info.ParentInfo;
import com.fleencorp.feen.common.model.info.ShareCountInfo;
import com.fleencorp.feen.common.model.info.UserLocationInfo;
import com.fleencorp.feen.common.model.response.core.FleenFeenResponse;
import com.fleencorp.feen.model.contract.Bookmarkable;
import com.fleencorp.feen.model.contract.HasId;
import com.fleencorp.feen.model.contract.Updatable;
import com.fleencorp.feen.softask.constant.core.SoftAskType;
import com.fleencorp.feen.softask.contract.HasMood;
import com.fleencorp.feen.softask.contract.SoftAskCommonResponse;
import com.fleencorp.feen.softask.model.info.core.MoodTagInfo;
import com.fleencorp.feen.softask.model.info.reply.SoftAskReplyCountInfo;
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
  "content",
  "author_id",
  "vote_count_info",
  "user_vote_info",
  "bookmark_count_info",
  "user_bookmark_info",
  "child_reply_count_info",
  "share_count_info",
  "parent_info",
  "child_replies_search_result",
  "user_location_info",
  "mood_tag_info",
  "display_time_label",
  "user",
  "created_on",
  "updated_on",
  "is_updatable"
})
public class SoftAskReplyResponse extends FleenFeenResponse
  implements Bookmarkable, HasId, HasMood, SoftAskCommonResponse, Updatable {

  @JsonProperty("content")
  private String content;

  @JsonProperty("vote_count_info")
  private SoftAskVoteCountInfo voteCountInfo;

  @JsonProperty("user_vote_info")
  private SoftAskUserVoteInfo softAskUserVoteInfo;

  @JsonProperty("bookmark_count_info")
  private BookmarkCountInfo bookmarkCountInfo;

  @JsonProperty("user_bookmark_info")
  private UserBookmarkInfo userBookmarkInfo;

  @JsonProperty("child_reply_count_info")
  private SoftAskReplyCountInfo replyCountInfo;

  @JsonProperty("share_count_info")
  private ShareCountInfo shareCountInfo;

  @JsonProperty("parent_info")
  private ParentInfo parentInfo;

  @JsonProperty("user")
  private SoftAskParticipantResponse softAskParticipantResponse;

  @JsonProperty("child_replies_search_result")
  private SoftAskReplySearchResult childRepliesSearchResult;

  @JsonProperty("user_location_info")
  private UserLocationInfo userLocationInfo;

  @JsonProperty("mood_tag_info")
  private MoodTagInfo moodTagInfo;

  @JsonProperty("display_time_label")
  private String displayTimeLabel;

  @JsonProperty("is_updatable")
  private Boolean isUpdatable;

  @JsonIgnore
  private Long authorId;

  @JsonIgnore
  private Long organizerId;

  @JsonIgnore
  private Long memberId;

  @Override
  @JsonIgnore
  public SoftAskType getSoftAskType() {
    return SoftAskType.SOFT_ASK_REPLY;
  }

  @Override
  @JsonIgnore
  public Long getParentId() {
    return nonNull(parentInfo) ? parentInfo.getParentId() : null;
  }

  @Override
  @JsonIgnore
  public boolean hasLatitudeAndLongitude() {
    return nonNull(userLocationInfo) && userLocationInfo.hasLatitudeAndLongitude();
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

