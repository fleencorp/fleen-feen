package com.fleencorp.feen.configuration.external.google.notification;

import com.fleencorp.base.util.JsonUtil;
import com.fleencorp.feen.configuration.external.google.api.GoogleApiConfiguration;
import com.fleencorp.feen.configuration.external.google.service.account.ServiceAccountProperties;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.List;

import static com.fleencorp.feen.constant.external.google.GoogleOauth2Scopes.CLOUD_MESSAGING;
import static com.fleencorp.feen.constant.external.google.GoogleOauth2Scopes.CLOUD_PLATFORM;

/**
 * The {@code FirebaseConfiguration} class is responsible for configuring Firebase services
 * in the application. It initializes Firebase options and sets up the required credentials
 * and configurations for Firebase services such as Firebase Messaging.
 *
 * <p>This configuration makes use of a service account and delegated authority email
 * to authenticate with Firebase and Google Cloud services. It also integrates the
 * application name into the Firebase setup.</p>
 *
 * <p>Methods in this class are designed to provide {@link FirebaseMessaging} and configure
 * Firebase with the appropriate Google credentials.</p>
 *
 * @author Yusuf Alamu Musa
 * @version 1.0
 */
@Configuration
public class FirebaseConfiguration extends GoogleApiConfiguration {

  /**
   * Constructs a {@link FirebaseConfiguration} instance, initializing the base configuration
   * for Firebase services using provided delegated authority email, application name,
   * service account properties, and JSON utilities.
   *
   * @param delegatedAuthorityEmail the delegated authority email address for Google services
   * @param applicationName the name of the application
   * @param serviceAccountProperties the service account properties required for Google Cloud
   * @param jsonUtil a utility for JSON processing
   */
  public FirebaseConfiguration(
      @Value("${google.delegated.authority.email}") final String delegatedAuthorityEmail,
      @Value("${application.name}") final String applicationName,
      final ServiceAccountProperties serviceAccountProperties,
      final JsonUtil jsonUtil) {
    super(delegatedAuthorityEmail, applicationName, serviceAccountProperties, jsonUtil);
  }

  /**
   * Initializes FirebaseApp and retrieves an instance of FirebaseMessaging.
   *
   * @return {@link FirebaseMessaging} instance for interacting with Firebase Cloud Messaging.
   * @throws IOException if there is an issue initializing the Firebase options.
   */
  @Bean
  public FirebaseMessaging firebaseMessaging() throws IOException {
    // Initialize the Firebase application using the configured Firebase options
    final FirebaseApp firebaseApp = FirebaseApp.initializeApp(firebaseOptions(), applicationName);
    // Return the FirebaseMessaging instance associated with the initialized Firebase app
    return FirebaseMessaging.getInstance(firebaseApp);
  }

  /**
   * Creates and configures Firebase options using service account credentials.
   *
   * @return {@link FirebaseOptions} with the specified credentials and options.
   * @throws IOException if there is an issue retrieving the Google client credentials.
   */
  protected FirebaseOptions firebaseOptions() throws IOException {
    // Set credentials by retrieving them from the service account with required scopes
    return FirebaseOptions.builder()
      .setCredentials(getGoogleClientCredentialFromServiceAccountBase(List.of(CLOUD_PLATFORM, CLOUD_MESSAGING)))
      .build();
  }
}
