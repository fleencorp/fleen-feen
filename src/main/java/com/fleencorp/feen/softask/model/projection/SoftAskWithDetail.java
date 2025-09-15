package com.fleencorp.feen.softask.model.projection;

import com.fleencorp.feen.softask.model.domain.SoftAsk;
import com.fleencorp.feen.softask.model.domain.SoftAskParticipantDetail;

public record SoftAskWithDetail(
  SoftAsk softAsk,
  Long participantId,
  String username,
  String displayName,
  String avatar,
  Double distance) {

  public SoftAskParticipantDetail participantDetail() {
    SoftAskParticipantDetail participantDetail = SoftAskParticipantDetail.of(username, displayName, avatar);
    softAsk.setParticipant(participantDetail);

    return participantDetail;
  }
}
