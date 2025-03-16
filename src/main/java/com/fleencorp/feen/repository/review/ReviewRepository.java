package com.fleencorp.feen.repository.review;

import com.fleencorp.feen.model.domain.review.Review;
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

  @Query("SELECT r FROM Review r WHERE r.streamId = :streamId")
  Page<Review> findByStreamId(@Param("streamId") Long streamId, Pageable pageable);

  @Query("SELECT sr FROM Review sr WHERE sr.streamId = :streamId ORDER BY sr.createdOn DESC")
  List<Review> findMostRecentReviewByStream(@Param("streamId") Long streamId, PageRequest pageRequest);

  @Query("SELECT r FROM Review r WHERE r.member = :member")
  Page<Review> findByMember(Member member, Pageable pageable);

  @Query("SELECT r FROM Review r WHERE r.reviewId = :reviewId AND r.streamId = :streamId AND r.member = :member")
  Optional<Review> findByReviewIdAndStreamAndMember(@Param("reviewId") Long reviewId, @Param("streamId") Long streamId, Member member);

  @Modifying
  @Query("DELETE FROM Review r WHERE r.reviewId = :reviewId AND r.member = :member")
  void deleteByStreamReviewIdAndMember(Long reviewId, Member member);
}
