package com.fleencorp.feen.model.domain.base;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * Base class for entities with automatic auditing fields.
 *
 * <p>This class provides common auditing fields for entities, including
 * timestamps for creation and last modification. It is designed to be extended
 * by other entity classes.</p>
 *
 * @see <a href="https://velog.io/@yesrin/BaseEntity">
 *   BaseEntity (@CreatedDate, @LastModifiedDate)</a>
 * @see <a href="https://velog.io/@wonizizi99/SpringData-JPA-Auditing">
 *   [Spring] Data JPA, Applying Auditing and Implementing Auditing Directly
 * hyewon jeong · February 5, 2023</a>
 */
@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class FleenFeenEntity {

  @CreatedDate
  @Column(name = "created_on", nullable = false, updatable = false)
  protected LocalDateTime createdOn;

  @LastModifiedDate
  @Column(name = "updated_on", nullable = false)
  protected LocalDateTime updatedOn;
}
