package com.fleencorp.feen.user.service.profile;

import com.fleencorp.feen.shared.security.RegisteredUser;
import com.fleencorp.feen.user.exception.member.MemberNotFoundException;
import com.fleencorp.feen.user.model.response.UserProfileResponse;
import com.fleencorp.feen.user.model.search.UserProfileSearchRequest;

public interface UserProfilePublicService {

  UserProfileResponse getUserProfile(UserProfileSearchRequest userProfileSearchRequest, RegisteredUser user) throws MemberNotFoundException;
}
