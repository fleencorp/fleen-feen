package com.fleencorp.feen.configuration.message;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;

import java.util.Locale;

import static com.fleencorp.feen.constant.base.SimpleConstant.COMMA;

@Configuration
public class MessageSourceConfiguration {

  /**
   *
   * @param baseNames
   * @param encoding
   * @return
   *
   * @see <a href="https://velog.io/@ksk7584/%EB%A9%94%EC%8B%9C%EC%A7%80-%EA%B5%AD%EC%A0%9C%ED%99%94">
   *   Messages, Internationalization</a>
   * @see <a href="https://velog.io/@mini-boo/DB%EB%A5%BC-%ED%86%B5%ED%95%9C-%EB%8B%A4%EA%B5%AD%EC%96%B4-%EC%A7%80%EC%9B%90-%EA%B8%B0%EB%8A%A5">
   *   Implementation of multilingual support through DB</a>
   */
  @Bean
  public MessageSource messageSource(
      @Value("${spring.messages.base-names}") final String baseNames,
      @Value("${spring.messages.encoding}") final String encoding) {
    final ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
    messageSource.setBasenames(baseNames.split(COMMA));
    messageSource.setCacheSeconds(60);
    messageSource.setDefaultLocale(Locale.US);
    messageSource.setDefaultEncoding(encoding);
    messageSource.setUseCodeAsDefaultMessage(false);
    messageSource.setFallbackToSystemLocale(false);
    messageSource.setAlwaysUseMessageFormat(true);
    return messageSource;
  }

  /**
   *
   * @param messageSource
   * @return
   *
   * @see <a href-"https://velog.io/@maketheworldwise/%EB%8B%A4%EA%B5%AD%EC%96%B4-%EC%B2%98%EB%A6%AC%EC%9D%98-%EB%AA%A8%EB%93%A0-%EA%B2%83">
   *   </a>
   * @see <a href="https://velog.io/@wjddn3711/Spring-%EA%B5%AD%EC%A0%9C%ED%99%94-Json-%ED%95%84%ED%84%B0%EB%A7%81-HATEOS-HAL-RestAPI-%EB%B2%84%EC%A0%84%EA%B4%80%EB%A6%AC">
   *   Spring - Internationalization, Json filtering, HATEOS, HAL, RestAPI versioning</a>
   * @see <a href="https://louis-devlog.tistory.com/48">
   *   Providing multilingual (international) services by introducing i18n</a>
   */
  @Bean
  public MessageSourceAccessor messageSourceAccessor(final MessageSource messageSource) {
    return new MessageSourceAccessor(messageSource);
  }

  @Bean
  public CookieLocaleResolver cookieLocaleResolver() {
    final CookieLocaleResolver cookieLocaleResolver = new CookieLocaleResolver();
    cookieLocaleResolver.setDefaultLocale(Locale.US);
    cookieLocaleResolver.setCookieName("locale");
    cookieLocaleResolver.setCookieMaxAge(10_800);
    cookieLocaleResolver.setCookiePath("/");
    cookieLocaleResolver.setCookieSecure(true);
    cookieLocaleResolver.setCookieHttpOnly(true);
    return cookieLocaleResolver;
  }

  @Bean
  public LocaleChangeInterceptor localeChangeInterceptor() {
    final LocaleChangeInterceptor localeChangeInterceptor = new LocaleChangeInterceptor();
    localeChangeInterceptor.setParamName("lang");
    return localeChangeInterceptor;
  }

}
