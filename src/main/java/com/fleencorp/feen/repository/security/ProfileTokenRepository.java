package com.fleencorp.feen.repository.security;

import com.fleencorp.feen.model.domain.user.ProfileToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ProfileTokenRepository extends JpaRepository<ProfileToken, Long> {

  @Query("SELECT pt FROM ProfileToken pt WHERE pt.member.emailAddress = :emailAddress")
  Optional<ProfileToken> findByEmailAddress(String emailAddress);
}
