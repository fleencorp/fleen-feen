package com.fleencorp.feen.service.like;

import com.fleencorp.feen.exception.base.FailedOperationException;
import com.fleencorp.feen.exception.chat.space.ChatSpaceNotFoundException;
import com.fleencorp.feen.exception.stream.StreamNotFoundException;
import com.fleencorp.feen.model.contract.SetLikeInfo;
import com.fleencorp.feen.model.domain.user.Member;
import com.fleencorp.feen.model.dto.like.LikeDto;
import com.fleencorp.feen.model.info.like.LikeInfo;
import com.fleencorp.feen.model.response.like.LikeResponse;
import com.fleencorp.feen.model.security.FleenUser;

import java.util.List;

public interface LikeService {

  LikeResponse like(LikeDto likeDto, FleenUser user) throws StreamNotFoundException, ChatSpaceNotFoundException, FailedOperationException;

  LikeInfo findChatSpaceLikeByMember(Long parentId, Member member);

  LikeInfo findStreamLikeByMember(Long parentId, Member member);

  void setUserLikeInfo(SetLikeInfo setLikeInfo, FleenUser user);

  void setUserLikeInfo(final List<? extends SetLikeInfo> setLikeInfos, FleenUser user);
}
