package com.fleencorp.feen.model.domain.calendar;

import com.fleencorp.feen.model.domain.base.FleenFeenEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import static jakarta.persistence.GenerationType.IDENTITY;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "calendar", uniqueConstraints = {
  @UniqueConstraint(columnNames = {"code"})
})
public class Calendar extends FleenFeenEntity {

  @Id
  @GeneratedValue(strategy = IDENTITY)
  @Column(name = "calendar_id", nullable = false, updatable = false, unique = true)
  private Long calendarId;

  @Column(name = "external_id")
  private String externalId;

  @Column(name = "title", nullable = false, length = 300)
  private String title;

  @Column(name = "description", nullable = false, length = 1000)
  private String description;

  @Column(name = "timezone", nullable = false, length = 30)
  private String timezone;

  @Column(name = "code", nullable = false, length = 100)
  private String code;

  @Default
  @Column(name = "is_active", nullable = false)
  private Boolean isActive = true;

  public void update(final String title, final String description, final String timezone) {
    this.title = title;
    this.description = description;
    this.timezone = timezone;
  }
}
