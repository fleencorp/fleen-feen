package com.fleencorp.feen.model.domain.share;

import com.fleencorp.feen.constant.share.BlockStatus;
import com.fleencorp.feen.model.domain.base.FleenFeenEntity;
import com.fleencorp.feen.model.domain.user.Member;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "blocked_user")
public class BlockUser extends FleenFeenEntity {

  @Id
  @GeneratedValue(strategy = IDENTITY)
  @Column(name = "block_user_id", nullable = false, updatable = false, unique = true)
  private Long blockedUserId;

  @ManyToOne(fetch = LAZY, optional = false, targetEntity = Member.class)
  @JoinColumn(name = "initiator_id", referencedColumnName = "member_id", nullable = false, updatable = false)
  private Member initiator;

  @ManyToOne(fetch = LAZY, optional = false, targetEntity = Member.class)
  @JoinColumn(name = "recipient_id", referencedColumnName = "member_id", nullable = false, updatable = false)
  private Member recipient;

  @Builder.Default
  @Enumerated(STRING)
  @Column(name = "block_status", nullable = false)
  private BlockStatus blockStatus = BlockStatus.BLOCKED;

  public static BlockUser of(final Member initiator, final Member recipient, final BlockStatus blockStatus) {
    return BlockUser.builder()
      .initiator(initiator)
      .recipient(recipient)
      .blockStatus(blockStatus)
      .build();
  }
}
