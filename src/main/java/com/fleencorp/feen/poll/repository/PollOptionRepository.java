package com.fleencorp.feen.poll.repository;

import com.fleencorp.feen.poll.model.domain.PollOption;
import com.fleencorp.feen.poll.model.projection.PollOptionEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface PollOptionRepository extends JpaRepository<PollOption, Long> {

  @Modifying
  @Query(value =
    """
      UPDATE PollOption po
      SET po.voteCount = po.voteCount + 1
      WHERE po.pollId = :pollId AND po.pollOptionId IN :optionIds
    """)
  void incrementTotalEntries(@Param("pollId") Long pollId, @Param("optionIds") Collection<Long> optionIds);

  @Modifying
  @Query(value =
    """
      UPDATE PollOption po
      SET po.voteCount = po.voteCount - 1
      WHERE po.pollId = :pollId AND po.pollOptionId IN :optionIds
    """)
  void decrementTotalEntries(@Param("pollId") Long pollId, @Param("optionIds") Collection<Long> optionIds);

  @Query(value =
  """
    SELECT new com.fleencorp.feen.poll.model.projection.PollOptionEntry(po.pollOptionId, po.voteCount)
    FROM PollOption po
    WHERE po.pollId = :pollId AND po.pollOptionId IN (:optionIds)
    """)
  List<PollOptionEntry> findOptionEntries(@Param("pollId") Long pollId, @Param("optionIds") Collection<Long> optionIds);
}
