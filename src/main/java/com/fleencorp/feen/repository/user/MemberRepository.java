package com.fleencorp.feen.repository.user;

import com.fleencorp.feen.model.domain.user.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

  Optional<Member> findByEmailAddress(String emailAddress);

  boolean existsByEmailAddress(String emailAddress);

  boolean existsByPhoneNumber(String phoneNumber);
}
