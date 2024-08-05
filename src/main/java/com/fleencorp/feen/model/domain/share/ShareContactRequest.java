package com.fleencorp.feen.model.domain.share;

import com.fleencorp.feen.constant.share.ContactType;
import com.fleencorp.feen.constant.share.ShareContactRequestStatus;
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
@Table(name = "share_contact_request")
public class ShareContactRequest extends FleenFeenEntity {

  @Id
  @GeneratedValue(strategy = IDENTITY)
  @Column(name = "share_contact_request_id", nullable = false, updatable = false, unique = true)
  private Long shareContactRequestId;

  @Builder.Default
  @Column(name = "is_expected", nullable = false)
  private Boolean isExpected = false;

  @Enumerated(STRING)
  @Column(name = "share_contact_request_status")
  private ShareContactRequestStatus shareContactRequestStatus;

  @Enumerated(STRING)
  @Column(name = "contact_type", updatable = false)
  private ContactType contactType;

  @Column(name = "contact", length = 1000)
  private String contact;

  @ManyToOne(fetch = LAZY, optional = false, targetEntity = Member.class)
  @JoinColumn(name = "initiator_id", referencedColumnName = "member_id", nullable = false, updatable = false)
  private Member initiator;

  @ManyToOne(fetch = LAZY, optional = false, targetEntity = Member.class)
  @JoinColumn(name = "recipient_id", referencedColumnName = "member_id", nullable = false, updatable = false)
  private Member recipient;

  @Column(name = "initiator_comment", length = 1000)
  private String initiatorComment;

  @Column(name = "recipient_comment", length = 1000)
  private String recipientComment;

  public void update(final ShareContactRequestStatus status, final ContactType type, final String contact, final String comment) {
    this.shareContactRequestStatus = status;
    this.contactType = type;
    this.contact = contact;
    this.initiatorComment = comment;
  }
}
