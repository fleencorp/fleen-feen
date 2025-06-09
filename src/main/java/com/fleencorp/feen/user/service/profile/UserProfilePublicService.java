package com.fleencorp.feen.user.service.profile;

import com.fleencorp.feen.user.exception.MemberNotFoundException;
import com.fleencorp.feen.model.request.search.UserProfileSearchRequest;
import com.fleencorp.feen.user.model.response.UserProfileResponse;
import com.fleencorp.feen.model.security.FleenUser;

public interface UserProfilePublicService {

  UserProfileResponse getUserProfile(UserProfileSearchRequest userProfileSearchRequest, FleenUser user) throws MemberNotFoundException;
}
