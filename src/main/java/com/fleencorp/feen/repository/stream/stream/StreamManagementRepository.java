package com.fleencorp.feen.repository.stream.stream;

import com.fleencorp.feen.model.domain.stream.FleenStream;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface StreamManagementRepository extends JpaRepository<FleenStream, Long> {

  @Modifying
  @Query("UPDATE FleenStream fs SET fs.totalAttendees = fs.totalAttendees + 1 WHERE fs.streamId = :id")
  void incrementTotalAttendees(@Param("id") Long streamId);

  @Modifying
  @Query("UPDATE FleenStream fs SET fs.totalAttendees = fs.totalAttendees - 1 WHERE fs.streamId = :id")
  void decrementTotalAttendees(@Param("id") Long streamId);

  @Modifying
  @Query(value = "UPDATE stream SET like_count = like_count + 1 WHERE stream_id = :streamId RETURNING like_count", nativeQuery = true)
  int incrementAndGetLikeCount(@Param("streamId") Long streamId);

  @Modifying
  @Query(value = "UPDATE stream SET like_count = like_count - 1 WHERE stream_id = :streamId RETURNING like_count", nativeQuery = true)
  int decrementAndGetLikeCount(@Param("streamId") Long streamId);
}
