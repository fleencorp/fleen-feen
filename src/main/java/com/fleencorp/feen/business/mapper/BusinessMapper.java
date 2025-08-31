package com.fleencorp.feen.business.mapper;

import com.fleencorp.feen.business.model.domain.Business;
import com.fleencorp.feen.business.model.response.core.BusinessResponse;

import java.util.Collection;

public interface BusinessMapper {

  BusinessResponse toBusinessResponse(Business entry);

  Collection<BusinessResponse> toBusinessResponses(Collection<Business> entries);
}
