package com.fleencorp.feen.service.share;

import com.fleencorp.base.model.view.search.SearchResultView;
import com.fleencorp.feen.model.dto.share.BlockUserDto;
import com.fleencorp.feen.model.request.search.share.BlockUserSearchRequest;
import com.fleencorp.feen.model.response.share.block.BlockUserStatusResponse;
import com.fleencorp.feen.model.security.FleenUser;

public interface BlockedUserService {

  SearchResultView findBlockedUsers(BlockUserSearchRequest blockUserSearchRequest, FleenUser user);

  BlockUserStatusResponse blockOrUnblockUser(BlockUserDto blockUserDto, FleenUser user);
}
