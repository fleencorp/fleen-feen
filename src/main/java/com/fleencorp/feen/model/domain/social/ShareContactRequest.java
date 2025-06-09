package com.fleencorp.feen.model.domain.social;

import com.fleencorp.feen.constant.social.ShareContactRequestStatus;
import com.fleencorp.feen.contact.constant.ContactType;
import com.fleencorp.feen.model.domain.base.FleenFeenEntity;
import com.fleencorp.feen.user.model.domain.Member;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;

import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static java.util.Objects.nonNull;

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

  @Column(name = "is_expected", nullable = false)
  private Boolean isExpected = false;

  @Enumerated(STRING)
  @Column(name = "share_contact_request_status")
  private ShareContactRequestStatus requestStatus;

  @Enumerated(STRING)
  @Column(name = "contact_type", updatable = false)
  private ContactType contactType;

  @Column(name = "contact", length = 1000)
  private String contact;

  @CreatedBy
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

  public String getRecipientName() {
    return nonNull(recipient) ? recipient.getFullName() : null;
  }

  /**
   * Updates the share contact request with the specified status, contact type, contact information, and comment.
   *
   * @param status the new status of the contact request
   * @param type the type of contact being shared (e.g., email, phone)
   * @param contact the contact information being shared
   * @param comment an optional comment from the initiator
   */
  public void update(final ShareContactRequestStatus status, final ContactType type, final String contact, final String comment) {
    this.requestStatus = status;
    this.contactType = type;
    this.contact = contact;
    this.initiatorComment = comment;
  }

  /**
   * Checks if the share contact request has been rejected.
   *
   * @return {@code true} if the request status is rejected, {@code false} otherwise
   */
  public boolean isRejected() {
    return ShareContactRequestStatus.isAcceptedOrRejected(requestStatus);
  }

  public void cancel() {
    requestStatus = ShareContactRequestStatus.CANCELED;
  }
}
