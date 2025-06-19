package com.fleencorp.feen.follower.repository;

import com.fleencorp.feen.follower.model.domain.Follower;
import com.fleencorp.feen.user.model.domain.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
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

  @Query("SELECT f FROM Follower f WHERE f.following = :following AND f.followedId IN (:followedIds)")
  List<Follower> findByFollowingAndFollowers(@Param("following") Member following, @Param("followedIds") List<Long> followedIds);

  @Query("SELECT f FROM Follower f WHERE f.followed = :followed AND f.followingId IN (:followingIds)")
  List<Follower> findByFollowedAndFollowings(@Param("followed") Member followed, @Param("followingIds") List<Long> followingIds);

}
