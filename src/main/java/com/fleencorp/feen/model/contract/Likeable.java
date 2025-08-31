package com.fleencorp.feen.model.contract;

import com.fleencorp.feen.like.model.info.LikeCountInfo;
import com.fleencorp.feen.like.model.info.UserLikeInfo;

public interface Likeable extends HasId {

  void setUserLikeInfo(UserLikeInfo userLikeInfo);

  void setLikeCountInfo(LikeCountInfo likeCountInfo);
}
