package com.fleencorp.feen.review.model.response.base;

import com.fasterxml.jackson.annotation.*;
import com.fleencorp.feen.bookmark.model.info.BookmarkCountInfo;
import com.fleencorp.feen.bookmark.model.info.UserBookmarkInfo;
import com.fleencorp.feen.common.model.info.ParentInfo;
import com.fleencorp.feen.common.model.response.core.FleenFeenResponse;
import com.fleencorp.feen.like.model.info.LikeCountInfo;
import com.fleencorp.feen.like.model.info.UserLikeInfo;
import com.fleencorp.feen.model.contract.Bookmarkable;
import com.fleencorp.feen.model.contract.HasId;
import com.fleencorp.feen.model.contract.Likeable;
import com.fleencorp.feen.model.contract.Updatable;
import com.fleencorp.feen.review.constant.ReviewParentType;
import com.fleencorp.feen.stream.model.info.rating.RatingInfo;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
  "id",
  "review",
  "review_parent_type",
  "reviewer_name",
  "reviewer_photo",
  "rating_info",
  "parent_info",
  "bookmark_count_info",
  "user_bookmark_info",
  "user_like_info",
  "like_count_info",
  "is_updatable",
  "created_on",
  "updated_on",
})
public class ReviewResponse extends FleenFeenResponse
  implements Bookmarkable, HasId, Likeable, Updatable {

  @JsonProperty("review")
  private String review;

  @JsonFormat(shape = STRING)
  @JsonProperty("review_parent_type")
  private ReviewParentType reviewParentType;

  @JsonProperty("rating_info")
  private RatingInfo ratingInfo;

  @JsonProperty("reviewer_name")
  private String reviewerName;

  @JsonProperty("reviewer_photo")
  private String reviewerPhoto;

  @JsonProperty("is_updatable")
  private Boolean isUpdatable;

  @JsonProperty("parent_info")
  private ParentInfo parentInfo;

  @JsonProperty("bookmark_count_info")
  private BookmarkCountInfo bookmarkCountInfo;

  @JsonProperty("user_bookmark_info")
  private UserBookmarkInfo userBookmarkInfo;

  @JsonProperty("user_like_info")
  private UserLikeInfo userLikeInfo;

  @JsonProperty("like_count_info")
  private LikeCountInfo likeCountInfo;

  @JsonIgnore
  private Long authorId;

  @JsonIgnore
  private Long organizerId;

  @JsonIgnore
  private Long memberId;

  @Override
  @JsonIgnore
  public Long getAuthorId() {
    return getMemberId();
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
