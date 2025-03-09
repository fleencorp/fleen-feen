package com.fleencorp.feen.repository.stream;

import com.fleencorp.feen.model.domain.stream.FleenStream;
import com.fleencorp.feen.model.domain.stream.StreamReview;
import com.fleencorp.feen.model.domain.user.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface StreamReviewRepository extends JpaRepository<StreamReview, Long> {

  @Query("SELECT r FROM StreamReview r WHERE r.stream = :stream")
  Page<StreamReview> findByStream(FleenStream stream, Pageable pageable);

  @Query("SELECT sr FROM StreamReview sr WHERE sr.stream = :stream ORDER BY sr.createdOn DESC")
  List<StreamReview> findMostRecentReviewByStream(@Param("stream") FleenStream stream, PageRequest pageRequest);

  @Query("SELECT r FROM StreamReview r WHERE r.member = :member")
  Page<StreamReview> findByMember(Member member, Pageable pageable);

  @Query("SELECT r FROM StreamReview r WHERE r.reviewId = :reviewId AND r.stream = :stream AND r.member = :member")
  Optional<StreamReview> findByReviewIdAndStreamAndMember(@Param("reviewId") Long reviewId, @Param("stream") FleenStream stream, Member member);

  @Modifying
  @Query("DELETE FROM StreamReview r WHERE r.reviewId = :reviewId AND r.member = :member")
  void deleteByStreamReviewIdAndMember(Long reviewId, Member member);
}
