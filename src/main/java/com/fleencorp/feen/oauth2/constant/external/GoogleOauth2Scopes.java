package com.fleencorp.feen.oauth2.constant.external;

/**
 * The {@code GoogleOauth2Scopes} class defines constants for various OAuth 2.0 scopes used
 * to authenticate and authorize access to Google services. These scopes are required for
 * different Google API services such as Google Chat, Firebase Cloud Messaging, and Cloud Platform.
 *
 * <p>This class is used to specify the appropriate OAuth 2.0 scopes needed by the application
 * to interact with Google services. Each constant represents a specific scope URL used in OAuth
 * authentication flows.</p>
 *
 * @author Yusuf Alamu Musa
 * @version 1.0
 */
public final class GoogleOauth2Scopes {

  private GoogleOauth2Scopes() {}

  public static final String CHAT_BOT = "https://www.googleapis.com/auth/chat.bot";
  public static final String CHAT_DELETE = "https://www.googleapis.com/auth/chat.delete";
  public static final String CHAT_MEMBERSHIPS = "https://www.googleapis.com/auth/chat.memberships";
  public static final String CHAT_MEMBERSHIPS_APP = "https://www.googleapis.com/auth/chat.memberships.app";
  public static final String CHAT_MEMBERSHIPS_READONLY = "https://www.googleapis.com/auth/chat.memberships.readonly";
  public static final String CHAT_MESSAGES = "https://www.googleapis.com/auth/chat.messages";
  public static final String CHAT_SPACES = "https://www.googleapis.com/auth/chat.spaces";
  public static final String CHAT_SPACES_CREATE = "https://www.googleapis.com/auth/chat.spaces.create";

  public static final String CLOUD_MESSAGING = "https://www.googleapis.com/auth/firebase.messaging";
  public static final String CLOUD_PLATFORM = "https://www.googleapis.com/auth/cloud-platform";

}
