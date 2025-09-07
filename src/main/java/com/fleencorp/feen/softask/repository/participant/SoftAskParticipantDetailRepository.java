package com.fleencorp.feen.softask.repository.participant;

import com.fleencorp.feen.softask.model.domain.SoftAskParticipantDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface SoftAskParticipantDetailRepository extends JpaRepository<SoftAskParticipantDetail, Long> {

  @Query("SELECT sau FROM SoftAskParticipantDetail sau WHERE sau.softAskId = :softAskId AND sau.userId = :userId")
  Optional<SoftAskParticipantDetail> findUsernameBySoftAskIdAndUserId(@Param("softAskId") Long softAskId, @Param("userId") Long userId);
}
