package com.fleencorp.feen.shared.calendar.contract;

public interface IsACalendar {

  Long getCalendarId();

  String getExternalId();

  String getTitle();

  String getDescription();

  String getTimezone();

  String getCode();
}
