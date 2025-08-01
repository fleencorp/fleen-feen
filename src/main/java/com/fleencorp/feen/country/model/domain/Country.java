package com.fleencorp.feen.country.model.domain;

import com.fleencorp.feen.model.domain.base.FleenFeenEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static jakarta.persistence.GenerationType.IDENTITY;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "country", uniqueConstraints = @UniqueConstraint(columnNames = "code"))
public class Country extends FleenFeenEntity {

  @Id
  @GeneratedValue(strategy = IDENTITY)
  @Column(name = "country_id", nullable = false, updatable = false, unique = true)
  private Long countryId;

  @Column(name = "title", nullable = false, length = 100)
  private String title;

  @Column(name = "code", nullable = false, length = 5)
  private String code;

  @Column(name = "timezone", nullable = false, length = 100)
  private String timezone;
}
