package com.fleencorp.feen.verification.service;

import com.fleencorp.feen.verification.model.domain.ProfileToken;

import java.util.Optional;

public interface ProfileTokenService {

  ProfileToken save(ProfileToken token);

  Optional<ProfileToken> findByEmailAddress(String emailAddress);

  void validateProfileToken(String emailAddress, String tokenOrCode);

  void resetProfileToken(Optional<ProfileToken> profileTokenExist);
}
