package com.fleencorp.feen.like.model.projection;

import com.fleencorp.feen.like.model.domain.Like;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserLikeInfoSelect {

  private Long chatSpaceId;
  private Long pollId;
  private Long memberId;
  private Long reviewId;
  private Long streamId;
  private boolean liked;

  public UserLikeInfoSelect(final Like like) {
    this.chatSpaceId = like.getChatSpaceId();
    this.pollId = like.getPollId();
    this.reviewId = like.getReviewId();
    this.streamId = like.getStreamId();
    this.liked = like.isLiked();
  }

}
