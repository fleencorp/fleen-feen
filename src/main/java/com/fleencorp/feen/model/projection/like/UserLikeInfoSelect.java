package com.fleencorp.feen.model.projection.like;

import com.fleencorp.feen.model.domain.like.Like;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserLikeInfoSelect {

  private Long streamId;
  private Long chatSpaceId;
  private Long reviewId;
  private boolean liked;

  public UserLikeInfoSelect(final Like like) {
    this.streamId = like.getStreamId();
    this.chatSpaceId = like.getChatSpaceId();
    this.reviewId = like.getReviewId();
    this.liked = like.isLiked();
  }

}
