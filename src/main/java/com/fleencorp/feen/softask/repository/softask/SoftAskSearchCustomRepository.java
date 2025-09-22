package com.fleencorp.feen.softask.repository.softask;

import com.fleencorp.feen.softask.model.projection.SoftAskWithDetail;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SoftAskSearchCustomRepository {

  Page<SoftAskWithDetail> findMany(
    Double latitude,
    Double longitude,
    Double radiusKm,
    Pageable pageable
  );

  Page<SoftAskWithDetail> findByAuthor(Long authorId, Pageable pageable);
}
