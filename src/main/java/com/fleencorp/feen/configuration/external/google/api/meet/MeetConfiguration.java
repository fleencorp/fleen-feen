package com.fleencorp.feen.configuration.external.google.api.meet;

import com.fleencorp.base.util.JsonUtil;
import com.fleencorp.feen.configuration.external.google.api.GoogleApiConfiguration;
import com.fleencorp.feen.configuration.external.google.service.account.ServiceAccountProperties;
import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.apps.meet.v2beta.SpacesServiceClient;
import com.google.apps.meet.v2beta.SpacesServiceSettings;
import com.google.auth.oauth2.GoogleCredentials;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

import java.io.IOException;
import java.util.Set;

/**
 * This class provides configuration for interacting with Google Meet API.
 * It includes methods for obtaining meet space instances, credentials, and other necessary configurations.
 *
 * @see <a href="https://developers.google.com/cloud-search/docs/guides/delegation">
 *   Perform Google Workspace domain-wide delegation of authority</a>
 *
 * @author Yusuf Alamu
 * @version 1.0
*/
@Slf4j
public class MeetConfiguration extends GoogleApiConfiguration {


  /**
   * Constructs a new MeetConfiguration instance with the provided dependencies.
   *
   * @param delegatedAuthorityEmail   The email address associated with the Google Meet
   *                                  and with delegated authority.
   * @param applicationName           The name of the application
   * @param serviceAccountProperties  The properties required for service account authentication.
   * @param jsonUtil                  Utility class for JSON operations.
   */
  public MeetConfiguration(
      @Value("${google.delegated.authority.email}") final String delegatedAuthorityEmail,
      @Value("${application.name}") final String applicationName,
      final ServiceAccountProperties serviceAccountProperties,
      final JsonUtil jsonUtil) {
    super(delegatedAuthorityEmail, applicationName, serviceAccountProperties, jsonUtil);
  }


  /**
   * Creates and returns a client for interacting with the Google Spaces Service API.
   *
   * <p>This method initializes the {@link SpacesServiceClient} by obtaining the required Google API credentials
   * from a service account and applying the necessary scopes for accessing the Google Meetings and Drive APIs.
   * The client can then be used to interact with Google Spaces and related resources, such as creating, reading,
   * or managing spaces.</p>
   *
   * @return a {@link SpacesServiceClient} instance configured with the required credentials and settings
   * @throws IOException if an error occurs while retrieving the credentials or creating the service client
   */
  @Bean
  public SpacesServiceClient getSpaceService() throws IOException {
    final Set<String> scopes = Set.of(
      "https://www.googleapis.com/auth/meetings.space.settings",
      "https://www.googleapis.com/auth/meetings.space.created",
      "https://www.googleapis.com/auth/meetings.space.readonly",
      "https://www.googleapis.com/auth/drive.readonly"
    );
    final GoogleCredentials credentials = getGoogleClientCredentialFromServiceAccount(scopes);
    final SpacesServiceSettings serviceSettings = SpacesServiceSettings.newBuilder()
      .setCredentialsProvider(FixedCredentialsProvider.create(credentials))
      .build();

    return SpacesServiceClient.create(serviceSettings);
  }

}
