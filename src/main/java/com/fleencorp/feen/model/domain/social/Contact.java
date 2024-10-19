package com.fleencorp.feen.model.domain.social;

import com.fleencorp.feen.constant.social.ContactType;
import com.fleencorp.feen.model.domain.base.FleenFeenEntity;
import com.fleencorp.feen.model.domain.user.Member;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedBy;

import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;

@SuperBuilder
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
  private String contact;

  @CreatedBy
  @ManyToOne(fetch = LAZY, optional = false, targetEntity = Member.class)
  @JoinColumn(name = "owner_id", referencedColumnName = "member_id", nullable = false, updatable = false)
  private Member owner;

  public void update(final ContactType contactType, final String contact) {
    this.contactType = contactType;
    this.contact = contact;
  }
}
