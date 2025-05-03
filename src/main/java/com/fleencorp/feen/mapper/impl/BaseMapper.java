package com.fleencorp.feen.mapper.impl;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class BaseMapper {

  private final MessageSource messageSource;

  public BaseMapper(final MessageSource messageSource) {
    this.messageSource = messageSource;
  }

  /**
   * Translates the provided message code into a localized message based on the current locale.
   *
   * <p>This method retrieves the current locale from the {@link LocaleContextHolder}, and then uses the
   * {@link MessageSource} to resolve the message corresponding to the provided {@code messageCode}.
   * The method returns the translated message string for the current locale. If the message code cannot be found,
   * the method may return the default message or throw an exception based on the configuration of the {@link MessageSource}.</p>
   *
   * @param messageCode The code representing the message to be translated.
   * @return The localized message corresponding to the {@code messageCode} for the current locale.
   */
  protected String translate(final String messageCode) {
    final Locale locale = LocaleContextHolder.getLocale();
    return messageSource.getMessage(messageCode, null, locale);
  }

  protected String translate(final String messageCode, final Object...args) {
    final Locale locale = LocaleContextHolder.getLocale();
    return messageSource.getMessage(messageCode, args, locale);
  }
}
