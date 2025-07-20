package com.fleencorp.feen.softask.model.domain;

import com.fleencorp.feen.model.domain.base.FleenFeenEntity;
import com.fleencorp.feen.softask.constant.core.vote.SoftAskVoteParentType;
import com.fleencorp.feen.softask.constant.core.vote.SoftAskVoteType;
import com.fleencorp.feen.user.model.domain.Member;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedBy;

import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static java.util.Objects.nonNull;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "soft_ask_votes")
public class SoftAskVote extends FleenFeenEntity {

  @Id
  @GeneratedValue(strategy = IDENTITY)
  @Column(name = "vote_id", nullable = false, updatable = false, unique = true)
  private Long voteId;

  @Column(name = "parent_id", updatable = false)
  private Long parentId;

  @Column(name = "parent_title", updatable = false)
  private String parentTitle;

  @Enumerated(STRING)
  @Column(name = "vote_parent_type", nullable = false)
  private SoftAskVoteParentType parentType;

  @Enumerated(STRING)
  @Column(name = "vote_type", nullable = false)
  private SoftAskVoteType voteType;

  @Column(name = "soft_ask_id", updatable = false, insertable = false)
  private Long softAskId;

  @ToString.Exclude
  @ManyToOne(fetch = LAZY, targetEntity = SoftAsk.class)
  @JoinColumn(name = "soft_ask_id", referencedColumnName = "soft_ask_id")
  private SoftAsk softAsk;

  @Column(name = "soft_ask_answer_id", updatable = false, insertable = false)
  private Long softAskAnswerId;

  @ToString.Exclude
  @ManyToOne(fetch = LAZY, targetEntity = SoftAskAnswer.class)
  @JoinColumn(name = "soft_ask_answer_id", referencedColumnName = "soft_ask_answer_id")
  private SoftAskAnswer softAskAnswer;

  @Column(name = "soft_ask_reply_id", updatable = false, insertable = false)
  private Long softAskReplyId;

  @ToString.Exclude
  @ManyToOne(fetch = LAZY, targetEntity = SoftAskReply.class)
  @JoinColumn(name = "soft_ask_reply_id", referencedColumnName = "soft_ask_reply_id")
  private SoftAskReply softAskReply;

  @Column(name = "member_id", insertable = false, updatable = false)
  private Long memberId;

  @CreatedBy
  @ToString.Exclude
  @ManyToOne(fetch = LAZY, optional = false, targetEntity = Member.class)
  @JoinColumn(name = "member_id", referencedColumnName = "member_id", nullable = false, updatable = false)
  private Member member;

  public String getParentContent() {
    return switch (parentType) {
      case SOFT_ASK_ANSWER -> nonNull(softAskAnswer) ? softAskAnswer.getContent() : null;
      case SOFT_ASK_REPLY -> nonNull(softAskReply) ? softAskReply.getContent(): null;
      case SOFT_ASK -> nonNull(softAsk) ? softAsk.getDescription() : null;
    };
  }

  public boolean isVoted() {
    return SoftAskVoteType.voted(voteType);
  }

}
