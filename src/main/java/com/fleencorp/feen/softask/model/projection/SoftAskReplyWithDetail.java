package com.fleencorp.feen.softask.model.projection;

import com.fleencorp.feen.softask.model.domain.SoftAskReply;
import com.fleencorp.feen.softask.model.domain.SoftAskUsername;

public record SoftAskReplyWithDetail(SoftAskReply reply, SoftAskUsername username) {}
