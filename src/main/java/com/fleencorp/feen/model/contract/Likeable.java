package com.fleencorp.feen.model.contract;

import com.fleencorp.feen.model.info.like.UserLikeInfo;

public interface Likeable {

  Long getNumberId();

  void setUserLikeInfo(UserLikeInfo userLikeInfo);
}
