package com.fleencorp.feen.softask.model.projection;

import com.fleencorp.feen.softask.model.domain.SoftAskReply;
import com.fleencorp.feen.softask.model.domain.SoftAskParticipantDetail;

public record SoftAskReplyWithDetail(SoftAskReply reply, SoftAskParticipantDetail username) {}
