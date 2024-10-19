package com.fleencorp.feen.configuration.external.google.api.calendar;

import com.fleencorp.base.util.JsonUtil;
import com.fleencorp.feen.configuration.external.google.api.GoogleApiConfiguration;
import com.fleencorp.feen.configuration.external.google.service.account.ServiceAccountProperties;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.security.GeneralSecurityException;

/**
 * This class provides configuration for interacting with Google Calendar API.
 * It includes methods for obtaining calendar instances, credentials, and other necessary configurations.
 *
 * @see <a href="https://velog.io/@minwest/%EC%84%9C%EB%B9%84%EC%8A%A4-%EA%B3%84%EC%A0%95%EC%9C%BC%EB%A1%9C-Google-Calendar-API-%EC%82%AC%EC%9A%A9%ED%95%98%EA%B8%B0">
 *   Using Google Calendar API with a service account</a>
 * @see <a href="https://velog.io/@minwest/%EC%84%9C%EB%B9%84%EC%8A%A4-%EA%B3%84%EC%A0%95%EC%9C%BC%EB%A1%9C-Google-Calendar-API-%EC%82%AC%EC%9A%A9%ED%95%98%EA%B8%B0-2-b9drsetp">
 *   Using Google Calendar API with a service account (2)</a>
 * @see <a href="https://sree394.medium.com/leveraging-service-accounts-for-seamless-google-calendar-integration-in-spring-boot-applications-52ee0d65652a">
 *   Leveraging Service Accounts for Seamless Google Calendar Integration in Spring Boot Applications</a>
 * @see <a href="https://developers.google.com/cloud-search/docs/guides/delegation">
 *   Perform Google Workspace domain-wide delegation of authority</a>
 *
 * @author Yusuf Alamu
 * @version 1.0
*/
@Configuration
@Slf4j
public class CalendarConfiguration extends GoogleApiConfiguration {


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
    super(delegatedAuthorityEmail, applicationName, serviceAccountProperties, jsonUtil);
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
    return new Calendar.Builder(getHttpTransport(), getJsonFactory(), getHttpCredentialsAdapter(CalendarScopes.all()))
      .setApplicationName(applicationName)
      .build();
  }

}
