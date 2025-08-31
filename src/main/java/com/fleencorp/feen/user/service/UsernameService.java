package com.fleencorp.feen.user.service;

import com.fleencorp.feen.user.model.domain.Member;

public interface UsernameService {

  String generateRandomUsername();

  void assignUniqueUsername(Member member);
}
