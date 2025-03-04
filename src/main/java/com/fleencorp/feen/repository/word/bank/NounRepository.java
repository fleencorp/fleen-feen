package com.fleencorp.feen.repository.word.bank;

import com.fleencorp.feen.model.domain.word.bank.Noun;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface NounRepository extends JpaRepository<Noun, Long> {

  @Query(value = "SELECT MIN(id) FROM nouns", nativeQuery = true)
  int findMinNounId();

  @Query(value = "SELECT MAX(id) FROM nouns", nativeQuery = true)
  int findMaxNounId();

  @Query(value = "SELECT * FROM nouns WHERE id = :id", nativeQuery = true)
  Optional<Noun> findNounById(@Param("id") int id);
}
