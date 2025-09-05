package com.fleencorp.feen.like.service;

import com.fleencorp.feen.chat.space.exception.core.ChatSpaceNotFoundException;
import com.fleencorp.feen.common.exception.FailedOperationException;
import com.fleencorp.feen.like.model.dto.LikeDto;
import com.fleencorp.feen.like.model.response.LikeCreateResponse;
import com.fleencorp.feen.shared.security.RegisteredUser;
import com.fleencorp.feen.stream.exception.core.StreamNotFoundException;

public interface LikeService {

  LikeCreateResponse like(LikeDto likeDto, RegisteredUser user)
    throws ChatSpaceNotFoundException, StreamNotFoundException, FailedOperationException;
}
