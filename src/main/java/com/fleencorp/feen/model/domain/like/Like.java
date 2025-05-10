package com.fleencorp.feen.model.domain.like;

import com.fleencorp.feen.constant.like.LikeParentType;
import com.fleencorp.feen.constant.like.LikeType;
import com.fleencorp.feen.model.domain.base.FleenFeenEntity;
import com.fleencorp.feen.model.domain.chat.ChatSpace;
import com.fleencorp.feen.model.domain.review.Review;
import com.fleencorp.feen.model.domain.stream.FleenStream;
import com.fleencorp.feen.model.domain.user.Member;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;

import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.FetchType.EAGER;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "likes")
public class Like extends FleenFeenEntity {

  @Id
  @GeneratedValue(strategy = IDENTITY)
  @Column(name = "like_id", nullable = false, updatable = false, unique = true)
  private Long likeId;

  @Column(name = "parent_id", updatable = false)
  private Long parentId;

  @Column(name = "parent_title", updatable = false)
  private String parentTitle;

  @Enumerated(STRING)
  @Column(name = "like_parent_type", nullable = false)
  private LikeParentType likeParentType;

  @Enumerated(STRING)
  @Column(name = "like_type", nullable = false)
  private LikeType likeType;

  @Column(name = "chat_space_id", updatable = false, insertable = false)
  private Long chatSpaceId;

  @ManyToOne(fetch = LAZY, targetEntity = ChatSpace.class)
  @JoinColumn(name = "chat_space_id", referencedColumnName = "chat_space_id")
  private ChatSpace chatSpace;

  @Column(name = "stream_id", updatable = false, insertable = false)
  private Long streamId;

  @ManyToOne(fetch = LAZY, targetEntity = FleenStream.class)
  @JoinColumn(name = "stream_id", referencedColumnName = "stream_id")
  private FleenStream stream;

  @Column(name = "review_id", updatable = false, insertable = false)
  private Long reviewId;

  @ManyToOne(fetch = LAZY, targetEntity = Review.class)
  @JoinColumn(name = "review_id", referencedColumnName = "review_id")
  private Review review;

  @Column(name = "member_id", insertable = false, updatable = false)
  private Long memberId;

  @CreatedBy
  @ManyToOne(fetch = EAGER, optional = false, targetEntity = Member.class)
  @JoinColumn(name = "member_id", referencedColumnName = "member_id", nullable = false, updatable = false)
  private Member member;

  public Long getParentId() {
    if (LikeParentType.isStream(likeParentType)) {
      return streamId;
    } else if (LikeParentType.isChatSpace(likeParentType)) {
      return chatSpaceId;
    } else if (LikeParentType.isReview(likeParentType)) {
      return reviewId;
    }

    return null;
  }
}
