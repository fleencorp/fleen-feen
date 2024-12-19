package com.fleencorp.feen.model.request.stream;

import com.fleencorp.feen.constant.stream.StreamType;
import com.fleencorp.feen.model.domain.auth.Oauth2Authorization;
import com.fleencorp.feen.model.domain.calendar.Calendar;
import com.fleencorp.feen.model.domain.stream.FleenStream;
import com.fleencorp.feen.model.dto.event.CreateCalendarEventDto;
import com.fleencorp.feen.model.dto.livebroadcast.CreateLiveBroadcastDto;

import static com.fleencorp.feen.model.dto.event.CreateCalendarEventDto.EventAttendeeOrGuest;
import static java.util.Objects.nonNull;

public record CreateStreamRequest(Calendar calendar, FleenStream stream, EventAttendeeOrGuest attendeeOrGuest,
                                  String userEmailAddress, StreamType streamType, CreateCalendarEventDto createEventDto,
                                  CreateLiveBroadcastDto createLiveBroadcastDto, Oauth2Authorization oauth2Authorization) {

  public String calendarExternalId() {
    return nonNull(calendar) ? calendar.getExternalId() : null;
  }

  public String accessToken() {
    return nonNull(oauth2Authorization) ? oauth2Authorization.getAccessToken() : null;
  }

  public boolean isABroadcast() {
    return StreamType.isLiveStream(streamType);
  }

  public boolean isAnEvent() {
    return StreamType.isEvent(streamType);
  }

  public static CreateStreamRequest of(final Calendar calendar, final FleenStream stream, final EventAttendeeOrGuest attendeeOrGuest, final String userEmailAddress, final StreamType streamType, final CreateCalendarEventDto createEventDto) {
    return new CreateStreamRequest(calendar, stream, attendeeOrGuest, userEmailAddress, streamType, createEventDto, null, null);
  }

  public static CreateStreamRequest of(final FleenStream stream, final StreamType streamType, final CreateLiveBroadcastDto createLiveBroadcastDto, final Oauth2Authorization oauth2Authorization) {
    return new CreateStreamRequest(null, stream, null, null, streamType, null, createLiveBroadcastDto, oauth2Authorization);
  }
}
