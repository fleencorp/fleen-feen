package com.fleencorp.feen.repository.common;

import com.fleencorp.feen.model.domain.other.Country;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CountryRepository extends JpaRepository<Country, Long> {

  @Query("SELECT c FROM Country c WHERE c.countryId IS NOT NULL ORDER BY c.updatedOn DESC")
  Page<Country> findMany(Pageable pageable);
}
