package com.fleencorp.feen.softask.service.participant;

import com.fleencorp.feen.shared.security.RegisteredUser;
import com.fleencorp.feen.softask.model.response.user.SoftAskUserProfileRetrieveResponse;

public interface SoftAskParticipantService {

  SoftAskUserProfileRetrieveResponse findUserProfile(RegisteredUser user);
}
