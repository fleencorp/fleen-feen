package com.fleencorp.feen.exception;

import com.fleencorp.feen.exception.base.BasicException;
import lombok.AllArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.fleencorp.base.constant.base.ExceptionConstant.INVALID_REQUEST_BODY;
import static com.google.common.base.CaseFormat.LOWER_CAMEL;
import static com.google.common.base.CaseFormat.LOWER_UNDERSCORE;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestControllerAdvice
@AllArgsConstructor
public class RestExceptionHandler {

  private final MessageSource messageSource;
  private static final String ERROR_TYPE_KEY = "ERROR_TYPE";
  private static final String DATA_FIELD_NAME = "field";
  private static final String ERRORS_PROPERTY_NAME = "errors";
  private static final String FIELDS_PROPERTY_NAME = "fields";
  private static final String DATA_VALIDATION_ERROR_TYPE = "DATA_VALIDATION";

  @ExceptionHandler(value = {BasicException.class})
  @ResponseStatus(value = NOT_FOUND)
  public Object handleBasicException(final BasicException e) {
   final String message = messageSource.getMessage(e.getMessageKey(), e.getMessageArgs(), LocaleContextHolder.getLocale());
   return buildErrorMap(message, NOT_FOUND);
  }

  @ResponseStatus(BAD_REQUEST)
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public Object handleDataValidationError(final MethodArgumentNotValidException ex) {
    // List to store field-specific validation errors
    final List<Map<String, Object>> fieldErrors = new ArrayList<>();

    // Extracting field errors from the binding result
    ex.getBindingResult().getFieldErrors().forEach(fieldError -> {
      final Map<String, Object> fieldErrorDetails = new HashMap<>();

      // Extracting field name and converting it to snake_case
      final String fieldName = LOWER_CAMEL.to(LOWER_UNDERSCORE, fieldError.getField());

      // Extracting validation error messages
      final List<String> errors = ex.getBindingResult().getFieldErrors(fieldError.getField())
        .stream()
        .map(DefaultMessageSourceResolvable::getDefaultMessage)
        .collect(Collectors.toList());

      // Adding field-specific details to the list
      fieldErrorDetails.put(DATA_FIELD_NAME, fieldName);
      fieldErrorDetails.put(ERRORS_PROPERTY_NAME, errors);
      fieldErrors.add(fieldErrorDetails);
    });

    // Building the overall error response map
    final Map<String, Object> errorResponse = new HashMap<>(buildErrorMap(INVALID_REQUEST_BODY, BAD_REQUEST));
    errorResponse.put(FIELDS_PROPERTY_NAME, fieldErrors);
    errorResponse.put(ERROR_TYPE_KEY, DATA_VALIDATION_ERROR_TYPE);

    return errorResponse;
  }

  private Map<String, Object> buildErrorMap(final String message, final HttpStatus status) {
    final Map<String, Object> error = new HashMap<>();
    error.put("message", message);
    error.put("status", status.value());
    error.put("timestamp", LocalDateTime.now().toString());
    return error;
  }
}
