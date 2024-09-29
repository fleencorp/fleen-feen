package com.fleencorp.feen.repository.user;

import com.fleencorp.feen.model.domain.user.Member;
import com.fleencorp.feen.model.projection.EmailAddressSelect;
import com.fleencorp.feen.model.projection.PhoneNumberSelect;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.Set;

public interface MemberRepository extends JpaRepository<Member, Long> {

  Optional<Member> findByEmailAddress(String emailAddress);

  boolean existsByEmailAddress(String emailAddress);

  boolean existsByPhoneNumber(String phoneNumber);

  boolean existsByMemberId(Long memberId);

  @Query("SELECT COUNT(m.memberId) FROM Member m WHERE m.memberId IN (:ids)")
  long countByIds(@Param("ids") Set<Long> ids);

  @Query("SELECT m FROM Member m WHERE m.memberId IN (:ids)")
  Set<Member> findAllByIds(@Param("ids") Set<Long> ids);

  @Query("SELECT m FROM Member m WHERE m.emailAddress = :q OR m.firstName = :q OR m.lastName = :q")
  Page<Member> findAllByEmailAddressOrFirstNameOrLastName(@Param("q") String userIdOrName, Pageable pageable);

  @Query("SELECT new com.fleencorp.feen.model.projection.EmailAddressSelect(m.memberId, m.emailAddress) FROM Member m WHERE m.emailAddress = :emailAddress")
  Optional<EmailAddressSelect> findEmailOfMember(@Param("emailAddress") String emailAddress);

  @Query("SELECT new com.fleencorp.feen.model.projection.PhoneNumberSelect(m.memberId, m.phoneNumber) FROM Member m WHERE m.phoneNumber = :phoneNumber")
  Optional<PhoneNumberSelect> findPhoneOfMember(@Param("phoneNumber") String phoneNumber);
}
