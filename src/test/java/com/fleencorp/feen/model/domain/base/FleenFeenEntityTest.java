package com.fleencorp.feen.model.domain.base;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class FleenFeenEntityTest {

  @DisplayName("Create empty FleenFeenEntity")
  @Test
  void create_empty_fleen_feen_entity() {
    // GIVEN
    FleenFeenEntity fleenFeenEntity = new FleenFeenEntity();

    // ASSERT
    assertNotNull(fleenFeenEntity);
  }

  @DisplayName("Create a null FleenFeenEntity")
  @Test
  void create_null_fleen_feen_entity() {
    // GIVEN
    FleenFeenEntity fleenFeenEntity = null;

    // ASSERT
    assertNull(fleenFeenEntity);
  }

  @DisplayName("Ensure Created Ons are equal")
  @Test
  void ensure_created_on_are_equal() {
    // GIVEN
    LocalDateTime createdOn = LocalDateTime.now();
    FleenFeenEntity fleenFeenEntity = new FleenFeenEntity();
    fleenFeenEntity.setCreatedOn(createdOn);

    // ASSERT
    assertEquals(createdOn, fleenFeenEntity.getCreatedOn());
  }

  @DisplayName("Ensure Updated Ons are equal")
  @Test
  void ensure_updated_on_are_equal() {
    // GIVEN
    LocalDateTime updatedOn = LocalDateTime.now();
    FleenFeenEntity fleenFeenEntity = new FleenFeenEntity();
    fleenFeenEntity.setUpdatedOn(updatedOn);

    // ASSERT
    assertEquals(updatedOn, fleenFeenEntity.getUpdatedOn());
  }

  @DisplayName("Ensure Created Ons are not equal")
  @Test
  void ensure_created_on_are_not_equal() {
    // GIVEN
    LocalDateTime createdOn = LocalDateTime.now();
    FleenFeenEntity fleenFeenEntity = new FleenFeenEntity();
    fleenFeenEntity.setCreatedOn(LocalDateTime.now());

    // ASSERT
    assertNotEquals(createdOn, fleenFeenEntity.getCreatedOn());
  }

  @DisplayName("Ensure Updated Ons are not equal")
  @Test
  void ensure_updated_on_are_not_equal() {
    // GIVEN
    LocalDateTime updatedOn = LocalDateTime.now();
    FleenFeenEntity fleenFeenEntity = new FleenFeenEntity();
    fleenFeenEntity.setUpdatedOn(LocalDateTime.now());

    // ASSERT
    assertNotEquals(updatedOn, fleenFeenEntity.getUpdatedOn());
  }

  @DisplayName("Ensure Created On and Updated On are null")
  @Test
  void ensure_created_on_and_updated_on_are_null() {
    // GIVEN
    FleenFeenEntity fleenFeenEntity = new FleenFeenEntity();
    fleenFeenEntity.setCreatedOn(null);
    fleenFeenEntity.setUpdatedOn(null);

    // ASSERT
    assertNull(fleenFeenEntity.getCreatedOn());
    assertNull(fleenFeenEntity.getUpdatedOn());
  }

  @DisplayName("Ensure Created On and Updated On are not null")
  @Test
  void ensure_created_on_and_updated_on_are_not_null() {
    // GIVEN
    FleenFeenEntity fleenFeenEntity = new FleenFeenEntity();
    fleenFeenEntity.setCreatedOn(LocalDateTime.now());
    fleenFeenEntity.setUpdatedOn(LocalDateTime.now());

    // ASSERT
    assertNotNull(fleenFeenEntity.getCreatedOn());
    assertNotNull(fleenFeenEntity.getUpdatedOn());
  }
}