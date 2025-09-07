package com.fleencorp.feen.softask.model.projection;

import com.fleencorp.feen.softask.model.domain.SoftAsk;
import com.fleencorp.feen.softask.model.domain.SoftAskParticipantDetail;

public record SoftAskWithDetail(SoftAsk softAsk, SoftAskParticipantDetail username) {}
