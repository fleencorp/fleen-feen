package com.fleencorp.feen.repository.user;

import com.fleencorp.feen.model.domain.user.Follower;
import com.fleencorp.feen.model.domain.user.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface FollowerRepository extends JpaRepository<Follower, Long> {

  Optional<Follower> findByFollowingAndFollowed(Member follower, Member followed);

  // Finding followers of a user (users who follow the given user)
  @EntityGraph(attributePaths = {"followed"})
  @Query("SELECT f FROM Follower f WHERE f.followed = :member")
  Page<Follower> findFollowersByUser(@Param("member") Member member, Pageable pageable);

  // Finding users the given user is following
  @EntityGraph(attributePaths = {"following"})
  @Query("SELECT f FROM Follower f WHERE f.following = :member")
  Page<Follower> findByFollowing(@Param("member") Member member, Pageable pageable);

  @Query("SELECT f FROM Follower f WHERE f.followingId = :followingId AND f.followedId = :followedId")
  Optional<Follower> findByFollowingIdAndFollowedId(@Param("followingId") Long followingId, @Param("followedId") Long followedId);

  @Query("SELECT COUNT(f) FROM Follower f WHERE f.followingId = :memberId")
  long countByFollowing(@Param("memberId") Long memberId);

  @Query("SELECT COUNT(f) FROM Follower f WHERE f.followedId = :memberId")
  long countByFollowed(@Param("memberId") Long memberId);

}
