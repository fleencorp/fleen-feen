package com.fleencorp.feen.follower.controller;

import com.fleencorp.base.model.request.search.SearchRequest;
import com.fleencorp.base.resolver.SearchParam;
import com.fleencorp.feen.follower.model.dto.FollowOrUnfollowUserDto;
import com.fleencorp.feen.follower.model.response.FollowUserResponse;
import com.fleencorp.feen.follower.model.response.UnfollowUserResponse;
import com.fleencorp.feen.follower.model.search.FollowerSearchResult;
import com.fleencorp.feen.follower.model.search.FollowingSearchResult;
import com.fleencorp.feen.follower.service.FollowerService;
import com.fleencorp.feen.user.model.security.RegisteredUser;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/follower")
@PreAuthorize("hasAnyRole('ADMINISTRATOR', 'SUPER_ADMINISTRATOR', 'USER')")
public class FollowerController {

  private final FollowerService followerService;

  public FollowerController(final FollowerService followerService) {
    this.followerService = followerService;
  }

  @GetMapping(value = "/get-followers")
  public FollowerSearchResult getFollowers(
      @SearchParam final SearchRequest searchRequest,
      @AuthenticationPrincipal final RegisteredUser user) {
    return followerService.getFollowers(searchRequest, user);
  }

  @GetMapping(value = "/get-followings")
  public FollowingSearchResult getFollowings(
      @SearchParam final SearchRequest searchRequest,
      @AuthenticationPrincipal final RegisteredUser user) {
    return followerService.getFollowings(searchRequest, user);
  }

  @PutMapping(value = "/follow")
  public FollowUserResponse followUser(
      @Valid @RequestBody final FollowOrUnfollowUserDto followUserDto,
      @AuthenticationPrincipal final RegisteredUser user) {
    return followerService.followUser(followUserDto, user);
  }

  @PutMapping(value = "/unfollow")
  public UnfollowUserResponse unfollowUser(
      @Valid @RequestBody final FollowOrUnfollowUserDto unfollowUserDto,
      @AuthenticationPrincipal final RegisteredUser user) {
    return followerService.unfollowUser(unfollowUserDto, user);
  }

}
