package com.fleencorp.feen.repository.user;

import com.fleencorp.feen.model.domain.user.Follower;
import com.fleencorp.feen.model.domain.user.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FollowerRepository extends JpaRepository<Follower, Long> {

  Optional<Follower> findByFollowerAndFollowed(Member follower, Member followed);

  Page<Follower> findByFollowed(Member followed, Pageable pageable);

  Page<Follower> findByFollower(Member member, Pageable pageable);
}
