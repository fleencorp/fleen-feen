package com.fleencorp.feen.repository.share;

import com.fleencorp.feen.constant.share.ContactType;
import com.fleencorp.feen.model.domain.share.Contact;
import com.fleencorp.feen.model.domain.user.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ContactRepository extends JpaRepository<Contact, Long> {

  Page<Contact> findByOwner(Member owner, Pageable pageable);

  Optional<Contact> findByOwnerAndContactType(Member owner, ContactType contactType);

  Optional<Contact> findByContactIdAndOwner(Long contactId, Member member);

  void deleteAllByOwner(Member owner);
}
