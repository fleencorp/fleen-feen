package com.fleencorp.feen.repository.calendar;

import com.fleencorp.feen.model.domain.calendar.Calendar;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface CalendarRepository extends JpaRepository<Calendar, Long> {

  @Query("SELECT cal FROM Calendar cal WHERE cal.createdOn BETWEEN :startDate AND :endDate ORDER BY cal.updatedOn DESC")
  Page<Calendar> findByDateBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate, Pageable pageable);

  Page<Calendar> findByTitle(@Param("title") String title, Pageable pageable);

  @Query("SELECT cal FROM Calendar cal WHERE cal.calendarId IS NOT NULL ORDER BY cal.updatedOn DESC")
  Page<Calendar> findMany(Pageable pageable);

  Optional<Calendar> findDistinctByCodeIgnoreCase(@Param("code") String code);
}
