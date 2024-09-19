package com.fleencorp.feen.model.domain.user;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

class RoleEntityTest {

  @DisplayName("Role is null")
  @Test
  void test_role_is_null() {
    // GIVEN
    Role role = new Role();

    // ASSERT
    assertNull(role.getRoleId());
    assertNull(role.getTitle());
    assertNull(role.getCode());
    assertNull(role.getDescription());
  }

  @DisplayName("Role is not null")
  @Test
  void test_role_is_not_null() {
    // GIVEN
    Role role = Role.builder()
                    .roleId(1L)
                    .title("Admin")
                    .code("ADMIN")
                    .description("Administrator role")
                    .build();

    // ASSERT
    assertNotNull(role.getRoleId());
    assertNotNull(role.getTitle());
    assertNotNull(role.getCode());
    assertNotNull(role.getDescription());
  }

  @DisplayName("Ensure Created Role is equal")
  @Test
  void test_created_role_is_equal() {
    // GIVEN
    Role role = Role.builder()
                    .roleId(1L)
                    .title("Admin")
                    .code("ADMIN")
                    .description("Administrator role")
                    .build();

    // ASSERT
    assertEquals(1L, role.getRoleId());
    assertEquals("Admin", role.getTitle());
    assertEquals("ADMIN", role.getCode());
    assertEquals("Administrator role", role.getDescription());
  }

  @DisplayName("Role is not null")
  @Test
  void test_role_is_not_equal() {
    // GIVEN
    Role role = Role.builder()
                    .roleId(1L)
                    .title("Admin")
                    .code("ADMIN")
                    .description("Administrator role")
                    .build();

    // ASSERT
    assertNotEquals(2L, role.getRoleId());
    assertNotEquals("User", role.getTitle());
    assertNotEquals("USER", role.getCode());
    assertNotEquals("User role", role.getDescription());
  }
}