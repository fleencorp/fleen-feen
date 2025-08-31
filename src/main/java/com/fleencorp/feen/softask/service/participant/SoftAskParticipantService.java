package com.fleencorp.feen.softask.service.participant;

import com.fleencorp.feen.softask.model.response.user.SoftAskUserProfileRetrieveResponse;
import com.fleencorp.feen.user.model.security.RegisteredUser;

public interface SoftAskParticipantService {

  SoftAskUserProfileRetrieveResponse findUserProfile(RegisteredUser user);
}
