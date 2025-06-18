package com.fleencorp.feen.poll.model.domain;

import com.fleencorp.feen.model.domain.base.FleenFeenEntity;
import com.fleencorp.feen.user.model.domain.Member;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "poll_vote", uniqueConstraints = {
  @UniqueConstraint(columnNames = {"poll_id", "member_id", "option_id"})
})
public class PollVote extends FleenFeenEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "vote_id", nullable = false, updatable = false, unique = true)
  private Long pollVoteId;

  @Column(name = "poll_id", insertable = false, updatable = false)
  private Long pollId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "poll_id", nullable = false)
  private Poll poll;

  @Column(name = "option_id", insertable = false, updatable = false)
  private Long pollOptionId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "option_id", nullable = false)
  private PollOption pollOption;

  @Column(name = "member_id", insertable = false, updatable = false)
  private Long voterId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "member_id", nullable = false)
  private Member voter;
}
