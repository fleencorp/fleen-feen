package com.fleencorp.feen.configuration.external.google.calendar;

import com.fleencorp.feen.configuration.external.google.service.account.ServiceAccountDto;
import com.fleencorp.feen.configuration.external.google.service.account.ServiceAccountProperties;
import com.fleencorp.feen.util.JsonUtil;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;

/**
* This class provides configuration for interacting with Google Calendar API.
* It includes methods for obtaining calendar instances, credentials, and other necessary configurations.
*
* @author Yusuf Alamu
* @version 1.0
*
* @see <a href="https://velog.io/@minwest/%EC%84%9C%EB%B9%84%EC%8A%A4-%EA%B3%84%EC%A0%95%EC%9C%BC%EB%A1%9C-Google-Calendar-API-%EC%82%AC%EC%9A%A9%ED%95%98%EA%B8%B0">
*   Using Google Calendar API with a service account</a>
* @see <a href="https://velog.io/@minwest/%EC%84%9C%EB%B9%84%EC%8A%A4-%EA%B3%84%EC%A0%95%EC%9C%BC%EB%A1%9C-Google-Calendar-API-%EC%82%AC%EC%9A%A9%ED%95%98%EA%B8%B0-2-b9drsetp">
*   Using Google Calendar API with a service account (2)</a>
* @see <a href="https://sree394.medium.com/leveraging-service-accounts-for-seamless-google-calendar-integration-in-spring-boot-applications-52ee0d65652a">
*   Leveraging Service Accounts for Seamless Google Calendar Integration in Spring Boot Applications</a>
*
* @see <a href="https://developers.google.com/cloud-search/docs/guides/delegation">
*   Perform Google Workspace domain-wide delegation of authority</a>
*/
@Configuration
@Slf4j
public class CalendarConfiguration {

  private final String applicationName;
  private final String delegatedAuthorityEmail;
  private final ServiceAccountProperties serviceAccountProperties;
  private final JsonUtil jsonUtil;

  /**
   * Constructs a new CalendarConfiguration instance with the provided dependencies.
   *
   * @param delegatedAuthorityEmail   The email address associated with the Google Calendar
   *                                  and with delegated authority.
   * @param applicationName           The name of the application
   * @param serviceAccountProperties  The properties required for service account authentication.
   * @param jsonUtil                  Utility class for JSON operations.
   */
  public CalendarConfiguration(
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
   * Retrieves a Google Calendar instance with configured properties.
   *
   * @return A Calendar instance configured for interacting with Google Calendar API.
   * @throws GeneralSecurityException If a security exception occurs during transport initialization.
   * @throws IOException              If an I/O exception occurs.
   */
  @Bean
  public Calendar getCalendar() throws GeneralSecurityException, IOException {
    return new Calendar.Builder(CalendarConfiguration.getHttpTransport(), CalendarConfiguration.getJsonFactory(), getHttpCredentialsAdapter())
      .setApplicationName(applicationName)
      .build();
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
   */
  public GoogleCredentials getGoogleClientCredentialFromServiceAccount() throws IOException {
    return GoogleCredentials
      .fromStream(getServiceAccountInputStream())
      .createScoped(CalendarScopes.all())
      .createDelegated(delegatedAuthorityEmail);
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
   * Retrieves HTTP credentials adapter from Google client credentials.
   *
   * @return HttpCredentialsAdapter configured with Google client credentials.
   * @throws IOException If an I/O exception occurs while creating HttpCredentialsAdapter.
   */
  public HttpCredentialsAdapter getHttpCredentialsAdapter() throws IOException {
    return new HttpCredentialsAdapter(getGoogleClientCredentialFromServiceAccount());
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
