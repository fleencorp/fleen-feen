package com.fleencorp.feen.common.constant.external.google.chat.base;

import com.fleencorp.base.constant.base.ApiParameter;
import lombok.Getter;

/**
 * Enum representing various Google Chat API parameters.
 *
 * <p>This enum contains constants that represent key parameters or URIs
 * used for interacting with Google Chat services. Each constant provides
 * an associated value that can be retrieved using {@code getValue()}.</p>
 *
 * @author Yusuf Alamu Musa
 * @version 1.0
 */
@Getter
public enum GoogleChatParameter implements ApiParameter {

  CHAT_SPACE_BASE_URI("https://chat.google.com/room/"),
  SPACES("spaces"),
  MEMBERS("members"),
  USERS("users");

  private final String value;

  GoogleChatParameter(final String value) {
    this.value = value;
  }

  /**
   * Retrieves the value for the "spaces" endpoint.
   *
   * <p>This method returns the value associated with the spaces endpoint
   * for use in constructing URIs or API requests.</p>
   *
   * @return the value representing the spaces endpoint
   */
  public static String spaces() {
    return SPACES.getValue();
  }

  /**
   * Retrieves the value for the "members" endpoint.
   *
   * <p>This method returns the value associated with the members endpoint
   * for use in constructing URIs or API requests.</p>
   *
   * @return the value representing the members endpoint
   */
  public static String members() {
    return MEMBERS.getValue();
  }

  /**
   * Retrieves the base URI for the chat space.
   *
   * <p>This method returns the base URI used when constructing chat space-related URIs.</p>
   *
   * @return the base URI for chat spaces
   */
  public static String chatSpaceBaseUri() {
    return CHAT_SPACE_BASE_URI.getValue();
  }

  /**
   * Retrieves the value for the "users" endpoint.
   *
   * <p>This method returns the value associated with the users endpoint
   * for use in constructing URIs or API requests.</p>
   *
   * @return the value representing the users endpoint
   */
  public static String users() {
    return USERS.getValue();
  }


}
