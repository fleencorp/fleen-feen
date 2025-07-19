package com.fleencorp.feen.softask.service.softask;

import com.fleencorp.feen.softask.model.domain.SoftAsk;
import com.fleencorp.feen.softask.model.request.SoftAskSearchRequest;
import com.fleencorp.feen.softask.model.response.softask.SoftAskRetrieveResponse;
import com.fleencorp.feen.softask.model.search.SoftAskSearchResult;
import com.fleencorp.feen.user.model.security.RegisteredUser;

public interface SoftAskSearchService {

  SoftAsk findSoftAsk(Long softAskId);

  SoftAskRetrieveResponse retrieveSoftAsk(Long softAskId, RegisteredUser user);

  SoftAskSearchResult findSoftAsks(SoftAskSearchRequest searchRequest, RegisteredUser user);
}
