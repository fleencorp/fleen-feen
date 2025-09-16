package com.fleencorp.feen.softask.service.softask;

import com.fleencorp.feen.shared.security.RegisteredUser;
import com.fleencorp.feen.softask.model.request.SoftAskSearchRequest;
import com.fleencorp.feen.softask.model.search.SoftAskSearchResult;

public interface TrendingSoftAskService {

  SoftAskSearchResult trendingSoftAsks(SoftAskSearchRequest request, RegisteredUser user);
}
