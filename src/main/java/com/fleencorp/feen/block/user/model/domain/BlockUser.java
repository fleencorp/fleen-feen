package com.fleencorp.feen.block.user.model.domain;

import com.fleencorp.feen.common.constant.social.BlockStatus;
import com.fleencorp.feen.model.domain.base.FleenFeenEntity;
import com.fleencorp.feen.user.model.domain.Member;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.FetchType.EAGER;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static java.util.Objects.nonNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "block_user")
public class BlockUser extends FleenFeenEntity {

  @Id
  @GeneratedValue(strategy = IDENTITY)
  @Column(name = "block_user_id", nullable = false, updatable = false, unique = true)
  private Long blockedUserId;

  @Column(name = "initiator_id", nullable = false, updatable = false, insertable = false)
  private Long initiatorId;

  @ManyToOne(fetch = LAZY, optional = false, targetEntity = Member.class)
  @JoinColumn(name = "initiator_id", referencedColumnName = "member_id", nullable = false, updatable = false)
  private Member initiator;

  @Column(name = "recipient_id", nullable = false, updatable = false, insertable = false)
  private Long recipientId;

  @ManyToOne(fetch = EAGER, optional = false, targetEntity = Member.class)
  @JoinColumn(name = "recipient_id", referencedColumnName = "member_id", nullable = false, updatable = false)
  private Member recipient;

  @Enumerated(STRING)
  @Column(name = "block_status", nullable = false)
  private BlockStatus blockStatus = BlockStatus.BLOCKED;

  public Long getBlockedMemberId() {
    return nonNull(recipient) ? recipient.getMemberId() : null;
  }

  public String getBlockedMemberName() {
    return nonNull(recipient) ? recipient.getFullName() : null;
  }

  public String getBlockedMemberUsername() {
    return nonNull(recipient) ? recipient.getUsername() : null;
  }

  public Boolean isBlocked() {
    return nonNull(blockStatus) && BlockStatus.isBlocked(blockStatus);
  }

  public static BlockUser of(final Member initiator, final Member recipient, final BlockStatus blockStatus) {
    final BlockUser blockUser = new BlockUser();
    blockUser.setInitiatorId(initiator.getMemberId());
    blockUser.setInitiator(initiator);
    blockUser.setRecipientId(recipient.getMemberId());
    blockUser.setRecipient(recipient);
    blockUser.setBlockStatus(blockStatus);

    return blockUser;
  }
}
