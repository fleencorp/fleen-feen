package com.fleencorp.feen.block.user.service;

import com.fleencorp.feen.block.user.model.dto.BlockUserDto;
import com.fleencorp.feen.block.user.model.request.search.BlockUserSearchRequest;
import com.fleencorp.feen.block.user.model.response.BlockUserStatusResponse;
import com.fleencorp.feen.block.user.model.search.BlockingUserSearchResult;
import com.fleencorp.feen.user.model.domain.Member;
import com.fleencorp.feen.user.model.security.RegisteredUser;

public interface BlockUserService {

  BlockingUserSearchResult findBlockedUsers(BlockUserSearchRequest blockUserSearchRequest, RegisteredUser user);

  BlockUserStatusResponse blockOrUnblockUser(BlockUserDto blockUserDto, RegisteredUser user);

  boolean existsByInitiatorAndRecipient(Member viewer, Member target);
}
