package com.fleencorp.feen.poll.repository;

import com.fleencorp.feen.poll.model.domain.Poll;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PollManagementRepository extends JpaRepository<Poll, Integer> {

  @Modifying
  @Query(value = "UPDATE poll SET bookmark_count = bookmark_count - 1 WHERE poll_id = :pollId", nativeQuery = true)
  void decrementAndGetBookmarkCount(@Param("pollId") Long pollId);

  @Modifying
  @Query(value = "UPDATE poll SET bookmark_count = bookmark_count + 1 WHERE poll_id = :pollId", nativeQuery = true)
  void incrementAndGetBookmarkCount(@Param("pollId") Long pollId);

  @Query(value = "SELECT bookmark_count FROM poll WHERE poll_id = :pollId", nativeQuery = true)
  Integer getBookmarkCount(@Param("pollId") Long pollId);

  @Modifying
  @Query(value = "UPDATE poll SET like_count = like_count - 1 WHERE poll_id = :pollId", nativeQuery = true)
  void decrementAndGetLikeCount(@Param("pollId") Long pollId);

  @Modifying
  @Query(value = "UPDATE poll SET like_count = like_count + 1 WHERE poll_id = :pollId", nativeQuery = true)
  void incrementAndGetLikeCount(@Param("pollId") Long pollId);

  @Query(value = "SELECT like_count FROM poll WHERE poll = :pollId", nativeQuery = true)
  Integer getLikeCount(@Param("pollId") Long pollId);

}
