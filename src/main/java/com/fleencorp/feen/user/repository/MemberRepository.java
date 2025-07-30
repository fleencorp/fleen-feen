package com.fleencorp.feen.user.repository;

import com.fleencorp.feen.common.model.projection.EmailAddressSelect;
import com.fleencorp.feen.common.model.projection.PhoneNumberSelect;
import com.fleencorp.feen.user.model.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

  Optional<Member> findByEmailAddress(String emailAddress);

  boolean existsByEmailAddress(String emailAddress);

  boolean existsByPhoneNumber(String phoneNumber);

  boolean existsByUsername(String username);

  boolean existsByMemberId(Long memberId);

  @Query("SELECT new com.fleencorp.feen.common.model.projection.EmailAddressSelect(m.memberId, m.emailAddress) FROM Member m WHERE m.emailAddress = :emailAddress")
  Optional<EmailAddressSelect> findEmailOfMember(@Param("emailAddress") String emailAddress);

  @Query("SELECT new com.fleencorp.feen.common.model.projection.PhoneNumberSelect(m.memberId, m.phoneNumber) FROM Member m WHERE m.phoneNumber = :phoneNumber")
  Optional<PhoneNumberSelect> findPhoneOfMember(@Param("phoneNumber") String phoneNumber);
}
