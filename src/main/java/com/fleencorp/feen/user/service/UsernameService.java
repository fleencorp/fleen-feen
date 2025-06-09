package com.fleencorp.feen.user.service;

import com.fleencorp.feen.user.model.domain.Member;

public interface UsernameService {

  void assignUniqueUsername(Member member);
}
