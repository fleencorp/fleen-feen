package com.fleencorp.feen.model.domain.auth;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class Oauth2AuthorizationTest {

  @Test
  void create_empty_oauth2_authorization() {
    // GIVEN
    final Oauth2Authorization oauth2Authorization = new Oauth2Authorization();

    // ASSERT
    assertNotNull(oauth2Authorization);
  }

  @Test
  void create_empty_oauth2_authorization_null() {
    // GIVEN
    final Oauth2Authorization oauth2Authorization = null;

    // ASSERT
    assertNull(oauth2Authorization);
  }

  @Test
  void create_oauth2_authorization_without_id() {
    // GIVEN
    final Oauth2Authorization oauth2Authorization = new Oauth2Authorization();

    // ASSERT
    assertNull(oauth2Authorization.getOauth2AuthorizationId());
  }

  @Test
  void create_oauth2_authorization_with_id() {
    // GIVEN
    final Oauth2Authorization oauth2Authorization = new Oauth2Authorization();
    oauth2Authorization.setOauth2AuthorizationId(1L);

    // ASSERT
    assertNotNull(oauth2Authorization.getOauth2AuthorizationId());
  }

  @Test
  void ensure_oauth2_authorization_id_are_equal() {
    // GIVEN
    final long oauth2AuthorizationId = 1L;
    final Oauth2Authorization oauth2Authorization1 = new Oauth2Authorization();
    oauth2Authorization1.setOauth2AuthorizationId(1L);

    // ASSERT
    assertNotNull(oauth2Authorization1);
    assertEquals(oauth2AuthorizationId, oauth2Authorization1.getOauth2AuthorizationId());
  }

  @Test
  void create_oauth2_authorization_without_access_token() {
    // GIVEN
    final Oauth2Authorization oauth2Authorization = new Oauth2Authorization();

    // ASSERT
    assertNull(oauth2Authorization.getAccessToken());
  }

  @Test
  void create_oauth2_authorization_with_access_token() {
    // GIVEN
    final Oauth2Authorization oauth2Authorization = new Oauth2Authorization();
    oauth2Authorization.setAccessToken("accessToken");

    // ASSERT
    assertNotNull(oauth2Authorization.getAccessToken());
  }

  @Test
  void ensure_oauth2_authorization_access_token_are_equal() {
    // GIVEN
    final String accessToken = "accessToken";
    final Oauth2Authorization oauth2Authorization1 = new Oauth2Authorization();
    oauth2Authorization1.setAccessToken("accessToken");

    // ASSERT
    assertNotNull(oauth2Authorization1);
    assertEquals(accessToken, oauth2Authorization1.getAccessToken());
  }

  @Test
  void ensure_oauth2_authorization_access_token_are_not_equal() {
    // GIVEN
    final String accessToken = "accessToken";
    final Oauth2Authorization oauth2Authorization1 = new Oauth2Authorization();
    oauth2Authorization1.setAccessToken("tokenAccess");

    // ASSERT
    assertNotEquals(accessToken, oauth2Authorization1.getAccessToken());
  }

  @Test
  void create_oauth2_authorization_without_refresh_token() {
    // GIVEN
    final Oauth2Authorization oauth2Authorization = new Oauth2Authorization();

    // ASSERT
    assertNull(oauth2Authorization.getRefreshToken());
  }

  @Test
  void create_oauth2_authorization_with_refresh_token() {
    final Oauth2Authorization oauth2Authorization = new Oauth2Authorization();
    oauth2Authorization.setRefreshToken("refreshToken");

    assertNotNull(oauth2Authorization.getRefreshToken());
  }

  @Test
  void ensure_oauth2_authorization_refresh_token_are_equal() {
    // GIVEN
    final String refreshToken = "refreshToken";
    final Oauth2Authorization oauth2Authorization1 = new Oauth2Authorization();
    oauth2Authorization1.setRefreshToken(refreshToken);

    // ASSERT
    assertNotNull(oauth2Authorization1);
    assertEquals(refreshToken, oauth2Authorization1.getRefreshToken());
  }

  @Test
  void ensure_oauth2_authorization_refresh_token_are_not_equal() {
    // GIVEN
    final String refreshToken = "refreshToken";
    final Oauth2Authorization oauth2Authorization1 = new Oauth2Authorization();
    oauth2Authorization1.setRefreshToken("tokenRefresh");

    // ASSERT
    assertNotNull(oauth2Authorization1);
    assertNotEquals(refreshToken, oauth2Authorization1.getRefreshToken());
  }

}