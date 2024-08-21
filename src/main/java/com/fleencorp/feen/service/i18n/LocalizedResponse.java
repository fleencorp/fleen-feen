package com.fleencorp.feen.service.i18n;

import com.fleencorp.feen.model.response.base.ApiResponse;
import com.fleencorp.feen.model.response.base.FleenFeenResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.util.Locale;

import static java.util.Objects.nonNull;

/**
 * A component responsible for providing localized responses using a {@link MessageSource}.
 *
 * <p>This class uses a {@link MessageSource} bean to handle message localization.
 * The {@link MessageSource} used is specifically identified by the {@code response-message-source} qualifier.</p>
 *
 * @author Yusuf Alamu Musa
 * @version 1.0
 */
@Component
public class LocalizedResponse {

  private final MessageSource messageSource;

  /**
   * Constructs a {@link LocalizedResponse} with the specified {@link MessageSource}.
   *
   * @param messageSource the {@link MessageSource} used for retrieving localized messages.
   */
  public LocalizedResponse(
      @Qualifier("response-message-source") final MessageSource messageSource) {
    this.messageSource = messageSource;
  }

  /**
   * Retrieves a message from the message source based on the provided key and locale.
   *
   * <p>This method delegates the message retrieval to the {@link MessageSource} using the specified key, locale,
   * and optional parameters for message formatting. It returns the resolved message as a {@link String}.</p>
   *
   * @param key the message key to look up in the message source.
   * @param locale the locale to use for message resolution.
   * @param params optional parameters to format the message.
   * @return the resolved message as a {@link String}.
   */
  public String getMessage(final String key, final Locale locale, final Object... params) {
    return messageSource.getMessage(key, params, locale);
  }

  /**
   * Retrieves a message from the message source based on the provided key and the current locale.
   *
   * <p>This method retrieves a message using the specified key and the current locale obtained from
   * {@link LocaleContextHolder}. Optional parameters can be provided for message formatting. The resolved message
   * is returned as a {@link String}.</p>
   *
   * @param key the message key to look up in the message source.
   * @param params optional parameters to format the message.
   * @return the resolved message as a {@link String}.
   */
  public String getMessage(final String key, final Object... params) {
    return getMessage(key, LocaleContextHolder.getLocale(), params);
  }

  /**
   * Updates the message in the provided {@link FleenFeenResponse} with the localized message corresponding to its message key.
   *
   * <p>This method retrieves the localized message for the given {@link FleenFeenResponse} based on its message key and
   * updates the response's message field with this localized message. If the response is {@code null}, the method
   * returns {@code null}.</p>
   *
   * @param response the {@link FleenFeenResponse} object to be updated with the localized message.
   * @param <T> the type of the data contained in the {@link FleenFeenResponse}.
   * @return the updated {@link FleenFeenResponse} with the localized message, or {@code null} if the input response was {@code null}.
   */
  public <T extends ApiResponse> T of(final T response) {
    if (nonNull(response) && nonNull(response.getMessageKey())) {
      final String message = getMessage(response.getMessageKey());
      response.setMessage(message);
      return response;
    }
    return null;
  }
}
