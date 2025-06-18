package com.fleencorp.feen.poll.repository;

import com.fleencorp.feen.poll.model.domain.Poll;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PollRepository extends JpaRepository<Poll, Long> {

  @Query("SELECT p FROM Poll p WHERE p.pollId IS NOT NULL ORDER BY p.updatedOn DESC")
  Page<Poll> findMany(Pageable pageable);

  @Query("SELECT p FROM Poll p WHERE p.authorId = :authorId ORDER BY p.updatedOn DESC")
  Page<Poll> findByAuthor(@Param("authorId") Long authorId, Pageable pageable);

  @Query("SELECT p FROM Poll p WHERE p.streamId = :streamId AND p.deleted = false ORDER BY p.updatedOn DESC")
  Page<Poll> findByStream(@Param("streamId") Long streamId, Pageable pageable);

  @Query("SELECT p FROM Poll p WHERE p.chatSpaceId = :chatSpaceId AND p.deleted = false ORDER BY p.updatedOn DESC")
  Page<Poll> findByChatSpace(@Param("chatSpaceId") Long chatSpaceId, Pageable pageable);

}

