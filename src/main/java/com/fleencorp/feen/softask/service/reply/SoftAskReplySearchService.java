package com.fleencorp.feen.softask.service.reply;

import com.fleencorp.feen.shared.member.contract.IsAMember;
import com.fleencorp.feen.shared.security.RegisteredUser;
import com.fleencorp.feen.softask.exception.core.SoftAskReplyNotFoundException;
import com.fleencorp.feen.softask.model.domain.SoftAskReply;
import com.fleencorp.feen.softask.model.request.SoftAskSearchRequest;
import com.fleencorp.feen.softask.model.response.reply.SoftAskReplyRetrieveResponse;
import com.fleencorp.feen.softask.model.search.SoftAskReplySearchResult;

public interface SoftAskReplySearchService {

  SoftAskReplyRetrieveResponse retrieveSoftAskReply(SoftAskSearchRequest searchRequest, Long softAskReplyId, RegisteredUser user) throws SoftAskReplyNotFoundException;

  SoftAskReply findSoftAskReply(Long softAskId, Long softAskReplyId) throws SoftAskReplyNotFoundException;

  SoftAskReplySearchResult findSoftAskReplies(SoftAskSearchRequest searchRequest, IsAMember member);

  SoftAskReplySearchResult findSoftAskReplies(SoftAskSearchRequest searchRequest, RegisteredUser user);
}
