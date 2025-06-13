package com.fleencorp.feen.like.controller;

import com.fleencorp.feen.like.model.dto.LikeDto;
import com.fleencorp.feen.like.model.response.LikeResponse;
import com.fleencorp.feen.like.service.LikeService;
import com.fleencorp.feen.user.model.security.RegisteredUser;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/like")
public class LikeController {

  private final LikeService likeService;

  public LikeController(final LikeService likeService) {
    this.likeService = likeService;
  }

  @PreAuthorize("isFullyAuthenticated()")
  @PostMapping(value = "")
  public LikeResponse like(
      @Valid @RequestBody final LikeDto likeDto,
      @AuthenticationPrincipal final RegisteredUser user) {
    return likeService.like(likeDto, user);
  }
}
