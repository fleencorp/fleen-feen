package com.fleencorp.feen.like.repository;

import com.fleencorp.feen.like.constant.LikeParentType;
import com.fleencorp.feen.like.constant.LikeType;
import com.fleencorp.feen.like.model.domain.Like;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface LikeSearchRepository extends JpaRepository<Like, Long> {

  @Query(value = """
    SELECT b FROM Like b
      WHERE b.createdOn BETWEEN :startDate AND :endDate AND
      b.likeParentType IN (:likeParentTypes) AND
      b.likeType = :likeed AND
      b.memberId = :memberId
      ORDER BY b.updatedOn DESC
  """)
  Page<Like> findByDateBetween(
    @Param("startDate") LocalDateTime startDate,
    @Param("endDate") LocalDateTime endDate,
    @Param("likeParentTypes") List<LikeParentType> likeParentTypes,
    @Param("likeType") LikeType likeType,
    @Param("memberId") Long memberId,
    Pageable pageable);

  @Query(value = """
    SELECT b FROM Like b
      WHERE b.parentTitle = :title AND
      b.likeParentType IN (:likeParentTypes) AND
      b.likeType = :likeType AND
      b.memberId = :memberId
      ORDER BY b.updatedOn DESC
  """)
  Page<Like> findByTitle(
    @Param("title") String title,
    @Param("likeParentTypes") List<LikeParentType> likeParentTypes,
    @Param("likeType") LikeType likeType,
    @Param("memberId") Long memberId,
    Pageable pageable);

  @Query(value = """
    SELECT b FROM Like b
      WHERE b.likeId IS NOT NULL AND
      b.likeParentType IN (:likeParentTypes) AND
      b.likeType = :likeType AND
      b.memberId = :memberId
      ORDER BY b.updatedOn DESC
  """)
  Page<Like> findMany(
    @Param("likeParentTypes") List<LikeParentType> likeParentTypes,
    @Param("likeType") LikeType likeType,
    @Param("memberId") Long memberId,
    Pageable pageable);
}
