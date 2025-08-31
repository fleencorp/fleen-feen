package com.fleencorp.feen.softask.repository.participant;

import com.fleencorp.feen.softask.model.domain.SoftAskUsername;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface SoftAskUsernameRepository extends JpaRepository<SoftAskUsername, Long> {

  @Query("SELECT sau.username FROM SoftAskUsername sau WHERE sau.softAskId = :softAskId AND sau.userId = :userId")
  Optional<String> findUsernameBySoftAskIdAndUserId(@Param("softAskId") Long softAskId, @Param("userId") Long userId);
}
