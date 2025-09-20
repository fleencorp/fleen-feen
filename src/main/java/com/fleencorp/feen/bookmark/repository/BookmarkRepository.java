package com.fleencorp.feen.bookmark.repository;

import com.fleencorp.feen.bookmark.constant.BookmarkParentType;
import com.fleencorp.feen.bookmark.constant.BookmarkType;
import com.fleencorp.feen.bookmark.model.domain.Bookmark;
import com.fleencorp.feen.bookmark.model.projection.UserBookmarkInfoSelect;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {

  @Query("SELECT l FROM Bookmark l WHERE l.memberId = :memberId AND l.chatSpaceId = :chatSpaceId AND l.parentType =:parentType")
  Optional<Bookmark> findByMemberAndChatSpace(
    @Param("memberId") Long memberId,
    @Param("chatSpaceId") Long chatSpaceId,
    @Param("parentType") BookmarkParentType bookmarkParentType);

  @Query("SELECT l FROM Bookmark l WHERE l.memberId = :memberId AND l.streamId = :streamId AND l.parentType =:parentType")
  Optional<Bookmark> findByMemberAndStream(
    @Param("memberId") Long memberId,
    @Param("streamId") Long streamId,
    @Param("parentType") BookmarkParentType bookmarkParentType);

  @Query("SELECT l FROM Bookmark l WHERE l.memberId = :memberId AND l.pollId = :pollId AND l.parentType =:parentType")
  Optional<Bookmark> findByMemberAndPoll(
    @Param("memberId") Long memberId,
    @Param("pollId") Long pollId,
    @Param("parentType") BookmarkParentType bookmarkParentType);

  @Query("SELECT l FROM Bookmark l WHERE l.memberId = :memberId AND l.reviewId = :reviewId AND l.parentType =:parentType")
  Optional<Bookmark> findByMemberAndReview(
    @Param("memberId") Long memberId,
    @Param("reviewId") Long reviewId,
    @Param("parentType") BookmarkParentType bookmarkParentType);

  @Query("SELECT l FROM Bookmark l WHERE l.memberId = :memberId AND l.softAskId = :softAskId AND l.parentType =:parentType")
  Optional<Bookmark> findByMemberAndSoftAsk(
    @Param("memberId") Long memberId,
    @Param("softAskId") Long softAskId,
    @Param("parentType") BookmarkParentType bookmarkParentType);

  @Query(value = """
    SELECT b
    FROM Bookmark b
    JOIN SoftAskReply sar ON b.parentId = sar.softAskReplyId
    JOIN sar.softAsk sa
    WHERE b.memberId = :memberId
      AND b.parentType = :parentType
      AND sar.softAskReplyId = :softAskReplyId
      AND sa.softAskId = :softAskId
  """)
  Optional<Bookmark> findByMemberAndSoftAskReply(
    @Param("memberId") Long memberId,
    @Param("softAskId") Long softAskId,
    @Param("softAskReplyId") Long softAskReplyId,
    @Param("parentType") BookmarkParentType bookmarkParentType);

  @Query(value =
    """
      SELECT new com.fleencorp.feen.bookmark.model.projection.UserBookmarkInfoSelect(b) FROM Bookmark b
      WHERE b.member.memberId = :memberId
      AND b.parentType = :parentType
      AND b.bookmarkType IN :bookmarkTypes
      AND (
           b.streamId IN (:parentIds) OR
           b.chatSpaceId IN (:parentIds) OR
           b.reviewId IN (:parentIds) OR
           b.softAskId IN (:parentIds) OR
           (b.softAskReplyId IN (:parentIds) AND b.softAskReply.softAskId = :otherId)
          )
    """)
  List<UserBookmarkInfoSelect> findBookmarksByParentIdsAndMember(
    @Param("parentIds") List<Long> parentIds,
    @Param("otherId") Long otherId,
    @Param("memberId") Long memberId,
    @Param("parentType") BookmarkParentType parentType,
    @Param("bookmarkTypes") List<BookmarkType> bookmarkTypes
  );

}
