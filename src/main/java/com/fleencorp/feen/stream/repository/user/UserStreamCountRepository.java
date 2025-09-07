package com.fleencorp.feen.stream.repository.user;

import com.fleencorp.feen.stream.constant.core.StreamType;
import com.fleencorp.feen.stream.model.domain.FleenStream;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserStreamCountRepository extends JpaRepository<FleenStream, Long> {

  @Query("SELECT COUNT(sa) FROM StreamAttendee sa JOIN sa.memberId m WHERE m = :memberId")
  Long countTotalStreamsAttended(@Param("memberId") Long memberId);

  @Query("SELECT COUNT(sa) FROM StreamAttendee sa WHERE sa.stream.streamType = :streamType AND sa.memberId = :memberId")
  Long countTotalStreamsAttended(@Param("streamType") StreamType streamType, @Param("memberId") Long memberId);

  @Query("SELECT COUNT(fs) FROM FleenStream fs WHERE fs.memberId = :memberId")
  Long countTotalStreamsByUser(@Param("memberId") Long memberId);

  @Query("SELECT COUNT(fs) FROM FleenStream fs WHERE fs.streamType = :streamType AND fs.memberId = :memberId")
  Long countTotalStreamsByUser(@Param("streamType") StreamType streamType, @Param("memberId") Long memberId);
}
