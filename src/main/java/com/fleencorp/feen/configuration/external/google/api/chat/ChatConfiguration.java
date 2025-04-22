package com.fleencorp.feen.configuration.external.google.api.chat;

import com.fleencorp.base.util.JsonUtil;
import com.fleencorp.feen.configuration.external.google.api.GoogleApiConfiguration;
import com.fleencorp.feen.configuration.external.google.service.account.ServiceAccountProperties;
import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.chat.v1.ChatServiceClient;
import com.google.chat.v1.ChatServiceSettings;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.List;

import static com.fleencorp.feen.constant.external.google.GoogleOauth2Scopes.*;

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
   * Creates and returns a client for interacting with the chat service.
   *
   * <p>This method configures a {@link ChatServiceClient} using the chat service settings.
   * The settings include credentials obtained from a service account with specific
   * scopes required for chat operations, such as managing chat spaces, memberships,
   * and deleting chats.</p>
   *
   * <p>The client returned can be used to perform various chat-related
   * operations within the defined scopes.</p>
   *
   * @return a {@link ChatServiceClient} instance configured with the required credentials
   *         and scopes for interacting with the chat service.
   * @throws IOException if an error occurs while retrieving the credentials or creating
   *         the {@code ChatServiceClient}.
   */
  @Bean
  public ChatServiceClient chatService() throws IOException {
    final ChatServiceSettings settings = ChatServiceSettings.newBuilder()
      .setCredentialsProvider(FixedCredentialsProvider.create(getGoogleClientCredentialFromServiceAccount(
        List.of(CHAT_SPACES, CHAT_SPACES_CREATE,
                CHAT_MEMBERSHIPS, CHAT_MEMBERSHIPS_APP,
                CHAT_MEMBERSHIPS_READONLY, CHAT_DELETE))))
      .build();
    return ChatServiceClient.create(settings);
  }

  /**
   * Creates and returns a client for interacting with the chat service as a bot.
   *
   * <p>This method configures a {@link ChatServiceClient} using the chat service settings,
   * which include credentials obtained from a service account with specific scopes
   * necessary for bot operations in chat spaces.</p>
   *
   * <p>The client allows the bot to manage
   * chat spaces, send messages, and interact with chat bots within the defined scopes.</p>
   *
   * @return a {@link ChatServiceClient} instance configured with the required credentials
   *         and scopes for bot-related operations in the chat service.
   * @throws IOException if an error occurs while retrieving the credentials or creating
   *         the {@code ChatServiceClient}.
   */
  @Bean("chatBot")
  public ChatServiceClient chatServiceBot() throws IOException {
    final ChatServiceSettings settings = ChatServiceSettings.newBuilder()
      .setCredentialsProvider(FixedCredentialsProvider.create(getGoogleClientCredentialFromServiceAccountBase(
        List.of(CHAT_SPACES, CHAT_MESSAGES, CHAT_BOT))))
      .build();
    return ChatServiceClient.create(settings);
  }

}
