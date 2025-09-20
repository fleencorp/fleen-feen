package com.fleencorp.feen.bookmark.repository;

import com.fleencorp.feen.bookmark.constant.BookmarkParentType;
import com.fleencorp.feen.bookmark.constant.BookmarkType;
import com.fleencorp.feen.bookmark.model.domain.Bookmark;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface BookmarkSearchRepository extends JpaRepository<Bookmark, Long> {

  @Query(value = """
    SELECT b FROM Bookmark b
      WHERE b.createdOn BETWEEN :startDate AND :endDate AND
      b.parentType IN (:parentTypes) AND
      b.bookmarkType = :bookmarked AND
      b.memberId = :memberId
      ORDER BY b.updatedOn DESC
  """)
  Page<Bookmark> findByDateBetween(
    @Param("startDate") LocalDateTime startDate,
    @Param("endDate") LocalDateTime endDate,
    @Param("parentTypes") List<BookmarkParentType> parentTypes,
    @Param("bookmarkType") BookmarkType bookmarkType,
    @Param("memberId") Long memberId,
    Pageable pageable);

  @Query(value = """
    SELECT b FROM Bookmark b
      WHERE b.parentSummary = :summary AND
      b.parentType IN (:parentTypes) AND
      b.bookmarkType = :bookmarkType AND
      b.memberId = :memberId
      ORDER BY b.updatedOn DESC
  """)
  Page<Bookmark> findByTitle(
    @Param("summary") String summary,
    @Param("parentTypes") List<BookmarkParentType> parentTypes,
    @Param("bookmarkType") BookmarkType bookmarkType,
    @Param("memberId") Long memberId,
    Pageable pageable);

  @Query(value = """
    SELECT b FROM Bookmark b
      WHERE b.bookmarkId IS NOT NULL AND
      b.parentType IN (:parentTypes) AND
      b.bookmarkType = :bookmarkType AND
      b.memberId = :memberId
      ORDER BY b.updatedOn DESC
  """)
  Page<Bookmark> findMany(
    @Param("parentTypes") List<BookmarkParentType> parentTypes,
    @Param("bookmarkType") BookmarkType bookmarkType,
    @Param("memberId") Long memberId,
    Pageable pageable);
}
