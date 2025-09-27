package com.fleencorp.feen.softask.mapper;

import com.fleencorp.feen.model.contract.UserHaveOtherDetail;
import com.fleencorp.feen.shared.member.contract.IsAMember;
import com.fleencorp.feen.softask.contract.SoftAskCommonResponse;
import com.fleencorp.feen.softask.model.projection.SoftAskWithDetail;
import com.fleencorp.feen.softask.model.response.softask.core.SoftAskResponse;

import java.util.Collection;

public interface SoftAskUnifiedMapper {

  void setLocationDetails(SoftAskCommonResponse response, UserHaveOtherDetail userHaveOtherDetail);

  Collection<SoftAskResponse> toSoftAskResponses(Collection<SoftAskWithDetail> entries, IsAMember member);
}
