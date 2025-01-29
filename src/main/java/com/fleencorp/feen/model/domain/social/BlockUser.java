package com.fleencorp.feen.model.domain.social;

import com.fleencorp.feen.constant.social.BlockStatus;
import com.fleencorp.feen.model.domain.base.FleenFeenEntity;
import com.fleencorp.feen.model.domain.user.Member;
import jakarta.persistence.*;
import lombok.*;

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

  @ManyToOne(fetch = LAZY, optional = false, targetEntity = Member.class)
  @JoinColumn(name = "initiator_id", referencedColumnName = "member_id", nullable = false, updatable = false)
  private Member initiator;

  @ManyToOne(fetch = EAGER, optional = false, targetEntity = Member.class)
  @JoinColumn(name = "recipient_id", referencedColumnName = "member_id", nullable = false, updatable = false)
  private Member recipient;

  @Enumerated(STRING)
  @Column(name = "block_status", nullable = false)
  private BlockStatus blockStatus = BlockStatus.BLOCKED;

  public Long getRecipientMemberId() {
    return nonNull(recipient) ? recipient.getMemberId() : null;
  }

  public String getRecipientName() {
    return nonNull(recipient) ? recipient.getFullName() : null;
  }

  public static BlockUser of(final Member initiator, final Member recipient, final BlockStatus blockStatus) {
    final BlockUser blockUser = new BlockUser();
    blockUser.setInitiator(initiator);
    blockUser.setRecipient(recipient);
    blockUser.setBlockStatus(blockStatus);

    return blockUser;
  }
}
