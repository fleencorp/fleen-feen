package com.fleencorp.feen.softask.repository.softask;

import com.fleencorp.feen.softask.model.domain.SoftAsk;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SoftAskSearchRepository extends JpaRepository<SoftAsk, Long> {
}
