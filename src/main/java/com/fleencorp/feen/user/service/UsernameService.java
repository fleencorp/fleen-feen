package com.fleencorp.feen.user.service;

import com.fleencorp.feen.shared.common.model.GeneratedParticipantDetail;
import com.fleencorp.feen.user.model.domain.Member;

public interface UsernameService {

  GeneratedParticipantDetail generateRandomUsername();

  void assignUniqueUsername(Member member);
}
