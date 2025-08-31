package com.fleencorp.feen.contact.model.domain;

import com.fleencorp.feen.contact.constant.ContactType;
import com.fleencorp.feen.model.domain.base.FleenFeenEntity;
import com.fleencorp.feen.user.model.domain.Member;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedBy;

import java.util.Objects;

import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static java.util.Objects.nonNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "contact")
public class Contact extends FleenFeenEntity {

  @Id
  @GeneratedValue(strategy = IDENTITY)
  @Column(name = "contact_id", nullable = false, updatable = false, unique = true)
  private Long contactId;

  @Enumerated(STRING)
  @Column(name = "contact_type", updatable = false)
  private ContactType contactType;

  @Column(name = "contact", length = 1000)
  private String contactValue;

  @Column(name = "owner_id", insertable = false, updatable = false)
  private Long memberId;

  @ToString.Exclude
  @CreatedBy
  @ManyToOne(fetch = LAZY, optional = false, targetEntity = Member.class)
  @JoinColumn(name = "owner_id", referencedColumnName = "member_id", nullable = false, updatable = false)
  private Member owner;

  public Long getOwnerId() {
    return nonNull(owner) ? owner.getMemberId() : null;
  }

  public boolean isChanged(final String newValue) {
    return !Objects.equals(this.contactValue, newValue);
  }

  public void update(final ContactType contactType, final String contact) {
    this.contactType = contactType;
    this.contactValue = contact;
  }
}
