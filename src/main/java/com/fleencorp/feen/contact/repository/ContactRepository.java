package com.fleencorp.feen.contact.repository;

import com.fleencorp.feen.contact.constant.ContactType;
import com.fleencorp.feen.contact.model.domain.Contact;
import com.fleencorp.feen.user.model.domain.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ContactRepository extends JpaRepository<Contact, Long> {

  @Query("SELECT c FROM Contact c WHERE c.owner = :owner")
  Page<Contact> findByOwner(@Param("owner") Member owner, Pageable pageable);

  @Query("SELECT c FROM Contact c WHERE c.owner = :owner")
  List<Contact> findByOwner(@Param("owner") Member owner);

  @Query("SELECT c FROM Contact c WHERE c.memberId = :memberId AND c.contactType IN (:contactTypes)")
  List<Contact> findByContactTypeAndOwner(@Param("memberId") Long memberId, @Param("contactTypes") List<ContactType> contactTypes);

  Optional<Contact> findByContactTypeAndOwner(ContactType contactType, Member owner);

  long countByOwner(Member owner);
}
