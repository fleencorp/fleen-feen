package com.fleencorp.feen.user.controller.profile;

import com.fleencorp.feen.user.model.search.UserProfileSearchRequest;
import com.fleencorp.feen.user.model.response.UserProfileResponse;
import com.fleencorp.feen.user.model.security.RegisteredUser;
import com.fleencorp.feen.user.service.profile.UserProfilePublicService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/user")
public class UserProfilePublicController {

  private final UserProfilePublicService userProfilePublicService;

  public UserProfilePublicController(final UserProfilePublicService userProfilePublicService) {
    this.userProfilePublicService = userProfilePublicService;
  }

  @GetMapping(value = "/profile/{userId}")
  public UserProfileResponse getUserProfile(@PathVariable(name ="userId") final String targetUserId, @AuthenticationPrincipal final RegisteredUser user) {
      final UserProfileSearchRequest userProfileSearchRequest = UserProfileSearchRequest.of(targetUserId);
    return userProfilePublicService.getUserProfile(userProfileSearchRequest, user);
  }
}
