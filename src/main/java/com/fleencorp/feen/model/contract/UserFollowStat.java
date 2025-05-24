package com.fleencorp.feen.model.contract;

import com.fleencorp.feen.model.info.user.profile.TotalFollowedInfo;
import com.fleencorp.feen.model.info.user.profile.TotalFollowingInfo;

public interface UserFollowStat {

  void setTotalFollowedInfo(TotalFollowedInfo totalFollowedInfo);

  void setTotalFollowingInfo(TotalFollowingInfo totalFollowingInfo);

}
