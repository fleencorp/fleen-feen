package com.fleencorp.feen.business.service;

import com.fleencorp.feen.business.model.domain.Business;
import com.fleencorp.feen.business.model.request.search.BusinessSearchRequest;
import com.fleencorp.feen.business.model.search.BusinessSearchResult;
import com.fleencorp.feen.user.model.domain.Member;
import com.fleencorp.feen.shared.security.RegisteredUser;

public interface BusinessSearchService {

  BusinessSearchResult findBusinesses(BusinessSearchRequest searchRequest, RegisteredUser user);

  Business findBusiness(Long businessId);

  Business findBusinessAndVerifyOwnerRequired(final Long businessId, final Member member);
}
