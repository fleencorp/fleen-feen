package com.fleencorp.feen.repository.like;

import com.fleencorp.feen.constant.like.LikeParentType;
import com.fleencorp.feen.constant.like.LikeType;
import com.fleencorp.feen.model.domain.like.Like;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {

  @Query("SELECT l FROM Like l WHERE l.memberId = :memberId AND l.chatSpaceId = :chatSpaceId")
  Optional<Like> findByMemberAndChatSpace(@Param("memberId") Long memberId, @Param("chatSpaceId") Long chatSpaceId);

  @Query("SELECT l FROM Like l WHERE l.memberId = :memberId AND l.streamId = :streamId")
  Optional<Like> findByMemberAndStream(@Param("memberId") Long memberId, @Param("streamId") Long streamId);

  @Query("SELECT l FROM Like l WHERE l.memberId = :memberId AND l.reviewId = :reviewId")
  Optional<Like> findByMemberAndReview(@Param("memberId") Long memberId, @Param("reviewId") Long reviewId);

  /**
   * Checks if a like exists for the given parent entity by the specified member with the specified like type.
   *
   * <p>This query determines whether a like already exists for a specific parent entity,
   * based on the provided parameters: the member ID, parent entity ID, like type,
   * and the type of the parent entity (like a stream or a chat space).
   * It returns <code>true</code> if a like exists, otherwise it returns <code>false</code>.</p>
   *
   * @param parentId the ID of the parent entity (stream or chat space)
   * @param likeParentType the type of the parent entity (stream or chat space)
   * @param likeType the type of the like (e.g., like or dislike)
   * @param memberId the ID of the member performing the like action
   * @return <code>true</code> if a like exists, otherwise <code>false</code>
   */
  @Query(value = """
    SELECT CASE WHEN COUNT(l) > 0 THEN TRUE ELSE FALSE END FROM Like l
    WHERE l.memberId = :memberId AND l.likeParentType = :parentLikeType AND l.parentId = :parentId AND l.likeType = :likeType
    """)
  boolean existsLike(
    @Param("parentId") Long parentId, @Param("parentLikeType") LikeParentType likeParentType, @Param("likeType") LikeType likeType, @Param("memberId") Long memberId);

}
