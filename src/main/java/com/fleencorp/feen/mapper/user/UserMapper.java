package com.fleencorp.feen.mapper.user;

import com.fleencorp.feen.constant.security.profile.ProfileStatus;
import com.fleencorp.feen.model.info.user.ProfileStatusInfo;

public interface UserMapper {

  ProfileStatusInfo toProfileStatusInfo(ProfileStatus profileStatus);
}
