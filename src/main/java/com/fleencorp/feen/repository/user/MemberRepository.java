package com.fleencorp.feen.repository.user;

import com.fleencorp.feen.model.domain.user.Member;
import com.fleencorp.feen.model.projection.common.EmailAddressSelect;
import com.fleencorp.feen.model.projection.common.PhoneNumberSelect;
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

  @Query("SELECT new com.fleencorp.feen.model.projection.common.EmailAddressSelect(m.memberId, m.emailAddress) FROM Member m WHERE m.emailAddress = :emailAddress")
  Optional<EmailAddressSelect> findEmailOfMember(@Param("emailAddress") String emailAddress);

  @Query("SELECT new com.fleencorp.feen.model.projection.common.PhoneNumberSelect(m.memberId, m.phoneNumber) FROM Member m WHERE m.phoneNumber = :phoneNumber")
  Optional<PhoneNumberSelect> findPhoneOfMember(@Param("phoneNumber") String phoneNumber);
}
