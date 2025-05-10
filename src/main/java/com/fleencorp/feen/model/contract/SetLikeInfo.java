package com.fleencorp.feen.model.contract;

import com.fleencorp.feen.model.info.like.LikeInfo;

public interface SetLikeInfo {

  Long getNumberId();

  void setUserLikeInfo(LikeInfo likeInfo);
}
