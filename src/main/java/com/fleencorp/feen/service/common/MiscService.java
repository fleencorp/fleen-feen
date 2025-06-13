package com.fleencorp.feen.service.common;

import com.fleencorp.feen.calendar.model.domain.Calendar;
import com.fleencorp.feen.constant.stream.StreamType;
import com.fleencorp.feen.model.response.security.GetEncodedPasswordResponse;

public interface MiscService {

  GetEncodedPasswordResponse getEncodedPassword(String password);

  Calendar findCalendar(String countryTitle);

  Calendar findCalendarByStreamType(String countryTitle, StreamType streamType);
}
