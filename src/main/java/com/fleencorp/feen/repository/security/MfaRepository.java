package com.fleencorp.feen.repository.security;

import com.fleencorp.feen.user.model.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MfaRepository extends JpaRepository<Member, Long> {

  @Modifying
  @Query("UPDATE Member m SET m.mfaEnabled = :status WHERE m = :member")
  void enableOrDisableTwoFa(@Param("member") Member member, @Param("status") boolean status);

  @Query("SELECT m.mfaSecret FROM Member m WHERE m.emailAddress = :emailAddress")
  Optional<String> getTwoFaSecret(@Param("emailAddress") Long emailAddress);
}
