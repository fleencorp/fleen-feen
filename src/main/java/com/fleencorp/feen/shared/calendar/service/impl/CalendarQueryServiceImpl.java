package com.fleencorp.feen.shared.calendar.service.impl;

import com.fleencorp.feen.shared.calendar.contract.IsACalendar;
import com.fleencorp.feen.shared.calendar.service.CalendarQueryService;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CalendarQueryServiceImpl implements CalendarQueryService {

  private final EntityManager entityManager;

  public CalendarQueryServiceImpl(EntityManager entityManager) {
    this.entityManager = entityManager;
  }

  @Override
  @SuppressWarnings("unchecked")
  public Optional<IsACalendar> findDistinctByCodeIgnoreCase(String code) {
    List<IsACalendar> results = entityManager.createNativeQuery(
        """
        SELECT
            c.calendar_id AS calendarId,
            c.external_id AS externalId,
            c.title AS title,
            c.description AS description,
            c.timezone AS timezone,
            c.code AS code,
            c.is_active AS isActive
        FROM calendar c
        WHERE LOWER(c.code) = LOWER(:code)
        LIMIT 1
        """,
        IsACalendar.class
      )
      .setParameter("code", code)
      .getResultList();

    return results.stream().findFirst();
  }
}
