package com.fleencorp.feen.softask.service.common;

import com.fleencorp.feen.common.exception.FailedOperationException;
import com.fleencorp.feen.shared.member.contract.IsAMember;
import com.fleencorp.feen.model.contract.UserHaveOtherDetail;
import com.fleencorp.feen.softask.contract.SoftAskCommonResponse;
import com.fleencorp.feen.softask.exception.core.SoftAskReplyNotFoundException;
import com.fleencorp.feen.softask.exception.core.SoftAskUpdateDeniedException;
import com.fleencorp.feen.softask.model.dto.common.UpdateSoftAskContentDto;
import com.fleencorp.feen.softask.model.request.SoftAskSearchRequest;
import com.fleencorp.feen.softask.model.response.common.SoftAskContentUpdateResponse;
import com.fleencorp.feen.softask.model.response.softask.core.SoftAskResponse;
import com.fleencorp.feen.softask.model.search.SoftAskReplySearchResult;
import com.fleencorp.feen.user.model.security.RegisteredUser;

import java.util.Collection;

public interface SoftAskCommonService {

  <T extends SoftAskCommonResponse> void processSoftAskResponses(Collection<T> softAskCommonResponses, IsAMember member, UserHaveOtherDetail userHaveOtherDetail);

  SoftAskReplySearchResult findSomeSoftAskRepliesForSoftAsk(SoftAskSearchRequest searchRequest, SoftAskResponse softAskResponse, IsAMember member);

  SoftAskContentUpdateResponse updateSoftAskContent(UpdateSoftAskContentDto dto, RegisteredUser user)
    throws SoftAskReplyNotFoundException, SoftAskUpdateDeniedException,
    FailedOperationException;
}
