package com.fleencorp.feen.repository.user;

import com.fleencorp.feen.model.domain.user.Member;
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
}
