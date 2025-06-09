package com.fleencorp.feen.user.mapper;

import com.fleencorp.feen.constant.security.profile.ProfileStatus;
import com.fleencorp.feen.user.model.info.ProfileStatusInfo;

public interface UserMapper {

  ProfileStatusInfo toProfileStatusInfo(ProfileStatus profileStatus);
}
