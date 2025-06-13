package com.fleencorp.feen.user.service.profile;

import com.fleencorp.feen.user.exception.MemberNotFoundException;
import com.fleencorp.feen.user.model.response.UserProfileResponse;
import com.fleencorp.feen.user.model.search.UserProfileSearchRequest;
import com.fleencorp.feen.user.model.security.RegisteredUser;

public interface UserProfilePublicService {

  UserProfileResponse getUserProfile(UserProfileSearchRequest userProfileSearchRequest, RegisteredUser user) throws MemberNotFoundException;
}
