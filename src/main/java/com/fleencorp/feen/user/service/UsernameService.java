package com.fleencorp.feen.user.service;

import com.fleencorp.feen.shared.common.model.GeneratedUsername;
import com.fleencorp.feen.user.model.domain.Member;

public interface UsernameService {

  GeneratedUsername generateRandomUsername();

  void assignUniqueUsername(Member member);
}
