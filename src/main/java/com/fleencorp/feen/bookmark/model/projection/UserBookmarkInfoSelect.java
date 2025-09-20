package com.fleencorp.feen.bookmark.model.projection;

import com.fleencorp.feen.bookmark.model.domain.Bookmark;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserBookmarkInfoSelect {

  private Long chatSpaceId;
  private Long pollId;
  private Long reviewId;
  private Long softAskId;
  private Long softAskReplyId;
  private Long streamId;
  private boolean bookmarked;

  public UserBookmarkInfoSelect(final Bookmark bookmark) {
    this.chatSpaceId = bookmark.getChatSpaceId();
    this.pollId = bookmark.getPollId();
    this.reviewId = bookmark.getReviewId();
    this.softAskId = bookmark.getSoftAskReply().getSoftAskId();
    this.softAskReplyId = bookmark.getSoftAskReplyId();
    this.streamId = bookmark.getStreamId();
    this.bookmarked = bookmark.isBookmarked();
  }

}
