package com.fleencorp.feen.softask.service.reply;

import com.fleencorp.feen.softask.exception.core.SoftAskReplyNotFoundException;
import com.fleencorp.feen.softask.model.domain.SoftAskReply;
import com.fleencorp.feen.softask.model.request.SoftAskSearchRequest;
import com.fleencorp.feen.softask.model.response.reply.SoftAskReplyRetrieveResponse;
import com.fleencorp.feen.softask.model.search.SoftAskReplySearchResult;
import com.fleencorp.feen.user.model.domain.Member;
import com.fleencorp.feen.user.model.security.RegisteredUser;

public interface SoftAskReplySearchService {

  SoftAskReplyRetrieveResponse retrieveSoftAskReply(SoftAskSearchRequest searchRequest, Long softAskId, Long softAskReplyId) throws SoftAskReplyNotFoundException;

  SoftAskReply findSoftAskReply(Long softAskId, Long softAskReplyId) throws SoftAskReplyNotFoundException;

  SoftAskReplySearchResult findSoftAskReplies(SoftAskSearchRequest searchRequest, Member member);

  SoftAskReplySearchResult findSoftAskReplies(SoftAskSearchRequest searchRequest, RegisteredUser user);
}
