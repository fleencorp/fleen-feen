package com.fleencorp.feen.repository.stream.user;

import com.fleencorp.feen.constant.stream.StreamType;
import com.fleencorp.feen.model.domain.stream.FleenStream;
import com.fleencorp.feen.user.model.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserStreamCountRepository extends JpaRepository<FleenStream, Long> {

  @Query("SELECT COUNT(sa) FROM StreamAttendee sa JOIN sa.member m WHERE m = :member")
  Long countTotalStreamsAttended(@Param("member") Member member);

  @Query("SELECT COUNT(sa) FROM StreamAttendee sa JOIN sa.member m WHERE sa.stream.streamType = :streamType AND m = :member")
  Long countTotalStreamsAttended(@Param("streamType") StreamType streamType, @Param("member") Member member);

  @Query("SELECT COUNT(fs) FROM FleenStream fs WHERE fs.member = :member")
  Long countTotalStreamsByUser(@Param("member") Member member);

  @Query("SELECT COUNT(fs) FROM FleenStream fs WHERE fs.streamType = :streamType AND fs.member = :member")
  Long countTotalStreamsByUser(@Param("streamType") StreamType streamType, @Param("member") Member member);
}
