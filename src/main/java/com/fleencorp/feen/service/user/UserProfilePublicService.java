package com.fleencorp.feen.service.user;

import com.fleencorp.feen.exception.member.MemberNotFoundException;
import com.fleencorp.feen.model.request.search.UserProfileSearchRequest;
import com.fleencorp.feen.model.response.user.profile.UserProfileResponse;
import com.fleencorp.feen.model.security.FleenUser;

public interface UserProfilePublicService {

  UserProfileResponse getUserProfile(UserProfileSearchRequest userProfileSearchRequest, FleenUser user) throws MemberNotFoundException;
}
