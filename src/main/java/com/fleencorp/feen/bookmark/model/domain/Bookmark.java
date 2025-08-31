package com.fleencorp.feen.bookmark.model.domain;

import com.fleencorp.feen.bookmark.constant.BookmarkParentType;
import com.fleencorp.feen.bookmark.constant.BookmarkType;
import com.fleencorp.feen.chat.space.model.domain.ChatSpace;
import com.fleencorp.feen.model.domain.base.FleenFeenEntity;
import com.fleencorp.feen.review.model.domain.Review;
import com.fleencorp.feen.softask.model.domain.SoftAsk;
import com.fleencorp.feen.softask.model.domain.SoftAskReply;
import com.fleencorp.feen.stream.model.domain.FleenStream;
import com.fleencorp.feen.user.model.domain.Member;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedBy;

import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "bookmarks")
public class Bookmark extends FleenFeenEntity {

  @Id
  @GeneratedValue(strategy = IDENTITY)
  @Column(name = "bookmark_id", nullable = false, updatable = false, unique = true)
  private Long bookmarkId;

  @Column(name = "parent_id", updatable = false)
  private Long parentId;

  @Column(name = "parent_summary", updatable = false)
  private String parentSummary;

  @Enumerated(STRING)
  @Column(name = "bookmark_parent_type", nullable = false)
  private BookmarkParentType bookmarkParentType;

  @Enumerated(STRING)
  @Column(name = "bookmark_type", nullable = false)
  private BookmarkType bookmarkType;

  @Column(name = "chat_space_id", updatable = false, insertable = false)
  private Long chatSpaceId;

  @ToString.Exclude
  @ManyToOne(fetch = LAZY, targetEntity = ChatSpace.class)
  @JoinColumn(name = "chat_space_id", referencedColumnName = "chat_space_id")
  private ChatSpace chatSpace;

  @Column(name = "stream_id", updatable = false, insertable = false)
  private Long streamId;

  @ToString.Exclude
  @ManyToOne(fetch = LAZY, targetEntity = FleenStream.class)
  @JoinColumn(name = "stream_id", referencedColumnName = "stream_id")
  private FleenStream stream;

  @Column(name = "review_id", updatable = false, insertable = false)
  private Long reviewId;

  @ToString.Exclude
  @ManyToOne(fetch = LAZY, targetEntity = Review.class)
  @JoinColumn(name = "review_id", referencedColumnName = "review_id")
  private Review review;

  @Column(name = "soft_ask_id", updatable = false, insertable = false)
  private Long softAskId;

  @ToString.Exclude
  @ManyToOne(fetch = LAZY, targetEntity = SoftAsk.class)
  @JoinColumn(name = "soft_ask_id", referencedColumnName = "soft_ask_id")
  private SoftAsk softAsk;

  @Column(name = "soft_ask_reply_id", updatable = false, insertable = false)
  private Long softAskReplyId;

  @ToString.Exclude
  @ManyToOne(fetch = LAZY, targetEntity = SoftAskReply.class)
  @JoinColumn(name = "soft_ask_reply_id", referencedColumnName = "soft_ask_reply_id")
  private SoftAskReply softAskReply;

  @Column(name = "member_id", insertable = false, updatable = false)
  private Long memberId;

  @ToString.Exclude
  @CreatedBy
  @ManyToOne(fetch = LAZY, optional = false, targetEntity = Member.class)
  @JoinColumn(name = "member_id", referencedColumnName = "member_id", nullable = false, updatable = false)
  private Member member;

  public Long getOtherId() {
    return BookmarkParentType.isSoftAskReply(bookmarkParentType) ? softAskId : null;
  }

  public boolean isBookmarked() {
    return BookmarkType.isBookmarked(bookmarkType);
  }

  public Bookmark updateType(final BookmarkType type) {
    setBookmarkType(type);
    return this;
  }
}
