package com.fleencorp.feen.stream.repository.core;

import com.fleencorp.feen.stream.model.domain.FleenStream;
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
  @Query(value = "UPDATE stream SET like_count = like_count - 1 WHERE stream_id = :streamId", nativeQuery = true)
  void decrementAndGetLikeCount(@Param("streamId") Long streamId);

  @Modifying
  @Query(value = "UPDATE stream SET like_count = like_count + 1 WHERE stream_id = :streamId", nativeQuery = true)
  void incrementAndGetLikeCount(@Param("streamId") Long streamId);

  @Query(value = "SELECT like_count FROM stream WHERE stream_id = :streamId", nativeQuery = true)
  Integer getLikeCount(@Param("streamId") Long streamId);

  @Modifying
  @Query(value = "UPDATE stream SET bookmark_count = bookmark_count - 1 WHERE stream_id = :streamId", nativeQuery = true)
  void decrementAndGetBookmarkCount(@Param("streamId") Long streamId);

  @Modifying
  @Query(value = "UPDATE stream SET bookmark_count = bookmark_count + 1 WHERE stream_id = :streamId", nativeQuery = true)
  void incrementAndGetBookmarkCount(@Param("streamId") Long streamId);

  @Query(value = "SELECT bookmark_count FROM stream WHERE stream_id = :streamId", nativeQuery = true)
  Integer getBookmarkCount(@Param("streamId") Long streamId);
}
