package com.fleencorp.feen.stream.repository.core;

import com.fleencorp.feen.stream.constant.core.StreamStatus;
import com.fleencorp.feen.stream.constant.core.StreamType;
import com.fleencorp.feen.stream.model.domain.FleenStream;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface StreamSearchRepository extends JpaRepository<FleenStream, Long> {

  @Query(value = "SELECT fs FROM FleenStream fs WHERE fs.createdOn BETWEEN :startDate AND :endDate AND fs.streamStatus = :status ORDER BY fs.updatedOn DESC")
  Page<FleenStream> findByDateBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate, @Param("status") StreamStatus status, Pageable pageable);

  @Query(value = "SELECT fs FROM FleenStream fs WHERE fs.title = :title AND fs.streamStatus = :status")
  Page<FleenStream> findByTitle(@Param("title") String title, @Param("status") StreamStatus status, Pageable pageable);

  @Query("SELECT fs FROM FleenStream fs WHERE fs.streamId IS NOT NULL AND fs.streamStatus = :status ORDER BY fs.updatedOn DESC")
  Page<FleenStream> findMany(@Param("status") StreamStatus status, Pageable pageable);

  @Query(value = "SELECT fs FROM FleenStream fs WHERE fs.streamType = :streamType AND fs.scheduledStartDate > :currentDate ORDER BY fs.scheduledStartDate ASC")
  Page<FleenStream> findUpcomingStreams(@Param("currentDate") LocalDateTime dateTime, @Param("streamType") StreamType streamType, Pageable pageable);

  @Query(value = "SELECT fs FROM FleenStream fs WHERE fs.streamType = :streamType AND fs.scheduledStartDate > :currentDate AND LOWER(fs.title) " +
    "LIKE LOWER(CONCAT('%', :title, '%')) ORDER BY fs.scheduledStartDate ASC")
  Page<FleenStream> findUpcomingStreamsByTitle(@Param("title") String title, @Param("currentDate") LocalDateTime dateTime, @Param("streamType") StreamType streamType, Pageable pageable);

  @Query(value = "SELECT fs FROM FleenStream fs WHERE fs.streamType = :streamType AND fs.scheduledStartDate < :currentDate ORDER BY fs.scheduledStartDate ASC")
  Page<FleenStream> findPastStreams(@Param("currentDate") LocalDateTime dateTime, @Param("streamType") StreamType streamType, Pageable pageable);

  @Query(value = "SELECT fs FROM FleenStream fs WHERE fs.streamType = :streamType AND fs.scheduledStartDate < :currentDate AND LOWER(fs.title) " +
    "LIKE LOWER(CONCAT('%', :title, '%')) ORDER BY fs.scheduledStartDate ASC")
  Page<FleenStream> findPastStreamsByTitle(@Param("title") String title, @Param("currentDate") LocalDateTime dateTime, @Param("streamType") StreamType streamType, Pageable pageable);

  @Query(value = "SELECT fs FROM FleenStream fs WHERE fs.streamType = :streamType AND :currentDate > fs.scheduledStartDate AND :currentDate < fs.scheduledEndDate ORDER BY fs.scheduledStartDate ASC")
  Page<FleenStream> findLiveStreams(@Param("currentDate") LocalDateTime dateTime, @Param("streamType") StreamType streamType, Pageable pageable);

  @Query(value = "SELECT fs FROM FleenStream fs WHERE fs.streamType = :streamType AND :currentDate > fs.scheduledStartDate AND :currentDate < fs.scheduledEndDate AND LOWER(fs.title) " +
    "LIKE LOWER(CONCAT('%', :title, '%')) ORDER BY fs.scheduledStartDate ASC")
  Page<FleenStream> findLiveStreamsByTitle(@Param("title") String title, @Param("currentDate") LocalDateTime dateTime, @Param("streamType") StreamType streamType, Pageable pageable);

  @Query(value = "SELECT fs FROM FleenStream fs WHERE fs.chatSpaceId = :chatSpaceId ORDER BY fs.updatedOn DESC")
  Page<FleenStream> findByChatSpaceId(Long chatSpaceId, Pageable pageable);
}