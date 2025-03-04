package com.fleencorp.feen.service.user;

import com.fleencorp.feen.model.domain.user.Member;

public interface UsernameService {

  void assignUniqueUsername(Member member);
}
