package com.fleencorp.feen.model.request.stream;

import com.fleencorp.feen.constant.stream.StreamType;
import com.fleencorp.feen.model.domain.auth.Oauth2Authorization;
import com.fleencorp.feen.model.domain.calendar.Calendar;
import com.fleencorp.feen.model.domain.stream.FleenStream;

import static java.util.Objects.nonNull;

public record UpdateStreamVisibilityRequest(Calendar calendar, FleenStream stream, Oauth2Authorization oauth2Authorization, String visibility, StreamType streamType) {

  public String calendarExternalId() {
    return nonNull(calendar) ? calendar.getExternalId() : null;
  }

  public String streamExternalId() {
    return nonNull(stream) ? stream.getExternalId() : null;
  }

  public String accessToken() {
    return nonNull(oauth2Authorization) ? oauth2Authorization.getAccessToken() : null;
  }

  public boolean isAnEvent() {
    return StreamType.isEvent(streamType);
  }

  public boolean isABroadcast() {
    return StreamType.isLiveStream(streamType);
  }

  public static UpdateStreamVisibilityRequest of(final Calendar calendar, final FleenStream stream, final Oauth2Authorization oauth2Authorization, final String visibility, final StreamType streamType) {
    return new UpdateStreamVisibilityRequest(calendar, stream, oauth2Authorization, visibility, streamType);
  }
}