package com.fleencorp.feen.softask.service.participant;

import com.fleencorp.feen.softask.model.domain.SoftAskParticipantDetail;

public interface SoftAskParticipantDetailService {

  SoftAskParticipantDetail generateParticipantDetail(Long softAskId, Long userId);

  SoftAskParticipantDetail getOrAssignParticipantDetail(Long softAskId, Long userId);
}
