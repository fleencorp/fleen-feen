package com.fleencorp.feen.configuration.external.google.api.chat;

import com.fleencorp.base.util.JsonUtil;
import com.fleencorp.feen.configuration.external.google.api.GoogleApiConfiguration;
import com.fleencorp.feen.configuration.external.google.service.account.ServiceAccountProperties;
import com.google.api.services.chat.v1.HangoutsChat;
import com.google.api.services.chat.v1.HangoutsChatScopes;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.security.GeneralSecurityException;

/**
 * Configuration class for setting up Google Hangouts Chat integration.
 *
 * <p>This class extends {@link GoogleApiConfiguration} to inherit shared
 * functionality related to Google API configurations. It provides
 * specific configuration for Google Hangouts Chat, such as setting
 * up credentials and API access.</p>
 *
 * @author Yusuf Alamu Musa
 * @version 1.0
 */
@Configuration
public class ChatConfiguration extends GoogleApiConfiguration {

  /**
   * Configuration class for setting up Google Hangouts Chat.
   *
   * @param delegatedAuthorityEmail The delegated authority email used for Google API access.
   * @param applicationName The name of the application.
   * @param serviceAccountProperties The service account properties required for authentication.
   * @param jsonUtil Utility for JSON operations.
   */
  public ChatConfiguration(
    @Value("${google.delegated.authority.email}") final String delegatedAuthorityEmail,
    @Value("${application.name}") final String applicationName,
    final ServiceAccountProperties serviceAccountProperties,
    final JsonUtil jsonUtil) {
    super(delegatedAuthorityEmail, applicationName, serviceAccountProperties, jsonUtil);
  }

  /**
   * Creates a HangoutsChat instance for interacting with Google Hangouts Chat API.
   *
   * @return A configured HangoutsChat instance.
   * @throws GeneralSecurityException if security credentials are invalid or compromised.
   * @throws IOException if there's an error with I/O operations.
   */
  @Bean
  public HangoutsChat getHangoutsChat() throws GeneralSecurityException, IOException {
    return new HangoutsChat.Builder(getHttpTransport(), getJsonFactory(), getHttpCredentialsAdapter(HangoutsChatScopes.all()))
      .setApplicationName(applicationName)
      .build();
  }

}
