package com.fleencorp.feen.controller.social;

import com.fleencorp.base.resolver.SearchParam;
import com.fleencorp.feen.model.dto.social.block.BlockUserDto;
import com.fleencorp.feen.model.request.search.social.BlockUserSearchRequest;
import com.fleencorp.feen.model.response.social.block.BlockUserStatusResponse;
import com.fleencorp.feen.model.search.social.blocking.BlockingUserSearchResult;
import com.fleencorp.feen.model.security.FleenUser;
import com.fleencorp.feen.service.social.BlockUserService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/blocking")
public class BlockingController {

  private final BlockUserService blockUserService;

  public BlockingController(final BlockUserService blockUserService) {
    this.blockUserService = blockUserService;
  }

  @GetMapping(value = "/find-block-users")
  public BlockingUserSearchResult findBlockedUsers(
      @SearchParam final BlockUserSearchRequest blockUserSearchRequest,
      @AuthenticationPrincipal final FleenUser user) {
    return blockUserService.findBlockedUsers(blockUserSearchRequest, user);
  }

  @PutMapping(value = "/block-unblock")
  public BlockUserStatusResponse blockUnblock(
      @Valid @RequestBody final BlockUserDto blockUserDto,
      @AuthenticationPrincipal final FleenUser user) {
    return blockUserService.blockOrUnblockUser(blockUserDto, user);
  }

}
