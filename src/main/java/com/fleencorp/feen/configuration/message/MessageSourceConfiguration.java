package com.fleencorp.feen.configuration.message;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;

import java.util.Locale;

/**
 * Configuration class for setting up message sources in the application.
 *
 * <p>This class is responsible for configuring various {@link MessageSource} beans used throughout the application
 * for internationalization (i18n) and message resolution. It defines beans such as {@link ReloadableResourceBundleMessageSource}
 * for different types of messages like standard messages, error messages, and response messages.</p>
 *
 * @author Yusuf Alamu Musa
 * @version 1.0
 */
@Configuration
public class MessageSourceConfiguration {

  /**
   * Creates and configures a ReloadableResourceBundleMessageSource.
   *
   * <p>This method initializes a ReloadableResourceBundleMessageSource with specific settings:
   * it sets the cache duration to 60 seconds, the default locale to US English, disables using
   * code as the default message, prevents fallback to the system locale, and ensures that message
   * formatting is always used.</p>
   *
   * @return a configured instance of ReloadableResourceBundleMessageSource.
   *
   * @see <a href="https://velog.io/@maketheworldwise/%EB%8B%A4%EA%B5%AD%EC%96%B4-%EC%B2%98%EB%A6%AC%EC%9D%98-%EB%AA%A8%EB%93%A0-%EA%B2%83">
   *   Everything about multilingual processing!</a>
   */
  private ReloadableResourceBundleMessageSource baseMessageSource() {
    final ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
    messageSource.setCacheSeconds(60);
    messageSource.setDefaultLocale(Locale.US);
    messageSource.setUseCodeAsDefaultMessage(false);
    messageSource.setFallbackToSystemLocale(false);
    messageSource.setAlwaysUseMessageFormat(true);
    return messageSource;
  }

  /**
   * Configures and provides the primary {@link MessageSource} bean.
   *
   * <p>This method creates a {@link ReloadableResourceBundleMessageSource} bean using the base settings
   * from {@link #baseMessageSource()}. It sets the message base name and encoding based on the provided
   * configuration properties.</p>
   *
   * @param baseName the base name of the message resource bundle, typically defined in the application's configuration.
   * @param encoding the encoding to be used for reading the message files.
   * @return a configured {@link ReloadableResourceBundleMessageSource} bean.
   *
   * @see <a href="https://velog.io/@ksk7584/%EB%A9%94%EC%8B%9C%EC%A7%80-%EA%B5%AD%EC%A0%9C%ED%99%94">
   *   Messages, Internationalization</a>
   */
  @Bean
  @Primary
  public MessageSource messageSource(
      @Value("${spring.messages.message.base-name}") final String baseName,
      @Value("${spring.messages.encoding}") final String encoding) {
    final ReloadableResourceBundleMessageSource messageSource = baseMessageSource();
    messageSource.setBasenames(baseName);
    messageSource.setDefaultEncoding(encoding);
    return messageSource;
  }

  /**
   * Configures and provides a {@link MessageSource} bean for error messages.
   *
   * <p>This method creates a {@link ReloadableResourceBundleMessageSource} bean specifically for handling
   * error messages. It uses the base settings from {@link #baseMessageSource()} and sets the message base
   * name and encoding according to the provided configuration properties.</p>
   *
   * @param baseName the base name of the error message resource bundle, typically defined in the application's configuration.
   * @param encoding the encoding to be used for reading the error message files.
   * @return a configured {@link ReloadableResourceBundleMessageSource} bean for error messages.
   *
   * @see <a href="https://velog.io/@mini-boo/DB%EB%A5%BC-%ED%86%B5%ED%95%9C-%EB%8B%A4%EA%B5%AD%EC%96%B4-%EC%A7%80%EC%9B%90-%EA%B8%B0%EB%8A%A5">
   *   Implementation of multilingual support through DB</a>
   */
  @Bean("error-message-source")
  public MessageSource errorMessageSource(
      @Value("${spring.messages.error.base-name}") final String baseName,
      @Value("${spring.messages.encoding}") final String encoding) {
    final ReloadableResourceBundleMessageSource messageSource = baseMessageSource();
    messageSource.setBasenames(baseName);
    messageSource.setDefaultEncoding(encoding);
    return messageSource;
  }

  /**
   * Configures and provides a {@link MessageSource} bean for response messages.
   *
   * <p>This method creates a {@link ReloadableResourceBundleMessageSource} bean, configured to handle
   * response messages in the application. It uses the base settings from {@link #baseMessageSource()}
   * and applies the message base name and encoding specified by the provided configuration properties.</p>
   *
   * @param baseName the base name of the response message resource bundle, typically defined in the application's configuration.
   * @param encoding the encoding to be used for reading the response message files.
   * @return a configured {@link ReloadableResourceBundleMessageSource} bean for response messages.
   *
   * @see <a href="https://velog.io/@wjddn3711/Spring-%EA%B5%AD%EC%A0%9C%ED%99%94-Json-%ED%95%84%ED%84%B0%EB%A7%81-HATEOS-HAL-RestAPI-%EB%B2%84%EC%A0%84%EA%B4%80%EB%A6%AC">
   *   Spring - Internationalization, Json filtering, HATEOS, HAL, RestAPI versioning</a>
   */
  @Bean("response-message-source")
  public MessageSource responseMessageSource(
      @Value("${spring.messages.response.base-name}") final String baseName,
      @Value("${spring.messages.encoding}") final String encoding) {
    final ReloadableResourceBundleMessageSource messageSource = baseMessageSource();
    messageSource.setBasenames(baseName);
    messageSource.setDefaultEncoding(encoding);
    return messageSource;
  }

  /**
   * Provides a {@link MessageSourceAccessor} bean for convenient access to messages.
   *
   * <p>This method creates a {@link MessageSourceAccessor} using the provided {@link MessageSource} bean,
   * allowing for easy retrieval of messages in a type-safe manner throughout the application.</p>
   *
   * @param messageSource the {@link MessageSource} bean to be used for message resolution.
   * @return a configured {@link MessageSourceAccessor} bean.
   *
   * @see <a href="https://louis-devlog.tistory.com/48">
   *   Providing multilingual (international) services by introducing i18n</a>
   */
  @Bean
  public MessageSourceAccessor messageSourceAccessor(final MessageSource messageSource) {
    return new MessageSourceAccessor(messageSource);
  }

  /**
   * Configures and provides a {@link CookieLocaleResolver} bean to manage locale settings via cookies.
   *
   * <p>This method creates a CookieLocaleResolver that resolves the locale based on a cookie named "locale".
   * The default locale is set to {@link Locale#US}. The cookie is configured to be secure, HTTP-only, and available
   * across the entire application (cookie path "/").</p>
   *
   * @return a configured {@link CookieLocaleResolver} bean for handling locale settings via cookies.
   */
  @Bean
  public CookieLocaleResolver cookieLocaleResolver() {
    final CookieLocaleResolver cookieLocaleResolver = new CookieLocaleResolver("locale");
    cookieLocaleResolver.setDefaultLocale(Locale.US);
    cookieLocaleResolver.setCookiePath("/");
    cookieLocaleResolver.setCookieSecure(true);
    cookieLocaleResolver.setCookieHttpOnly(true);
    return cookieLocaleResolver;
  }

  /**
   * Configures and provides a {@link LocaleChangeInterceptor} bean to support locale changes via request parameters.
   *
   * <p>This method creates a LocaleChangeInterceptor that intercepts HTTP requests and allows the locale
   * to be changed by specifying a parameter. The parameter name for changing the locale is set to "lang".</p>
   *
   * @return a configured {@link LocaleChangeInterceptor} bean for handling locale changes.
   */
  @Bean
  public LocaleChangeInterceptor localeChangeInterceptor() {
    final LocaleChangeInterceptor localeChangeInterceptor = new LocaleChangeInterceptor();
    localeChangeInterceptor.setParamName("lang");
    return localeChangeInterceptor;
  }

}
