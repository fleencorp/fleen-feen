package com.fleencorp.feen.review.repository;

import com.fleencorp.feen.model.projection.stream.review.ReviewParentCount;
import com.fleencorp.feen.review.constant.ReviewParentType;
import com.fleencorp.feen.review.model.domain.Review;
import com.fleencorp.feen.user.model.domain.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {

  @Query("SELECT r FROM Review r WHERE r.streamId = :streamId")
  Page<Review> findByStreamId(@Param("streamId") Long streamId, Pageable pageable);

  @Query("SELECT sr FROM Review sr WHERE sr.streamId = :streamId ORDER BY sr.createdOn DESC")
  List<Review> findMostRecentReviewByStream(@Param("streamId") Long streamId, PageRequest pageRequest);

  @Query("SELECT r FROM Review r WHERE r.author = :member")
  Page<Review> findByMember(Member member, Pageable pageable);

  @Query("SELECT r FROM Review r WHERE r.reviewId = :reviewId AND r.streamId = :streamId AND r.author = :member")
  Optional<Review> findByReviewIdAndStreamAndMember(@Param("reviewId") Long reviewId, @Param("streamId") Long streamId, Member member);

  @Modifying
  @Query("DELETE FROM Review r WHERE r.reviewId = :reviewId AND r.author = :member")
  void deleteByStreamReviewIdAndMember(Long reviewId, Member member);

  @Modifying
  @Query(value = "UPDATE review SET like_count = like_count + 1 WHERE review = :chatSpaceId RETURNING like_count", nativeQuery = true)
  int incrementAndGetLikeCount(@Param("chatSpaceId") Long chatSpaceId);

  @Modifying
  @Query(value = "UPDATE review SET like_count = like_count - 1 WHERE review = :chatSpaceId RETURNING like_count", nativeQuery = true)
  int decrementAndGetLikeCount(@Param("chatSpaceId") Long chatSpaceId);

  @Query(value =
    """
      SELECT r.parentId AS parentId, COUNT(r) AS count
      FROM Review r
      WHERE r.reviewParentType = :parentType AND r.parentId IN (:parentIds)
      GROUP BY r.parentId
  """)
  List<ReviewParentCount> countReviewsGroupedByParentId(
    @Param("parentType") ReviewParentType parentType,
    @Param("parentIds") Collection<Long> parentIds
  );

}
