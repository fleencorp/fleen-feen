package com.fleencorp.feen.repository.security;

import com.fleencorp.feen.model.domain.user.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface MfaRepository extends JpaRepository<Member, Long> {

  @Modifying
  @Transactional
  @Query("UPDATE Member m SET m.mfaEnabled = true WHERE m.memberId = :memberId")
  void reEnableTwoFa(@Param("memberId") Long memberId);

  @Modifying
  @Transactional
  @Query("UPDATE Member m SET m.mfaEnabled = false WHERE m.memberId = :memberId")
  void disableTwoFa(@Param("memberId") Long memberId);

  @Query("SELECT m.mfaSecret FROM Member m WHERE m.emailAddress = :emailAddress")
  Optional<String> getTwoFaSecret(@Param("emailAddress") Long emailAddress);
}
