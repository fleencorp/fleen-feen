package com.fleencorp.feen.softask.service.common;

import com.fleencorp.feen.softask.model.domain.SoftAsk;
import com.fleencorp.feen.softask.model.domain.SoftAskAnswer;
import com.fleencorp.feen.softask.model.domain.SoftAskReply;

public interface SoftAskOperationService {

  SoftAskAnswer save(SoftAskAnswer softAskAnswer);

  SoftAskReply save(SoftAskReply softAskReply);

  SoftAsk save(SoftAsk softAsk);

  Integer incrementSoftAskVoteAndGetVoteCount(Long softAskId);
  
  Integer decrementSoftAskVoteAndGetVoteCount(Long softAskId);

  Integer incrementSoftAskAnswerVoteAndGetVoteCount(Long softAskAnswerId);

  Integer decrementSoftAskAnswerVoteAndGetVoteCount(Long softAskAnswerId);

  Integer incrementSoftAskReplyVoteAndGetVoteCount(Long softAskReplyId);

  Integer decrementSoftAskReplyVoteAndGetVoteCount(Long softAskReplyId);

  Integer incrementSoftAskAnswerCountAndGetAnswerCount(Long softAskId);

  Integer incrementSoftAskAnswerReplyCountAndGetReplyCount(Long softAskAnswerId);
}
