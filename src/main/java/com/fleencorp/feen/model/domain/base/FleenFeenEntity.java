package com.fleencorp.feen.model.domain.base;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@MappedSuperclass
public class FleenFeenEntity {

  @CreatedDate
  @Column(name = "created_on", nullable = false, updatable = false)
  protected LocalDateTime createdOn;

  @LastModifiedDate
  @Column(name = "updated_on", nullable = false)
  protected LocalDateTime updatedOn;
}
