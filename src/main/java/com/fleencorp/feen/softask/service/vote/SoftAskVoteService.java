package com.fleencorp.feen.softask.service.vote;

import com.fleencorp.feen.common.exception.FailedOperationException;
import com.fleencorp.feen.shared.security.RegisteredUser;
import com.fleencorp.feen.softask.exception.core.SoftAskNotFoundException;
import com.fleencorp.feen.softask.exception.core.SoftAskReplyNotFoundException;
import com.fleencorp.feen.softask.model.dto.vote.SoftAskVoteDto;
import com.fleencorp.feen.softask.model.response.vote.SoftAskVoteUpdateResponse;

public interface SoftAskVoteService {

  SoftAskVoteUpdateResponse vote(SoftAskVoteDto softAskVoteDto, RegisteredUser user)
    throws SoftAskNotFoundException, SoftAskReplyNotFoundException, FailedOperationException;

  Integer countUserSoftAskReplyVotes(Long memberId);

  Integer countUserSoftAskVotes(Long memberId);
}
