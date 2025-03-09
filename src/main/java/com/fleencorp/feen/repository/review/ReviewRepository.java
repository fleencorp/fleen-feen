package com.fleencorp.feen.repository.review;

import com.fleencorp.feen.model.domain.review.Review;
import com.fleencorp.feen.model.domain.stream.FleenStream;
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

public interface ReviewRepository extends JpaRepository<Review, Long> {

  @Query("SELECT r FROM Review r WHERE r.stream = :stream")
  Page<Review> findByStream(@Param("stream")FleenStream stream, Pageable pageable);

  @Query("SELECT sr FROM Review sr WHERE sr.stream = :stream ORDER BY sr.createdOn DESC")
  List<Review> findMostRecentReviewByStream(@Param("stream") FleenStream stream, PageRequest pageRequest);

  @Query("SELECT r FROM Review r WHERE r.member = :member")
  Page<Review> findByMember(Member member, Pageable pageable);

  @Query("SELECT r FROM Review r WHERE r.reviewId = :reviewId AND r.stream = :stream AND r.member = :member")
  Optional<Review> findByReviewIdAndStreamAndMember(@Param("reviewId") Long reviewId, @Param("stream") FleenStream stream, Member member);

  @Modifying
  @Query("DELETE FROM Review r WHERE r.reviewId = :reviewId AND r.member = :member")
  void deleteByStreamReviewIdAndMember(Long reviewId, Member member);
}
