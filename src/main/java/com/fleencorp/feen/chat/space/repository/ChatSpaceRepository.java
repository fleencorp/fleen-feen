package com.fleencorp.feen.chat.space.repository;

import com.fleencorp.feen.chat.space.constant.core.ChatSpaceStatus;
import com.fleencorp.feen.chat.space.model.domain.ChatSpace;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface ChatSpaceRepository extends JpaRepository<ChatSpace, Long> {

  @Query(value = """
    SELECT cs FROM ChatSpace cs
    WHERE
      cs.createdOn BETWEEN :startDate AND :endDate AND
      cs.status = :status
    ORDER BY
      cs.updatedOn DESC
  """)
  Page<ChatSpace> findByDateBetween(
    @Param("startDate") LocalDateTime startDate,
    @Param("endDate") LocalDateTime endDate,
    @Param("status") ChatSpaceStatus status,
    Pageable pageable
  );

  @Query(value = "SELECT cs FROM ChatSpace cs WHERE cs.title = :title AND cs.status = :status")
  Page<ChatSpace> findByTitle(@Param("title") String title, @Param("status") ChatSpaceStatus status, Pageable pageable);

  @Query("SELECT cs FROM ChatSpace cs WHERE cs.chatSpaceId IS NOT NULL AND cs.status = :status ORDER BY cs.updatedOn DESC")
  Page<ChatSpace> findMany(@Param("status") ChatSpaceStatus status, Pageable pageable);

  @Modifying
  @Query("UPDATE ChatSpace cs SET cs.totalMembers = cs.totalMembers - 1 WHERE cs.chatSpaceId = :id")
  void decrementTotalMembers(@Param("id") Long chatSpaceId);

  @Modifying
  @Query("UPDATE ChatSpace cs SET cs.totalMembers = cs.totalMembers + 1 WHERE cs.chatSpaceId = :id")
  void incrementTotalMembers(@Param("id") Long chatSpaceId);

  @Modifying
  @Query(value = "UPDATE chat_space SET like_count = like_count - 1 WHERE chat_space_id = :chatSpaceId", nativeQuery = true)
  void decrementAndGetLikeCount(@Param("chatSpaceId") Long chatSpaceId);

  @Modifying
  @Query(value = "UPDATE chat_space SET like_count = like_count + 1 WHERE chat_space_id = :chatSpaceId", nativeQuery = true)
  void incrementAndGetLikeCount(@Param("chatSpaceId") Long chatSpaceId);

  @Query(value = "SELECT like_count FROM chat_space WHERE chat_space_id = :chatSpaceId", nativeQuery = true)
  Integer getLikeCount(@Param("chatSpaceId") Long chatSpaceId);

  @Modifying
  @Query(value = "UPDATE chat_space SET bookmark_count = bookmark_count - 1 WHERE chat_space_id = :chatSpaceId", nativeQuery = true)
  void decrementAndGetBookmarkCount(@Param("chatSpaceId") Long chatSpaceId);

  @Modifying
  @Query(value = "UPDATE chat_space SET bookmark_count = bookmark_count + 1 WHERE chat_space_id = :chatSpaceId", nativeQuery = true)
  void incrementAndBookmarkCount(@Param("chatSpaceId") Long chatSpaceId);

  @Query(value = "SELECT bookmark_count FROM chat_space WHERE chat_space_id = :chatSpaceId", nativeQuery = true)
  Integer getBookmarkCount(@Param("chatSpaceId") Long chatSpaceId);
}
