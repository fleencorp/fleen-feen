package com.fleencorp.feen.model.contract;

import com.fleencorp.feen.like.model.info.UserLikeInfo;

public interface Likeable {

  Long getNumberId();

  void setUserLikeInfo(UserLikeInfo userLikeInfo);
}
