package com.fleencorp.feen.util;

import java.util.Collection;
import java.util.function.Supplier;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

/**
 * Utility class for exception handling and validation.
 *
 * <p>This class provides static methods to perform checks on objects and collections,
 * ensuring they are not null, and throws custom exceptions if they are.</p>
 *
 * @author Yusuf Alamu Musa
 * @version 1.0
 */
public class ExceptionUtil {

  /**
   * Checks if the given value is null and throws the provided exception if it is.
   *
   * @param <T>       the type of the value to check
   * @param value     the value to check for null
   * @param exception the supplier that provides the exception to throw if the value is null
   * @throws RuntimeException if the value is null and the exception supplier is non-null
   */
  public static <T> void checkIsNull(final T value, final Supplier<? extends RuntimeException> exception) {
    // Check if the exception supplier is non-null and the value is null
    if (nonNull(exception) && isNull(value)) {
      // Throw the exception provided by the supplier
      throw exception.get();
    }
  }

  /**
   * Checks if the given collection or any of its elements is null and throws the provided exception if any are null.
   *
   * @param collection the collection to check for null elements
   * @param exception  the supplier that provides the exception to throw if any element is null
   * @throws RuntimeException if the collection or any element in the collection is null and the exception supplier is non-null
   */
  public static void checkIsNullAny(final Collection<?> collection, final Supplier<? extends RuntimeException> exception) {
    // Return early if the exception supplier is null
    if (isNull(exception)) {
      return;
    }

    // Check if the collection itself is null and throw exception if so
    checkIsNull(collection, exception);
    // Iterate through each element in the collection
    for (final Object value: collection) {
      // Check if the element is null and throw exception if so
      checkIsNull(value, exception);
    }
  }

  /**
   * Throws a runtime exception supplied by the given {@link Supplier} if the specified condition is true.
   *
   * @param isTrue the condition to evaluate
   * @param exceptionSupplier the supplier that provides the exception to be thrown if the condition is true
   * @throws RuntimeException if {@code isTrue} is {@code true}, the exception provided by {@code exceptionSupplier} is thrown
   */
  public static void checkIsTrue(final boolean isTrue, final Supplier<? extends RuntimeException> exceptionSupplier) {
    if (isTrue) {
      throw exceptionSupplier.get();
    }
  }
}
