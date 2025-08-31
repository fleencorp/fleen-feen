package com.fleencorp.feen.softask.mapper;

import com.fleencorp.feen.model.contract.UserHaveOtherDetail;
import com.fleencorp.feen.softask.contract.SoftAskCommonResponse;

public interface UserLocationMapper {

  void setLocationDetails(SoftAskCommonResponse response, UserHaveOtherDetail userHaveOtherDetail);
}
