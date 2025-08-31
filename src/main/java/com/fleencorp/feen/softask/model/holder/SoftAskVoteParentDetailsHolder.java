package com.fleencorp.feen.softask.model.holder;

import com.fleencorp.feen.softask.model.domain.SoftAsk;
import com.fleencorp.feen.softask.model.domain.SoftAskReply;

import java.util.Optional;

public record SoftAskVoteParentDetailsHolder(SoftAsk softAsk, SoftAskReply softAskReply) {

  public Long getSoftAskId() {
    return Optional.of(softAsk)
      .map(SoftAsk::getSoftAskId)
      .orElse(null);
  }

  public Long getSoftAskReplyId() {
    return Optional.ofNullable(softAskReply)
      .map(SoftAskReply::getSoftAskReplyId)
      .orElse(null);
  }

  public static SoftAskVoteParentDetailsHolder of(final SoftAsk softAsk, final SoftAskReply softAskReply) {
    return new SoftAskVoteParentDetailsHolder(softAsk, softAskReply);
  }
}
