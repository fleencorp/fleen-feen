package com.fleencorp.feen.softask.mapper.impl;

import com.fleencorp.feen.model.contract.UserHaveOtherDetail;
import com.fleencorp.feen.shared.member.contract.IsAMember;
import com.fleencorp.feen.softask.contract.SoftAskCommonResponse;
import com.fleencorp.feen.softask.mapper.SoftAskMapper;
import com.fleencorp.feen.softask.mapper.SoftAskUnifiedMapper;
import com.fleencorp.feen.softask.mapper.UserLocationMapper;
import com.fleencorp.feen.softask.model.projection.SoftAskWithDetail;
import com.fleencorp.feen.softask.model.response.softask.core.SoftAskResponse;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
public class SoftAskUnifiedMapperImpl implements SoftAskUnifiedMapper {

  private final SoftAskMapper softAskMapper;
  private final UserLocationMapper userLocationMapper;

  public SoftAskUnifiedMapperImpl(
      final SoftAskMapper softAskMapper,
      final UserLocationMapper userLocationMapper) {
    this.softAskMapper = softAskMapper;
    this.userLocationMapper = userLocationMapper;
  }

  @Override
  public void setLocationDetails(SoftAskCommonResponse response, UserHaveOtherDetail userHaveOtherDetail) {
    userLocationMapper.setLocationDetails(response, userHaveOtherDetail);
  }

  @Override
  public Collection<SoftAskResponse> toSoftAskResponses(Collection<SoftAskWithDetail> entries, IsAMember member) {
    return softAskMapper.toSoftAskResponses(entries, member);
  }
}
