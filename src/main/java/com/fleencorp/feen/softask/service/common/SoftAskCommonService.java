package com.fleencorp.feen.softask.service.common;

import com.fleencorp.feen.common.exception.FailedOperationException;
import com.fleencorp.feen.softask.contract.SoftAskCommonResponse;
import com.fleencorp.feen.softask.exception.core.SoftAskAnswerNotFoundException;
import com.fleencorp.feen.softask.exception.core.SoftAskReplyNotFoundException;
import com.fleencorp.feen.softask.exception.core.SoftAskUpdateDeniedException;
import com.fleencorp.feen.softask.model.dto.common.UpdateSoftAskContentDto;
import com.fleencorp.feen.softask.model.response.common.SoftAskContentUpdateResponse;
import com.fleencorp.feen.softask.model.response.softask.core.SoftAskResponse;
import com.fleencorp.feen.softask.model.search.SoftAskAnswerSearchResult;
import com.fleencorp.feen.user.model.domain.Member;
import com.fleencorp.feen.user.model.security.RegisteredUser;

import java.util.Collection;

public interface SoftAskCommonService {

  <T extends SoftAskCommonResponse> void processSoftAskResponses(Collection<T> softAskCommonResponses, Member member);

  SoftAskAnswerSearchResult findSomeSoftAskAnswersForSoftAsk(SoftAskResponse softAskResponse, Member member);

  SoftAskContentUpdateResponse updateSoftAskContent(Long softAskTypeId, UpdateSoftAskContentDto dto, RegisteredUser user)
    throws SoftAskAnswerNotFoundException, SoftAskReplyNotFoundException, SoftAskUpdateDeniedException,
    FailedOperationException;
}
