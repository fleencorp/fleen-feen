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
  private Long reviewId;
  private Long streamId;
  private Long softAskId;
  private Long softAskReplyId;
  private boolean bookmarked;

  public UserBookmarkInfoSelect(final Bookmark bookmark) {
    this.chatSpaceId = bookmark.getChatSpaceId();
    this.reviewId = bookmark.getReviewId();
    this.streamId = bookmark.getStreamId();
    this.bookmarked = bookmark.isBookmarked();
    this.softAskId = bookmark.getSoftAskReply().getSoftAskId();
    this.softAskReplyId = bookmark.getSoftAskReplyId();
  }

}
