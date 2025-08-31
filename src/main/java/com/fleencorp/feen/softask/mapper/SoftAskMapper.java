package com.fleencorp.feen.softask.mapper;

import com.fleencorp.feen.common.model.info.IsDeletedInfo;
import com.fleencorp.feen.softask.model.domain.SoftAsk;
import com.fleencorp.feen.softask.model.domain.SoftAskReply;
import com.fleencorp.feen.softask.model.domain.SoftAskVote;
import com.fleencorp.feen.softask.model.projection.SoftAskReplyWithDetail;
import com.fleencorp.feen.softask.model.projection.SoftAskWithDetail;
import com.fleencorp.feen.softask.model.response.reply.core.SoftAskReplyResponse;
import com.fleencorp.feen.softask.model.response.softask.core.SoftAskResponse;
import com.fleencorp.feen.softask.model.response.vote.core.SoftAskVoteResponse;

import java.util.Collection;

public interface SoftAskMapper {

  SoftAskResponse toSoftAskResponse(final SoftAsk entry);

  Collection<SoftAskResponse> toSoftAskResponses(final Collection<SoftAskWithDetail> entries);

  SoftAskReplyResponse toSoftAskReplyResponse(SoftAskReply entry);

  Collection<SoftAskReplyResponse> toSoftAskReplyResponses(Collection<SoftAskReplyWithDetail> entries);

  SoftAskVoteResponse toSoftAskVoteResponse(SoftAskVote entry, boolean voted);

  Collection<SoftAskVoteResponse> toSoftAskVoteResponses(Collection<SoftAskVote> entries);

  IsDeletedInfo toIsDeletedInfo(boolean isDeleted);
}
