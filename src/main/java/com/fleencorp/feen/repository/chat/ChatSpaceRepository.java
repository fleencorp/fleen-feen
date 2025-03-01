package com.fleencorp.feen.repository.chat;

import com.fleencorp.feen.model.domain.chat.ChatSpace;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface ChatSpaceRepository extends JpaRepository<ChatSpace, Long> {

  @Query(value = "SELECT cs FROM ChatSpace cs WHERE cs.createdOn BETWEEN :startDate AND :endDate AND cs.active = :active ORDER BY cs.updatedOn DESC")
  Page<ChatSpace> findByDateBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate, @Param("active") Boolean active,
                                    Pageable pageable);

  @Query(value = "SELECT cs FROM ChatSpace cs WHERE cs.title = :title AND cs.active = :isActive")
  Page<ChatSpace> findByTitle(@Param("title") String title, @Param("isActive") Boolean active, Pageable pageable);

  @Query("SELECT cs FROM ChatSpace cs WHERE cs.chatSpaceId IS NOT NULL AND cs.active = :isActive ORDER BY cs.updatedOn DESC")
  Page<ChatSpace> findMany(@Param("isActive") Boolean active, Pageable pageable);

  @Modifying
  @Query("UPDATE ChatSpace cs SET cs.totalMembers = cs.totalMembers + 1 WHERE cs.chatSpaceId = :id")
  void incrementTotalMembers(@Param("id") Long chatSpaceId);

  @Modifying
  @Query("UPDATE ChatSpace cs SET cs.totalMembers = cs.totalMembers - 1 WHERE cs.chatSpaceId = :id")
  void decrementTotalMembers(@Param("id") Long chatSpaceId);
}
