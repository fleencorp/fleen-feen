package com.fleencorp.feen.poll.model.response.core;

import com.fasterxml.jackson.annotation.*;
import com.fleencorp.feen.bookmark.model.info.BookmarkCountInfo;
import com.fleencorp.feen.bookmark.model.info.UserBookmarkInfo;
import com.fleencorp.feen.common.model.info.ParentInfo;
import com.fleencorp.feen.common.model.info.ShareCountInfo;
import com.fleencorp.feen.common.model.response.core.FleenFeenResponse;
import com.fleencorp.feen.like.model.info.LikeCountInfo;
import com.fleencorp.feen.like.model.info.UserLikeInfo;
import com.fleencorp.feen.model.contract.*;
import com.fleencorp.feen.poll.model.info.*;
import com.fleencorp.feen.user.model.response.UserResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;

import static com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING;
import static com.fleencorp.base.util.datetime.DateFormatUtil.DATE_TIME;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
  "id",
  "question",
  "description",
  "poll_visibility_info",
  "expires_at",
  "total_entries",
  "total_vote_entries_info",
  "is_multiple_choice_info",
  "is_anonymous_info",
  "is_ended_info",
  "parent_info",
  "user_bookmark_info",
  "bookmark_count_info",
  "user_like_info",
  "like_count_info",
  "share_count_info",
  "is_updatable",
  "slug",
  "poll_options",
  "poll_vote",
  "author",
  "created_on",
  "updated_on",
})
public class PollResponse extends FleenFeenResponse
  implements Bookmarkable, HasId, HasSlug, Likeable, Updatable {

  @JsonProperty("question")
  private String question;

  @JsonProperty("description")
  private String description;

  @JsonProperty("poll_visibility_info")
  private PollVisibilityInfo pollVisibilityInfo;

  @JsonFormat(shape = STRING, pattern = DATE_TIME)
  @JsonProperty("expires_at")
  private LocalDateTime expiresAt;

  @JsonProperty("is_multiple_choice_info")
  private IsPollMultipleChoiceInfo isPollMultipleChoiceInfo;

  @JsonProperty("is_anonymous_info")
  private IsPollAnonymousInfo isPollAnonymousInfo;

  @JsonProperty("is_ended_info")
  private PollIsEndedInfo pollIsEndedInfo;

  @JsonProperty("parent_info")
  private ParentInfo parentInfo;

  @JsonProperty("user_bookmark_info")
  private UserBookmarkInfo userBookmarkInfo;

  @JsonProperty("bookmark_count_info")
  private BookmarkCountInfo bookmarkCountInfo;

  @JsonProperty("user_like_info")
  private UserLikeInfo userLikeInfo;

  @JsonProperty("like_count_info")
  private LikeCountInfo likeCountInfo;

  @JsonProperty("share_count_info")
  private ShareCountInfo shareCountInfo;

  @JsonProperty("total_vote_entries_info")
  private TotalPollVoteEntriesInfo totalPollVoteEntriesInfo;

  @JsonProperty("poll_options")
  private Collection<PollOptionResponse> pollOptions = new ArrayList<>();

  @JsonProperty("poll_vote")
  private PollVoteResponse pollVote;

  @JsonProperty("author")
  private UserResponse author;

  @JsonProperty("slug")
  private String slug;

  @JsonProperty("is_updatable")
  private Boolean isUpdatable;


  @JsonIgnore
  private Long authorId;

  @JsonIgnore
  private Long organizerId;

  @Override
  @JsonIgnore
  public Long getAuthorId() {
    return getOrganizerId();
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
