package com.fleencorp.feen.softask.service.common;

import com.fleencorp.feen.softask.model.domain.SoftAsk;
import com.fleencorp.feen.softask.model.domain.SoftAskReply;

public interface SoftAskOperationService {

  SoftAskReply save(SoftAskReply softAskReply);

  SoftAsk save(SoftAsk softAsk);

  SoftAsk findSoftAsk(Long softAskId);

  SoftAskReply findSoftAskReply(Long softAskId, Long softAskReplyId);

  Integer updateVoteCount(Long softAskId, boolean isVoted);

  Integer updateVoteCount(Long softAskId, Long softAskReplyId, boolean isVoted);

  Integer incrementSoftAskReplyCountAndGetReplyCount(Long softAskId);

  Integer incrementSoftAskReplyChildReplyCountAndGetReplyCount(Long softAskId, Long softAskReplyParentId);

  Integer updateBookmarkCount(Long softAskId, boolean increment);

  Integer updateBookmarkCount(Long softAskId, Long softAskReplyId, boolean increment);
}
