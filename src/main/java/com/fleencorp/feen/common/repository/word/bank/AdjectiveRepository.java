package com.fleencorp.feen.common.repository.word.bank;

import com.fleencorp.feen.model.domain.word.bank.Adjective;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface AdjectiveRepository extends JpaRepository<Adjective, Long> {

  @Query(value = "SELECT MIN(id) FROM adjectives", nativeQuery = true)
  int findMinAdjectiveId();

  @Query(value = "SELECT MAX(id) FROM adjectives", nativeQuery = true)
  int findMaxAdjectiveId();

  @Query(value = "SELECT * FROM adjectives WHERE id = :id", nativeQuery = true)
  Optional<Adjective> findAdjectiveById(@Param("id") int id);
}
