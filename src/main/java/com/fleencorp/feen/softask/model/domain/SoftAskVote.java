package com.fleencorp.feen.softask.model.domain;

import com.fleencorp.feen.model.domain.base.FleenFeenEntity;
import com.fleencorp.feen.softask.constant.core.vote.SoftAskVoteParentType;
import com.fleencorp.feen.softask.constant.core.vote.SoftAskVoteType;
import jakarta.persistence.*;
import lombok.*;

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
  @Column(name = "parent_type", nullable = false)
  private SoftAskVoteParentType parentType;

  @Enumerated(STRING)
  @Column(name = "type", nullable = false)
  private SoftAskVoteType voteType;

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

  @Column(name = "member_id", nullable = false, updatable = false)
  private Long memberId;

  public String getParentContent() {
    return switch (parentType) {
      case SOFT_ASK_REPLY -> nonNull(softAskReply) ? softAskReply.getContent(): null;
      case SOFT_ASK -> nonNull(softAsk) ? softAsk.getDescription() : null;
    };
  }

  public boolean isVoted() {
    return SoftAskVoteType.voted(voteType);
  }

}
