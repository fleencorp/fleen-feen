package com.fleencorp.feen.calendar.model.domain;

import com.fleencorp.feen.calendar.constant.CalendarStatus;
import com.fleencorp.feen.calendar.exception.core.CalendarAlreadyActiveException;
import com.fleencorp.feen.model.domain.base.FleenFeenEntity;
import com.fleencorp.feen.shared.calendar.contract.IsACalendar;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.GenerationType.IDENTITY;
import static java.util.Objects.nonNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "calendar", uniqueConstraints = {
  @UniqueConstraint(columnNames = {"code"})
})
public class Calendar extends FleenFeenEntity
  implements IsACalendar {

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

  @Enumerated(STRING)
  @Column(name = "status", nullable = false)
  private CalendarStatus status;

  public void markAsActive() {
    status = CalendarStatus.ACTIVE;
  }

  public void markAsInactive() {
    status = CalendarStatus.INACTIVE;
  }

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
   * Verifies that the calendar is not already active.
   *
   * <p>If the calendar has a non-null status and the status indicates it is active,
   * a {@link CalendarAlreadyActiveException} is thrown.</p>
   *
   * @throws CalendarAlreadyActiveException if the calendar is already active
   */
  public void verifyCalendarIsNotAlreadyActive() {
    if (nonNull(status) && CalendarStatus.isActive(status)) {
      throw new CalendarAlreadyActiveException();
    }
  }
}
