package com.fleencorp.feen.repository.oauth2;

import com.fleencorp.feen.constant.external.google.oauth2.Oauth2ServiceType;
import com.fleencorp.feen.model.domain.auth.Oauth2Authorization;
import com.fleencorp.feen.model.domain.user.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface Oauth2AuthorizationRepository extends JpaRepository<Oauth2Authorization, Long> {

  Optional<Oauth2Authorization> findByMember(Member member);

  Optional<Oauth2Authorization> findByMemberAndServiceType(Member member, Oauth2ServiceType oauth2ServiceType);
}
