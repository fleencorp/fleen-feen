package com.fleencorp.feen.softask.repository.vote;

import com.fleencorp.feen.softask.constant.core.vote.SoftAskVoteType;
import com.fleencorp.feen.softask.model.domain.SoftAskVote;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface SoftAskVoteSearchRepository extends JpaRepository<SoftAskVote, Long> {

  @Query("SELECT sav FROM SoftAskVote sav WHERE sav.parentId IN (:parentIds) AND sav.memberId = :memberId")
  List<SoftAskVote> findByParentsAndMember(@Param("parentIds") Collection<Long> parentIds, @Param("memberId") Long memberId);

  @Query("SELECT sav FROM SoftAskVote sav WHERE sav.memberId = :memberId AND sav.voteType IN (:voteTypeS) ORDER BY sav.updatedOn DESC")
  Page<SoftAskVote> findByAuthor(@Param("memberId") Long memberId, @Param("voteTypes") List<SoftAskVoteType> voteTypes, Pageable pageable);
}
