package com.fleencorp.feen.service.share;

import com.fleencorp.base.model.view.search.SearchResultView;
import com.fleencorp.feen.model.dto.share.BlockUserDto;
import com.fleencorp.feen.model.request.search.share.BlockUserSearchRequest;
import com.fleencorp.feen.model.response.share.BlockUserStatusResponse;
import com.fleencorp.feen.model.security.FleenUser;

public interface BlockedUserService {

  SearchResultView findBlockedUsers(FleenUser user, BlockUserSearchRequest blockUserSearchRequest);

  BlockUserStatusResponse blockOrUnblockUser(FleenUser user, BlockUserDto blockUserDto);
}
