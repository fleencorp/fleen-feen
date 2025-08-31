package com.fleencorp.feen.business.service;

import com.fleencorp.feen.business.model.domain.Business;
import com.fleencorp.feen.user.model.domain.Member;

public interface BusinessOperationService {

  Business save(Business business);

  Business findBusiness(Long businessId);

  Business findBusinessAndVerifyOwner(Long businessId, Member member);
}
