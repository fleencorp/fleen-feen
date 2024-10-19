package com.fleencorp.feen.service.social;

import com.fleencorp.feen.model.dto.social.block.BlockUserDto;
import com.fleencorp.feen.model.request.search.social.BlockUserSearchRequest;
import com.fleencorp.feen.model.response.social.block.BlockUserStatusResponse;
import com.fleencorp.feen.model.search.social.blocking.BlockingUserSearchResult;
import com.fleencorp.feen.model.security.FleenUser;

public interface BlockUserService {

  BlockingUserSearchResult findBlockedUsers(BlockUserSearchRequest blockUserSearchRequest, FleenUser user);

  BlockUserStatusResponse blockOrUnblockUser(BlockUserDto blockUserDto, FleenUser user);
}
