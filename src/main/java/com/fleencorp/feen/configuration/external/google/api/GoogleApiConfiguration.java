package com.fleencorp.feen.configuration.external.google.api;

import com.fleencorp.base.util.JsonUtil;
import com.fleencorp.feen.configuration.external.google.service.account.ServiceAccountDto;
import com.fleencorp.feen.configuration.external.google.service.account.ServiceAccountProperties;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.Collection;

/**
 * Base configuration class for setting up Google API integrations.
 *
 * <p>This class provides common configuration elements such as authentication,
 * credentials, and utility methods for accessing Google APIs.
 * It is intended to be extended by specific API configuration classes
 * (e.g., Google Calendar, Hangouts Chat) to avoid duplication of shared logic.</p>
 *
 * @author Yusuf Alamu Musa
 * @version 1.0
 */
@Component
public class GoogleApiConfiguration {

  protected final String applicationName;
  protected final String delegatedAuthorityEmail;
  protected final ServiceAccountProperties serviceAccountProperties;
  protected final JsonUtil jsonUtil;

  /**
   * Constructor for setting up common Google API configuration details.
   *
   * @param delegatedAuthorityEmail the email of the delegated authority used for API authentication
   * @param applicationName         the name of the application integrating with Google APIs
   * @param serviceAccountProperties the properties for accessing the Google service account
   * @param jsonUtil                a utility for handling JSON operations
   */
  public GoogleApiConfiguration(
      @Value("${google.delegated.authority.email}") final String delegatedAuthorityEmail,
      @Value("${application.name}") final String applicationName,
      final ServiceAccountProperties serviceAccountProperties,
      final JsonUtil jsonUtil) {
    this.delegatedAuthorityEmail = delegatedAuthorityEmail;
    this.applicationName = applicationName;
    this.serviceAccountProperties = serviceAccountProperties;
    this.jsonUtil = jsonUtil;
  }

  /**
   * Retrieves Google client credentials from the service account properties.
   *
   * @return GoogleCredentials object containing the client credentials.
   * @throws IOException If an I/O exception occurs while reading service account properties.
   *
   * @see <a href="https://medium.com/@gkav2022/google-calendar-integration-with-springboot-application-cddf361a3406">
   *   Google calendar integration with SpringBoot Application using Service Account</a>
   * @see <a href="https://developers.google.com/identity/protocols/oauth2/service-account">
   *   Using OAuth 2.0 for Server to Server Applications - Delegating domain-wide authority to the service account</a>
   * @see <a href="https://medium.com/iceapple-tech-talks/integration-with-google-calendar-api-using-service-account-1471e6e102c8">
   *   Integration with Google Calendar API using Service Account</a>
   */
  public GoogleCredentials getGoogleClientCredentialFromServiceAccount(final Collection<String> scopes) throws IOException {
    return getGoogleClientCredentialFromServiceAccountBase(scopes)
      .createDelegated(delegatedAuthorityEmail);
  }

  /**
   * Retrieves Google client credentials from a service account file.
   *
   * <p>This method reads the service account credentials from an input stream and
   * creates a scoped {@link GoogleCredentials} instance based on the provided
   * scopes. The credentials are used to authenticate requests to Google services
   * that require OAuth 2.0 authentication.</p>
   *
   * @param scopes a collection of scopes required for the authentication.
   * @return a {@link GoogleCredentials} object scoped with the provided permissions.
   * @throws IOException if there is an error reading the service account credentials
   *         from the input stream.
   */
  public GoogleCredentials getGoogleClientCredentialFromServiceAccountBase(final Collection<String> scopes) throws IOException {
    return GoogleCredentials
      .fromStream(getServiceAccountInputStream())
      .createScoped(scopes);
  }

  /**
   * Retrieves an input stream from service account properties.
   *
   * @return ByteArrayInputStream containing service account properties JSON.
   *
   * @see <a href="https://stackoverflow.com/questions/54886906/create-google-calendar-event-from-java-backend-from-oauth-authenticated-users-on">
   *   Create Google Calendar Event from Java Backend from OAuth authenticated Users only</a>
   */
  public ByteArrayInputStream getServiceAccountInputStream() {
    final ServiceAccountDto serviceAccountDto = ServiceAccountDto.fromServiceAccountProperties(serviceAccountProperties);
    final String serviceAccountPropertiesJson = toJson(serviceAccountDto);
    return new ByteArrayInputStream(serviceAccountPropertiesJson.getBytes(StandardCharsets.UTF_8));
  }

  /**
   * Converts the provided Java object into its JSON representation.
   * Handles special cases, such as replacing escaped newline characters (`\\n`) with actual newline characters (`\n`).
   *
   * @param value The Java object to be converted to JSON.
   * @return A JSON string representation of the provided object.
   */
  public String toJson(final Object value) {
    // Convert the object to a JSON string
    final String payload = jsonUtil.convertToString(value);
    // Replace escaped newline characters with actual newline characters
    return payload.replaceAll("\\\\n", "\n");
  }

  /**
   * Retrieves HTTP credentials adapter from Google client credentials.
   *
   * @return HttpCredentialsAdapter configured with Google client credentials.
   * @throws IOException If an I/O exception occurs while creating HttpCredentialsAdapter.
   */
  public HttpCredentialsAdapter getHttpCredentialsAdapter(final Collection<String> scopes) throws IOException {
    return new HttpCredentialsAdapter(getGoogleClientCredentialFromServiceAccount(scopes));
  }

  /**
   * Retrieves JSON factory for JSON operations.
   *
   * @return JsonFactory instance.
   */
  public static JsonFactory getJsonFactory() {
    return GsonFactory.getDefaultInstance();
  }

  /**
   * Retrieves HTTP transport for Google API calls.
   *
   * @return NetHttpTransport instance.
   * @throws GeneralSecurityException If a security exception occurs during transport initialization.
   * @throws IOException              If an I/O exception occurs.
   */
  public static NetHttpTransport getHttpTransport() throws GeneralSecurityException, IOException {
    return GoogleNetHttpTransport.newTrustedTransport();
  }
}
