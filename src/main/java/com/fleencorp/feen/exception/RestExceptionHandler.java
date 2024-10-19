package com.fleencorp.feen.exception;

import com.fleencorp.base.model.response.error.ErrorResponse;
import com.fleencorp.feen.constant.http.FleenHttpStatus;
import com.fleencorp.feen.exception.auth.AlreadySignedUpException;
import com.fleencorp.feen.exception.base.BasicException;
import com.fleencorp.feen.service.i18n.LocalizedResponse;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.fleencorp.base.constant.base.ExceptionMessages.invalidRequestBody;
import static com.fleencorp.feen.constant.http.FleenHttpStatus.badRequest;
import static com.google.common.base.CaseFormat.LOWER_CAMEL;
import static com.google.common.base.CaseFormat.LOWER_UNDERSCORE;
import static java.util.Objects.nonNull;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestControllerAdvice
public class RestExceptionHandler {

  private final LocalizedResponse localizedResponse;
  private static final String DATA_FIELD_NAME = "field";
  private static final String ERRORS_PROPERTY_NAME = "errors";

  public RestExceptionHandler(final LocalizedResponse localizedResponse) {
    this.localizedResponse = localizedResponse;
  }

  @ExceptionHandler(value = {BasicException.class})
  @ResponseStatus(value = NOT_FOUND)
  public Object handleBasicException(final BasicException e) {
   return localizedResponse.withStatus(e, FleenHttpStatus.notFound());
  }

  @ExceptionHandler(value = {AlreadySignedUpException.class})
  @ResponseStatus(value = BAD_REQUEST)
  public ErrorResponse handleBadRequest(final AlreadySignedUpException e) {
    return localizedResponse.withStatus(e, FleenHttpStatus.badRequest());
  }

  /**
   * Handles {@link MethodArgumentNotValidException} exceptions and constructs an error response for validation errors.
   *
   * <p>This method is invoked when a {@link MethodArgumentNotValidException} is thrown, typically due to validation
   * errors on the request body. It processes the field-specific errors, converts field names to snake_case, collects
   * validation error messages, and creates an {@link ErrorResponse} containing these details.</p>
   *
   * @param ex  the {@link MethodArgumentNotValidException} that contains validation errors.
   * @return an {@link ErrorResponse} object containing details of the validation errors, including field names and
   *         associated error messages.
   */
  @ResponseStatus(BAD_REQUEST)
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public Object handleDataValidationError(final MethodArgumentNotValidException ex) {
    // List to store field-specific validation errors
    final List<Map<String, Object>> fieldErrors = new ArrayList<>();

    // Extracting field errors from the binding result
    ex.getBindingResult()
      .getFieldErrors()
      .forEach(fieldError -> {
      final Map<String, Object> fieldErrorDetails = new HashMap<>();

      // Extracting field name and converting it to snake_case
      final String fieldName = toSnakeCase(fieldError.getField());
      // Extracting validation error messages
      final List<String> errors = getErrStrings(ex, fieldError);

      // Adding field-specific details to the list
      setFieldErrorDetails(fieldName, fieldErrorDetails, errors);
      fieldErrors.add(fieldErrorDetails);
    });

    return ErrorResponse.of(invalidRequestBody(), badRequest(), fieldErrors);
  }

  /**
   * Sets the details of field errors in the provided map.
   *
   * <p>This method populates the given map with field error details, including the field name and a list of error messages.
   * It adds the field name under the key {@code DATA_FIELD_NAME} and the list of error messages under the key {@code ERRORS_PROPERTY_NAME}.</p>
   *
   * @param fieldName        the name of the field associated with the errors.
   * @param fieldErrorDetails the map to which the field error details will be added.
   * @param errors           the list of error messages related to the field.
   */
  protected void setFieldErrorDetails(final String fieldName, final Map<String, Object> fieldErrorDetails, final List<String> errors) {
    fieldErrorDetails.put(DATA_FIELD_NAME, fieldName);
    fieldErrorDetails.put(ERRORS_PROPERTY_NAME, errors);
  }

  /**
   * Retrieves a list of error messages for a specific field from the provided exception.
   *
   * <p>This method extracts error messages associated with a particular field from a
   * {@link MethodArgumentNotValidException}. It collects the default messages for the given
   * field errors into a list and returns it.</p>
   *
   * @param ex         the {@link MethodArgumentNotValidException} containing the binding result.
   * @param fieldError the {@link FieldError} specifying the field for which error messages are to be retrieved.
   * @return a list of error messages associated with the specified field, or {@code null} if the exception is {@code null}.
   */
  protected static List<String> getErrStrings(final MethodArgumentNotValidException ex, final FieldError fieldError) {
    if (nonNull(ex)) {
      return ex.getBindingResult().getFieldErrors(fieldError.getField())
        .stream()
        .map(DefaultMessageSourceResolvable::getDefaultMessage)
        .collect(Collectors.toList());
    }
    return new ArrayList<>();
  }

  /**
   * Converts the given string from lower camel case to snake case.
   *
   * <p>This method converts the input string from lower camel case format
   * (e.g., "exampleString") to snake case format (e.g., "example_string").</p>
   *
   * @param input  the input string in lower camel case format.
   * @return the converted string in snake case format.
   */
  public static String toSnakeCase(final String input) {
    if (nonNull(input)) {
      return LOWER_CAMEL.to(LOWER_UNDERSCORE, input);
    }
    return null;
  }

}
