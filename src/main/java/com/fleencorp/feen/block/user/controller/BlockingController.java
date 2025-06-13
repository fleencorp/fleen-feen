package com.fleencorp.feen.block.user.controller;

import com.fleencorp.base.resolver.SearchParam;
import com.fleencorp.feen.block.user.model.dto.BlockUserDto;
import com.fleencorp.feen.block.user.model.request.search.BlockUserSearchRequest;
import com.fleencorp.feen.block.user.model.response.BlockUserStatusResponse;
import com.fleencorp.feen.block.user.model.search.BlockingUserSearchResult;
import com.fleencorp.feen.block.user.service.BlockUserService;
import com.fleencorp.feen.user.model.security.RegisteredUser;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/blocking")
@PreAuthorize("hasAnyRole('ADMINISTRATOR', 'SUPER_ADMINISTRATOR', 'USER')")
public class BlockingController {

  private final BlockUserService blockUserService;

  public BlockingController(final BlockUserService blockUserService) {
    this.blockUserService = blockUserService;
  }

  @GetMapping(value = "/find-block-users")
  public BlockingUserSearchResult findBlockedUsers(
      @SearchParam final BlockUserSearchRequest blockUserSearchRequest,
      @AuthenticationPrincipal final RegisteredUser user) {
    return blockUserService.findBlockedUsers(blockUserSearchRequest, user);
  }

  @PutMapping(value = "/block-unblock")
  public BlockUserStatusResponse blockUnblock(
      @Valid @RequestBody final BlockUserDto blockUserDto,
      @AuthenticationPrincipal final RegisteredUser user) {
    return blockUserService.blockOrUnblockUser(blockUserDto, user);
  }

}
