package com.fleencorp.feen.service.security;

import com.fleencorp.feen.model.domain.user.ProfileToken;

import java.util.Optional;

public interface ProfileTokenService {

  ProfileToken save(ProfileToken token);

  Optional<ProfileToken> findByEmailAddress(String emailAddress);

  void validateProfileToken(String emailAddress, String tokenOrCode);

  void resetProfileToken(Optional<ProfileToken> profileTokenExist);
}
