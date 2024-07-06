package com.fleencorp.feen.repository.stream;

import com.fleencorp.feen.model.domain.stream.FleenStream;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface FleenStreamRepository extends JpaRepository<FleenStream, Long> {

  @Query(value = "SELECT fs FROM FleenStream fs WHERE fs.createdOn BETWEEN :startDate AND :endDate ORDER BY fs.updatedOn DESC")
  Page<FleenStream> findByDateBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate, Pageable pageable);

  Page<FleenStream> findByTitle(@Param("title") String title, Pageable pageable);

  @Query("SELECT fs FROM FleenStream fs WHERE fs.fleenStreamId IS NOT NULL ORDER BY fs.updatedOn DESC")
  Page<FleenStream> findMany(Pageable pageable);

  @Query(value = "SELECT fs FROM FleenStream fs WHERE fs.scheduledStartDate > :currentDate ORDER BY fs.scheduledStartDate ASC")
  Page<FleenStream> findUpcomingEvents(@Param("currentDate") LocalDateTime dateTime, Pageable pageable);

  @Query(value = "SELECT fs FROM FleenStream fs WHERE fs.scheduledStartDate > :currentDate AND LOWER(fs.title) " +
          "LIKE LOWER(CONCAT('%', :title, '%')) ORDER BY fs.scheduledStartDate ASC")
  Page<FleenStream> findUpcomingEventsByTitle(@Param("title") String title, @Param("currentDate") LocalDateTime dateTime, Pageable pageable);

  @Query(value = "SELECT fs FROM FleenStream fs WHERE fs.scheduledStartDate < :currentDate ORDER BY fs.scheduledStartDate ASC")
  Page<FleenStream> findPastEvents(@Param("currentDate") LocalDateTime dateTime, Pageable pageable);

  @Query(value = "SELECT fs FROM FleenStream fs WHERE fs.scheduledStartDate < :currentDate AND LOWER(fs.title) " +
          "LIKE LOWER(CONCAT('%', :title, '%')) ORDER BY fs.scheduledStartDate ASC")
  Page<FleenStream> findPastEventsByTitle(@Param("title") String title, @Param("currentDate") LocalDateTime dateTime, Pageable pageable);

  @Query(value = "SELECT fs FROM FleenStream fs WHERE :currentDate > fs.scheduledStartDate AND :currentDate < fs.scheduledEndDate ORDER BY fs.scheduledStartDate ASC")
  Page<FleenStream> findLiveEvents(@Param("currentDate") LocalDateTime dateTime, Pageable pageable);

  @Query(value = "SELECT fs FROM FleenStream fs WHERE :currentDate > fs.scheduledStartDate AND :currentDate < fs.scheduledEndDate AND LOWER(fs.title) " +
          "LIKE LOWER(CONCAT('%', :title, '%')) ORDER BY fs.scheduledStartDate ASC")
  Page<FleenStream> findLiveEventsByTitle(@Param("title") String title, @Param("currentDate") LocalDateTime dateTime, Pageable pageable);

}
