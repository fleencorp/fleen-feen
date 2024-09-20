package com.fleencorp.feen.repository.stream;

import com.fleencorp.feen.model.domain.stream.FleenStream;
import com.fleencorp.feen.model.domain.stream.StreamReview;
import com.fleencorp.feen.model.domain.user.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface StreamReviewRepository extends JpaRepository<StreamReview, Long> {

  @Query("SELECT r FROM StreamReview r WHERE r.fleenStream = :stream")
  Page<StreamReview> findByStream(FleenStream stream, Pageable pageable);

  @Query("SELECT r FROM StreamReview r WHERE r.member = :member")
  Page<StreamReview> findByMember(Member member, Pageable pageable);

  void deleteByStreamReviewIdAndMember(Long reviewId, Member member);
}
