package com.fleencorp.feen.common.service.misc;

import com.fleencorp.feen.calendar.model.domain.Calendar;
import com.fleencorp.feen.stream.constant.core.StreamType;
import com.fleencorp.feen.model.response.security.GetEncodedPasswordResponse;

public interface MiscService {

  GetEncodedPasswordResponse getEncodedPassword(String password);

  Calendar findCalendar(String countryTitle);

  Calendar findCalendarByStreamType(String countryTitle, StreamType streamType);
}
