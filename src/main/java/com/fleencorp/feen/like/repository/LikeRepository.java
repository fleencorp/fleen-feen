package com.fleencorp.feen.like.repository;

import com.fleencorp.feen.like.constant.LikeParentType;
import com.fleencorp.feen.like.constant.LikeType;
import com.fleencorp.feen.like.model.domain.Like;
import com.fleencorp.feen.like.model.projection.UserLikeInfoSelect;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {

  @Query("SELECT l FROM Like l WHERE l.memberId = :memberId AND l.chatSpaceId = :chatSpaceId")
  Optional<Like> findByMemberAndChatSpace(@Param("memberId") Long memberId, @Param("chatSpaceId") Long chatSpaceId);

  @Query("SELECT l FROM Like l WHERE l.memberId = :memberId AND l.streamId = :streamId")
  Optional<Like> findByMemberAndStream(@Param("memberId") Long memberId, @Param("streamId") Long streamId);

  @Query("SELECT l FROM Like l WHERE l.memberId = :memberId AND l.reviewId = :reviewId")
  Optional<Like> findByMemberAndReview(@Param("memberId") Long memberId, @Param("reviewId") Long reviewId);

  /**
   * Retrieves a list of like information for the specified member and parent entities (streams, chat spaces, or reviews),
   * filtered by the like parent type and like types.
   *
   * <p>This query supports dynamic filtering for different parent entities (identified by their IDs)
   * using the provided {@code parentType} and {@code likeTypes}. It checks the appropriate ID field
   * based on the parent type.</p>
   *
   * @param parentIds the list of IDs representing the parent entities (e.g., streamId, chatSpaceId, reviewId)
   * @param memberId the ID of the member whose likes are being queried
   * @param parentType the parent entity type (STREAM, CHAT_SPACE, or REVIEW)
   * @param likeTypes the list of like types to include in the result
   * @return a list of {@link UserLikeInfoSelect} projections containing like details for the given member
   */
  @Query(value =
    """
      SELECT new com.fleencorp.feen.model.projection.like.UserLikeInfoSelect(l) FROM Like l
      WHERE l.member.memberId = :memberId
      AND l.likeParentType = :parentType
      AND l.likeType IN :likeTypes
      AND (l.streamId IN (:parentIds) OR l.chatSpaceId IN (:parentIds) OR l.reviewId IN (:parentIds))
    """)
  List<UserLikeInfoSelect> findLikesByParentIdsAndMember(
    @Param("parentIds") List<Long> parentIds,
    @Param("memberId") Long memberId,
    @Param("parentType") LikeParentType parentType,
    @Param("likeTypes") List<LikeType> likeTypes
  );


}
