package com.fleencorp.feen.model.domain.auth;

import com.fleencorp.feen.user.model.domain.Oauth2Authorization;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class Oauth2AuthorizationTest {

  @Test
  @DisplayName("Create new oauth2 authorization")
  void create_new_oauth2_authorization() {
    // given
    final Oauth2Authorization oauth2Authorization = new Oauth2Authorization();

    // then
    assertNotNull(oauth2Authorization);
  }

  @Test
  @DisplayName("Create empty oauth2 authorization")
  void create_empty_oauth2_authorization_null() {
    // given
    final Oauth2Authorization oauth2Authorization = null;

    // then
    assertNull(oauth2Authorization);
  }

  @Test
  @DisplayName("Create oauth2 authorization without id")
  void create_oauth2_authorization_without_id() {
    // given
    final Oauth2Authorization oauth2Authorization = new Oauth2Authorization();

    // then
    assertNull(oauth2Authorization.getOauth2AuthorizationId());
  }

  @Test
  @DisplayName("Create oauth2 authorization with id")
  void create_oauth2_authorization_with_id() {
    // given
    final Oauth2Authorization oauth2Authorization = new Oauth2Authorization();
    oauth2Authorization.setOauth2AuthorizationId(1L);

    // then
    assertNotNull(oauth2Authorization.getOauth2AuthorizationId());
  }

  @Test
  @DisplayName("Create oauth2 authorization ids are equal")
  void ensure_oauth2_authorization_id_are_equal() {
    // given
    final long oauth2AuthorizationId = 1L;
    final Oauth2Authorization oauth2Authorization1 = new Oauth2Authorization();
    oauth2Authorization1.setOauth2AuthorizationId(1L);

    // then
    assertNotNull(oauth2Authorization1);
    assertEquals(oauth2AuthorizationId, oauth2Authorization1.getOauth2AuthorizationId());
  }

  @Test
  @DisplayName("Create oauth2 authorization without access token")
  void create_oauth2_authorization_without_access_token() {
    // given
    final Oauth2Authorization oauth2Authorization = new Oauth2Authorization();

    // then
    assertNull(oauth2Authorization.getAccessToken());
  }

  @Test
  @DisplayName("Create oauth2 authorization with access token")
  void create_oauth2_authorization_with_access_token() {
    // given
    final Oauth2Authorization oauth2Authorization = new Oauth2Authorization();
    oauth2Authorization.setAccessToken("accessToken");

    // then
    assertNotNull(oauth2Authorization.getAccessToken());
  }

  @Test
  @DisplayName("Ensure oauth2 authorization access tokens are equal")
  void ensure_oauth2_authorization_access_token_are_equal() {
    // given
    final String accessToken = "accessToken";
    final Oauth2Authorization oauth2Authorization1 = new Oauth2Authorization();
    oauth2Authorization1.setAccessToken("accessToken");

    // then
    assertNotNull(oauth2Authorization1);
    assertEquals(accessToken, oauth2Authorization1.getAccessToken());
  }

  @Test
  @DisplayName("Ensure oauth2 authorization access tokens are not equal")
  void ensure_oauth2_authorization_access_token_are_not_equal() {
    // given
    final String accessToken = "accessToken";
    final Oauth2Authorization oauth2Authorization1 = new Oauth2Authorization();
    oauth2Authorization1.setAccessToken("tokenAccess");

    // then
    assertNotEquals(accessToken, oauth2Authorization1.getAccessToken());
  }

  @Test
  @DisplayName("Create oauth2 authorization without refresh token")
  void create_oauth2_authorization_without_refresh_token() {
    // given
    final Oauth2Authorization oauth2Authorization = new Oauth2Authorization();

    // then
    assertNull(oauth2Authorization.getRefreshToken());
  }

  @Test
  @DisplayName("Create oauth2 authorization with refresh token")
  void create_oauth2_authorization_with_refresh_token() {
    final Oauth2Authorization oauth2Authorization = new Oauth2Authorization();
    oauth2Authorization.setRefreshToken("refreshToken");

    assertNotNull(oauth2Authorization.getRefreshToken());
  }

  @Test
  @DisplayName("Ensure oauth2 authorization refresh tokens are equal")
  void ensure_oauth2_authorization_refresh_token_are_equal() {
    // given
    final String refreshToken = "refreshToken";
    final Oauth2Authorization oauth2Authorization1 = new Oauth2Authorization();
    oauth2Authorization1.setRefreshToken(refreshToken);

    // then
    assertNotNull(oauth2Authorization1);
    assertEquals(refreshToken, oauth2Authorization1.getRefreshToken());
  }

  @Test
  @DisplayName("Ensure oauth2 authorization refresh tokens are not equal")
  void ensure_oauth2_authorization_refresh_token_are_not_equal() {
    // given
    final String refreshToken = "refreshToken";
    final Oauth2Authorization oauth2Authorization1 = new Oauth2Authorization();
    oauth2Authorization1.setRefreshToken("tokenRefresh");

    // then
    assertNotNull(oauth2Authorization1);
    assertNotEquals(refreshToken, oauth2Authorization1.getRefreshToken());
  }

}