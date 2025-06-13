package com.fleencorp.feen.country.repository;

import com.fleencorp.feen.country.model.domain.Country;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface CountryRepository extends JpaRepository<Country, Long> {

  @Query("SELECT c FROM Country c WHERE c.countryId IS NOT NULL ORDER BY c.updatedOn DESC")
  Page<Country> findMany(Pageable pageable);

  Optional<Country> findByCode(String code);

  boolean existsByCode(String countryCode);
}
