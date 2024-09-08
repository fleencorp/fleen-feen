package com.fleencorp.feen.service.i18n;

import com.fleencorp.base.exception.FleenException;
import com.fleencorp.base.model.response.base.ApiResponse;
import com.fleencorp.base.model.response.error.ErrorResponse;
import com.fleencorp.feen.model.response.base.FleenFeenResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
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
  private final MessageSource exMessageSource;

  /**
   * Constructs a {@link LocalizedResponse} with the specified {@link MessageSource}.
   *
   * @param messageSource the {@link MessageSource} used for retrieving localized messages.
   */
  public LocalizedResponse(
      @Qualifier("response-message-source") final MessageSource messageSource,
      @Qualifier("error-message-source") final MessageSource exMessageSource) {
    this.messageSource = messageSource;
    this.exMessageSource = exMessageSource;
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
  public String getMessageEx(final String key, final Locale locale, final Object... params) {
    return exMessageSource.getMessage(key, params, locale);
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
  public String getMessageEx(final String key, final Object... params) {
    return getMessageEx(key, LocaleContextHolder.getLocale(), params);
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
    if (nonNull(response) && nonNull(response.getMessageCode())) {
      final String message = getMessage(response.getMessageCode());
      response.setMessage(message);
      return response;
    }
    return null;
  }

  /**
   * Populates the message of the given exception using the provided parameters.
   *
   * <p>If the exception and its message key are not null, this method retrieves a localized message
   * using the exception's message key and the provided parameters, sets this message on the exception,
   * and then returns the modified exception.</p>
   *
   * @param <T>     the type of the exception, which must extend {@link FleenException}.
   * @param ex      the exception to be populated with a localized message.
   * @return the modified exception with the localized message set, or {@code null} if the exception
   *         or its message key is {@code null}.
   */
  public <T extends FleenException> T of(final T ex) {
    if (nonNull(ex) && nonNull(ex.getMessageCode())) {
      final String message = getMessageEx(ex.getMessageCode(), ex.getParams());
      ex.setMessage(message);
      return ex;
    }
    return null;
  }

  /**
   * Retrieves the localized message for the given exception.
   *
   * <p>If the exception and its message key are not null, this method returns a localized message
   * based on the exception's message key using the default locale.</p>
   *
   * @param <T>  the type of the exception, which must extend {@link FleenException}.
   * @param ex   the exception for which to retrieve the localized message.
   * @return the localized message corresponding to the exception's message key, or {@code null} if
   *         the exception or its message key is {@code null}.
   */
  public <T extends FleenException> String getExMessage(final T ex) {
    if (nonNull(ex) && nonNull(ex.getMessageCode())) {
      return getMessageEx(ex.getMessageCode(), Locale.getDefault());
    }
    return null;
  }

  /**
   * Associates the given HTTP status with the provided exception and generates an error response.
   *
   * <p>If the exception and its message code are not null, this method retrieves a localized message
   * using the exception's message code and its parameters, and creates an {@link ErrorResponse}
   * with the retrieved message and the specified HTTP status. The exception itself is returned
   * without modification.</p>
   *
   * @param <T>     the type of the exception, which must extend {@link FleenException}.
   * @param ex      the exception to be associated with the provided HTTP status.
   * @param status  the HTTP status to be associated with the error response.
   * @return the provided exception, or {@code null} if the exception or its message code is {@code null}.
   */
  public <T extends FleenException> ErrorResponse withStatus(final T ex, final HttpStatus status) {
    if (nonNull(ex) && nonNull(ex.getMessageCode())) {
      final String message = getMessageEx(ex.getMessageCode(), ex.getParams());
      return ErrorResponse.of(message, status);
    }
    return ErrorResponse.of();
  }
}
