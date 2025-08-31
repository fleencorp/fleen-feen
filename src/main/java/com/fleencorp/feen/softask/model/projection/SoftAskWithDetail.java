package com.fleencorp.feen.softask.model.projection;

import com.fleencorp.feen.softask.model.domain.SoftAsk;
import com.fleencorp.feen.softask.model.domain.SoftAskUsername;

public record SoftAskWithDetail(SoftAsk softAsk, SoftAskUsername username) {}
