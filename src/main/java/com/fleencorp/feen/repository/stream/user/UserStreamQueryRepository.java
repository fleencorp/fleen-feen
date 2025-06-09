package com.fleencorp.feen.repository.stream.user;

import com.fleencorp.feen.constant.stream.StreamVisibility;
import com.fleencorp.feen.model.domain.stream.FleenStream;
import com.fleencorp.feen.user.model.domain.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface UserStreamQueryRepository extends JpaRepository<FleenStream, Long> {

  @Query("SELECT fs FROM FleenStream fs WHERE fs.streamId IS NOT NULL AND fs.member = :member ORDER BY fs.updatedOn DESC")
  Page<FleenStream> findManyByMe(@Param("member") Member member, Pageable pageable);

  @Query("SELECT fs FROM FleenStream fs WHERE fs.title = :title AND fs.member = :member ORDER BY fs.updatedOn DESC")
  Page<FleenStream> findByTitleAndUser(@Param("title") String title, @Param("member") Member member, Pageable pageable);

  @Query("SELECT fs FROM FleenStream fs WHERE fs.title = :title AND fs.streamVisibility = :visibility AND fs.member = :member ORDER BY fs.updatedOn DESC")
  Page<FleenStream> findByTitleAndUser(
    @Param("title") String title, @Param("visibility") StreamVisibility streamVisibility, @Param("member") Member member, Pageable pageable);

  @Query("SELECT fs FROM FleenStream fs WHERE fs.createdOn BETWEEN :startDate AND :endDate AND fs.member = :member ORDER BY fs.updatedOn DESC")
  Page<FleenStream> findByDateBetweenAndUser(
    @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate, @Param("member") Member member, Pageable pageable);

  @Query("SELECT fs FROM FleenStream fs WHERE fs.createdOn BETWEEN :startDate AND :endDate AND fs.streamVisibility = :visibility " +
    "AND fs.member = :member ORDER BY fs.updatedOn DESC")
  Page<FleenStream> findByDateBetweenAndUser(
    @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate, @Param("visibility") StreamVisibility streamVisibility,
    @Param("member") Member member, Pageable pageable);

  @Query("SELECT DISTINCT fs FROM FleenStream fs JOIN fs.attendees sa JOIN sa.member m WHERE m = :member")
  Page<FleenStream> findAttendedByUser(@Param("member") Member member, Pageable pageable);

  @Query("SELECT DISTINCT fs FROM FleenStream fs JOIN fs.attendees sa JOIN sa.member m WHERE fs.scheduledStartDate BETWEEN :startDate AND :endDate " +
    "AND m = :member ORDER BY fs.scheduledStartDate DESC")
  Page<FleenStream> findAttendedByDateBetweenAndUser(
    @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate, @Param("member") Member member, Pageable pageable);

  @Query("SELECT DISTINCT fs FROM FleenStream fs JOIN fs.attendees sa JOIN sa.member m WHERE LOWER(fs.title) " +
    "LIKE LOWER(CONCAT('%', :title, '%')) AND m = :member ORDER BY fs.scheduledStartDate DESC")
  Page<FleenStream> findAttendedByTitleAndUser(
    @Param("title") String title, @Param("member") Member member, Pageable pageable);

  @Query("SELECT DISTINCT fs FROM FleenStream fs " +
    "JOIN fs.attendees sa " +
    "WHERE sa.member = :you AND fs IN " +
    "(SELECT fs2 FROM FleenStream fs2 JOIN fs2.attendees sa2 WHERE sa2.member = :friend)")
  Page<FleenStream> findStreamsAttendedTogether(@Param("you") Member you, @Param("friend") Member friend, Pageable pageable);
}
