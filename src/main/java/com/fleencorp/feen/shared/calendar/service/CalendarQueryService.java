package com.fleencorp.feen.shared.calendar.service;

import com.fleencorp.feen.shared.calendar.contract.IsACalendar;

import java.util.Optional;

public interface CalendarQueryService {

  @SuppressWarnings("unchecked")
  Optional<IsACalendar> findDistinctByCodeIgnoreCase(String code);
}
