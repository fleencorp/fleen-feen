package com.fleencorp.feen.service.common;

import com.fleencorp.feen.model.domain.calendar.Calendar;
import com.fleencorp.feen.model.domain.user.Member;
import com.fleencorp.feen.model.response.security.GetEncodedPasswordResponse;
import com.fleencorp.feen.model.security.FleenUser;

public interface MiscService {

  GetEncodedPasswordResponse getEncodedPassword(String password);

  Calendar findCalendar(final String countryTitle);

  void verifyIfUserIsAuthorOrCreatorOrOwnerTryingToPerformAction(Member owner, FleenUser user);
}
