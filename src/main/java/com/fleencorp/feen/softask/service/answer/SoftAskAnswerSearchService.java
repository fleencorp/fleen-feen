package com.fleencorp.feen.softask.service.answer;

import com.fleencorp.feen.softask.exception.core.SoftAskAnswerNotFoundException;
import com.fleencorp.feen.softask.model.domain.SoftAskAnswer;
import com.fleencorp.feen.softask.model.request.SoftAskSearchRequest;
import com.fleencorp.feen.softask.model.search.SoftAskAnswerSearchResult;
import com.fleencorp.feen.user.model.domain.Member;
import com.fleencorp.feen.user.model.security.RegisteredUser;

public interface SoftAskAnswerSearchService {

  SoftAskAnswer findSoftAskAnswer(Long softAskAnswerId) throws SoftAskAnswerNotFoundException;

  SoftAskAnswerSearchResult findSoftAskAnswers(SoftAskSearchRequest searchRequest, Member member);

  SoftAskAnswerSearchResult findSoftAskAnswers(SoftAskSearchRequest searchRequest, RegisteredUser user);
}
