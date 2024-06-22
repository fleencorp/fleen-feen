package com.fleencorp.feen.repository;

import com.fleencorp.feen.model.domain.google.oauth2.GoogleOauth2Authorization;
import com.fleencorp.feen.model.domain.user.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GoogleOauth2AuthorizationRepository extends JpaRepository<GoogleOauth2Authorization, Long> {

  Optional<GoogleOauth2Authorization> findByMember(Member member);
}
