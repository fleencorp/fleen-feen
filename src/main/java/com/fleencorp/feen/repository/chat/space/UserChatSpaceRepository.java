package com.fleencorp.feen.repository.chat.space;

import com.fleencorp.feen.model.domain.chat.ChatSpace;
import com.fleencorp.feen.user.model.domain.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface UserChatSpaceRepository extends JpaRepository<ChatSpace, Long> {

  @Query(value = "SELECT cs FROM ChatSpace cs WHERE cs.createdOn BETWEEN :startDate AND :endDate AND cs.member = :member ORDER BY cs.updatedOn DESC")
  Page<ChatSpace> findByDateBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate, @Param("member") Member member,
                                    Pageable pageable);

  @Query(value = "SELECT cs FROM ChatSpace cs WHERE cs.title = :title AND cs.member = :member")
  Page<ChatSpace> findByTitle(@Param("title") String title, @Param("member") Member member, Pageable pageable);

  @Query("SELECT cs FROM ChatSpace cs WHERE cs.chatSpaceId IS NOT NULL AND cs.member = :member ORDER BY cs.updatedOn DESC")
  Page<ChatSpace> findMany(@Param("member") Member member, Pageable pageable);
}
