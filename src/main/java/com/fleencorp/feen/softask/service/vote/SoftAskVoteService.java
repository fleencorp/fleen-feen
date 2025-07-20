package com.fleencorp.feen.softask.service.vote;

import com.fleencorp.feen.common.exception.FailedOperationException;
import com.fleencorp.feen.chat.space.exception.core.ChatSpaceNotFoundException;
import com.fleencorp.feen.stream.exception.core.StreamNotFoundException;
import com.fleencorp.feen.softask.model.dto.vote.SoftAskVoteDto;
import com.fleencorp.feen.softask.model.response.vote.SoftAskVoteUpdateResponse;
import com.fleencorp.feen.user.model.security.RegisteredUser;

public interface SoftAskVoteService {

  SoftAskVoteUpdateResponse vote(SoftAskVoteDto softAskVoteDto, RegisteredUser user)
    throws StreamNotFoundException, ChatSpaceNotFoundException, FailedOperationException;

  Integer countUserSoftAskAnswerVotes(Long memberId);

  Integer countUserSoftAskReplyVotes(Long memberId);

  Integer countUserSoftAskVotes(Long memberId);
}
