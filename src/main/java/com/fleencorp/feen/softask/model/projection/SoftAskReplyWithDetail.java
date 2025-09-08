package com.fleencorp.feen.softask.model.projection;

import com.fleencorp.feen.softask.model.domain.SoftAskParticipantDetail;
import com.fleencorp.feen.softask.model.domain.SoftAskReply;

public record SoftAskReplyWithDetail(SoftAskReply reply, SoftAskParticipantDetail username) {}
