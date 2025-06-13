package com.fleencorp.feen.model.holder;

import com.fleencorp.feen.calendar.model.domain.Calendar;
import com.fleencorp.feen.user.model.domain.Oauth2Authorization;

public record StreamOtherDetailsHolder(Calendar calendar, Oauth2Authorization oauth2Authorization) {

  public static StreamOtherDetailsHolder of(final Calendar calendar, final Oauth2Authorization auth) {
    return new StreamOtherDetailsHolder(calendar, auth);
  }
}
