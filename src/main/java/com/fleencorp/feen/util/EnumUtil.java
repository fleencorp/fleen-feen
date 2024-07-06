package com.fleencorp.feen.util;

import com.fleencorp.feen.model.view.base.EnumView;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.nonNull;

@Slf4j
public enum EnumUtil {
    ;

    private static final char ENUM_VALUE_SEPARATOR = '_';
  private static final char ENUM_VALUE_REPLACE = ' ';

  /**
   * <p>Retrieves the ordinal values of an Enum class.</p>
   * <br/>
   *
   * <p>This method retrieves and returns the ordinal values of the specified Enum class. It identifies the 'values' method
   * within the Enum class using reflection, retrieves all enum instances, and obtains their respective ordinal values. It
   * returns a list of Long values representing the ordinals of the Enum constants.</p>
   *
   * @param enumClass The Class representing the Enum type for which ordinal values are retrieved.
   * @return A List of Long values representing the ordinals of the Enum constants, or null if retrieval fails.
   */
  public static List<Long> getValues(final Class<?> enumClass) {
    try {
      final List<Long> values = new ArrayList<>();
      final Method valuesMethod = enumClass.getMethod("values");
      final Object[] allEnums = (Object[]) valuesMethod.invoke(null);

      for (final Object enumValue : allEnums) {
        final Method ordinalMethod = enumClass.getMethod("ordinal");
        final Long ordinalValue = ((Integer) ordinalMethod.invoke(enumValue)).longValue();
        values.add(ordinalValue);
      }
      return values;
    } catch (final NoSuchMethodException | IllegalAccessException | InvocationTargetException ex) {
        EnumUtil.log.error(ex.getMessage(), ex);
    }
    return null;
  }

  /**
   * <p>Retrieves an enum constant of a specified type based on its ordinal value.</p>
   * <br/>
   *
   * <p>This method fetches and returns an enum constant of a given Enum type based on its ordinal value. It retrieves all
   * enum constants of the specified type, then checks and retrieves the enum constant at the provided ordinal position.</p>
   *
   * @param enumType      The Enum type from which to retrieve the constant.
   * @param ordinalValue  The ordinal value representing the position of the enum constant to retrieve.
   * @param <T>           The Enum type.
   * @return The enum constant based on the specified ordinal value.
   * @throws IllegalArgumentException If the provided ordinal value is invalid for the Enum.
   */

  public static <T extends Enum<T>> T getEnumConstant(final Class<T> enumType, final int ordinalValue) {
    final T[] allEnums = enumType.getEnumConstants();
    if ((0 <= ordinalValue) && (ordinalValue < allEnums.length)) {
      return allEnums[ordinalValue];
    }
    throw new IllegalArgumentException("Invalid ordinal value for the Enum.");
  }

  /**
   * Converts an enum type to a list of EnumView objects.
   *
   * <p> This method converts the specified enum class to a list of EnumView objects, where each EnumView
   * represents an enum constant with its label and name. It iterates through all enum constants,
   * replaces the ENUM_VALUE_SEPARATOR character in the enum constant label with ENUM_VALUE_REPLACE,
   * and adds the converted EnumView objects to the resulting list.</p>
   *
   * <p> The input enumClass parameter should be a valid enum class. If it is not an enum type,
   * an empty list will be returned.</p>
   *
   * @param enumClass The enum class to be converted to a list of EnumView objects.
   * @return A list of EnumView objects representing the enum constants.
   */
  public static List<? extends EnumView> convertEnumToList(final Class<? extends Enum<?>> enumClass) {
    final List<EnumView> views = new ArrayList<>();

    if (enumClass.isEnum()) {
      final Enum<?>[] constants = enumClass.getEnumConstants();

      for (final Enum<?> enumConst : constants) {
        final EnumView enumView = new EnumView();
        enumView.setLabel(enumConst.toString()
          .replaceAll(String.valueOf(EnumUtil.ENUM_VALUE_SEPARATOR), String.valueOf(EnumUtil.ENUM_VALUE_REPLACE)));
        enumView.setName(enumConst.name());
        views.add(enumView);
      }
    }

    return views;
  }

  /**
   * Parses a string value into an Enum of a specified type or returns null if parsing fails or if inputs are invalid.
   *
   * @param value    A string value to be parsed into the Enum.
   * @param enumType The Class object of the target Enum type.
   * @param <T>      The Enum type to parse.
   * @return An Enum value of type T if parsing succeeds, or null if parsing fails or inputs are invalid.
   */
  public static <T extends Enum<T>> T parseEnumOrNull(final String value, final Class<T> enumType) {
    if ((null == value) || (null == enumType) || !enumType.isEnum()) {
      return null;
    }

    try {
      return Enum.valueOf(enumType, value);
    } catch (final IllegalArgumentException e) {
      return null;
    }
  }

  /**
   * Checks if all values in a list match any of the entries in a given enum.
   *
   * @param values   The list of values to be checked.
   * @param enumClass The class object representing the enum type.
   * @param <T>       The type of the enum.
   * @return          {@code true} if all values match enum values, {@code false} otherwise.
   */
  public static <T extends Enum<T>> boolean matchEnumValues(final List<String> values, final Class<T> enumClass) {
    if (nonNull(values) && !values.isEmpty()) {
      // Iterate through each string in the list
      for (final String value : values) {
        boolean matchFound = false;
        // Iterate through each enum value
        // Check if the current string matches the current enum value
        for (final T enumValue : enumClass.getEnumConstants())
            if (value.equals(enumValue.toString())) {
                matchFound = true;
                break;
            }
        // If no match is found for the current string, return false
        if (!matchFound) {
          return false;
        }
      }
      // If all strings have a matching enum value, return true
      return true;
    }
    return false;
  }


}
