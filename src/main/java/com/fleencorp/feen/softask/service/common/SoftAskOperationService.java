package com.fleencorp.feen.softask.service.common;

import com.fleencorp.feen.softask.contract.SoftAskCommonData;
import com.fleencorp.feen.softask.model.domain.SoftAsk;
import com.fleencorp.feen.softask.model.domain.SoftAskReply;
import com.fleencorp.feen.softask.model.domain.SoftAskParticipantDetail;

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

  SoftAskParticipantDetail generateParticipantDetail(Long softAskId, Long userId);

  SoftAskParticipantDetail getOrAssignUsername(Long softAskId, Long userId);

  void setGeoHashAndGeoPrefix(SoftAskCommonData softAskCommonData);
}
