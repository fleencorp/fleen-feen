package com.fleencorp.feen.block.user.mapper;

import com.fleencorp.feen.block.user.model.domain.BlockUser;
import com.fleencorp.feen.block.user.model.response.BlockUserResponse;

import java.util.Collection;

public interface BlockUserMapper {

  BlockUserResponse toBlockUserResponse(BlockUser entry);

  Collection<BlockUserResponse> toBlockUserResponse(Collection<BlockUser> entries);
}
