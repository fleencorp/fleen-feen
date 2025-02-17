package com.fleencorp.feen.model.domain.calendar;

import com.fleencorp.feen.model.domain.base.FleenFeenEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static jakarta.persistence.GenerationType.IDENTITY;

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

  @Column(name = "is_active", nullable = false)
  private Boolean isActive = true;

  /**
   * Updates the title, description, and timezone of the object.
   *
   * @param title       the new title
   * @param description the new description
   * @param timezone    the new timezone
   */
  public void update(final String title, final String description, final String timezone) {
    this.title = title;
    this.description = description;
    this.timezone = timezone;
  }

  /**
   * Marks the calendar as active.
   */
  public void markAsActive() {
    isActive = true;
  }

  /**
   * Marks the calendar as inactive.
   */
  public void markAsInactive() {
    isActive = false;
  }
}
