package com.fleencorp.feen.softask.model.holder;

import com.fleencorp.feen.softask.model.domain.SoftAsk;
import com.fleencorp.feen.softask.model.domain.SoftAskAnswer;
import com.fleencorp.feen.softask.model.domain.SoftAskReply;

import java.util.Optional;

public record SoftAskVoteParentDetailsHolder(SoftAsk softAsk, SoftAskAnswer softAskAnswer, SoftAskReply softAskReply) {

  public Long parentId() {
    return Optional.ofNullable(softAsk)
      .map(SoftAsk::getSoftAskId)
      .or(() -> Optional.ofNullable(softAskAnswer).map(SoftAskAnswer::getSoftAskAnswerId))
      .or(() -> Optional.ofNullable(softAskReply).map(SoftAskReply::getSoftAskReplyId))
      .orElse(null);
  }
  public static SoftAskVoteParentDetailsHolder of(final SoftAsk softAsk, final SoftAskAnswer softAskAnswer, final SoftAskReply softAskReply) {
    return new SoftAskVoteParentDetailsHolder(softAsk, softAskAnswer, softAskReply);
  }
}
