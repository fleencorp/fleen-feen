package com.fleencorp.feen.model.request.stream;

import com.fleencorp.feen.constant.stream.StreamType;
import com.fleencorp.feen.model.domain.auth.Oauth2Authorization;
import com.fleencorp.feen.model.domain.calendar.Calendar;
import com.fleencorp.feen.model.domain.stream.FleenStream;

import static java.util.Objects.nonNull;

public record PatchStreamRequest(Calendar calendar, FleenStream stream, String title, String description, String location, Oauth2Authorization oauth2Authorization, StreamType streamType) {

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

  public static PatchStreamRequest of(final Calendar calendar, final FleenStream stream, final String title, final String description, final String location, final Oauth2Authorization oauth2Authorization, final StreamType streamType) {
    return new PatchStreamRequest(calendar, stream, title, description, location, oauth2Authorization, streamType);
  }
}
