package com.fleencorp.feen.model.domain.user;

import com.fleencorp.feen.constant.user.ShareContactRequestStatus;
import com.fleencorp.feen.constant.user.ShareContactRequestType;
import com.fleencorp.feen.model.domain.base.FleenFeenEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
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

  @Enumerated(STRING)
  @Column(name = "share_contact_request_status", nullable = false)
  private ShareContactRequestStatus shareContactRequestStatus;

  @Enumerated(STRING)
  @Column(name = "share_contact_request_type", nullable = false, updatable = false)
  private ShareContactRequestType shareContactRequestType;

  @Column(name = "contact", updatable = false, length = 1000)
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
}
