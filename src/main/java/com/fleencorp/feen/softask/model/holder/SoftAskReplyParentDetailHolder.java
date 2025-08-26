package com.fleencorp.feen.softask.model.holder;

import com.fleencorp.feen.softask.model.domain.SoftAsk;
import com.fleencorp.feen.softask.model.domain.SoftAskReply;

public record SoftAskReplyParentDetailHolder(SoftAsk softAsk, SoftAskReply softAskReply) {

  public static SoftAskReplyParentDetailHolder of(final SoftAsk softAsk, final SoftAskReply softAskReply) {
    return new SoftAskReplyParentDetailHolder(softAsk, softAskReply);
  }
}
