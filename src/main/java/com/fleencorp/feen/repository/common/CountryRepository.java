package com.fleencorp.feen.repository.common;

import com.fleencorp.feen.model.domain.other.Country;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CountryRepository extends JpaRepository<Country, Long> {
}
