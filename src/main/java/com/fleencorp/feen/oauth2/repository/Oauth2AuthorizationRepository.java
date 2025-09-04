package com.fleencorp.feen.oauth2.repository;

import com.fleencorp.feen.oauth2.constant.Oauth2ServiceType;
import com.fleencorp.feen.oauth2.model.domain.Oauth2Authorization;
import com.fleencorp.feen.user.model.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface Oauth2AuthorizationRepository extends JpaRepository<Oauth2Authorization, Long> {

  Optional<Oauth2Authorization> findByMemberIdAndServiceType(Long memberId, Oauth2ServiceType oauth2ServiceType);
}
