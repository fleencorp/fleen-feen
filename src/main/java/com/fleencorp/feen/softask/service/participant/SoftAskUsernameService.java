package com.fleencorp.feen.softask.service.participant;

import com.fleencorp.feen.shared.common.model.GeneratedUsername;
import com.fleencorp.feen.softask.model.domain.SoftAskUsername;

public interface SoftAskUsernameService {

  SoftAskUsername generateUsername(Long softAskId, Long userId);

  GeneratedUsername getOrAssignUsername(Long softAskId, Long userId);
}
