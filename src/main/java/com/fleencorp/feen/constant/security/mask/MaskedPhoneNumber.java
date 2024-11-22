package com.fleencorp.feen.constant.security.mask;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fleencorp.base.constant.base.ApiParameter;
import com.fleencorp.base.serializer.ToStringEnumSerializer;
import lombok.Getter;
import lombok.Setter;

/**
 * Enum representing a masked phone number, used as an API parameter.
 *
 * <p>This enum provides a way to represent and handle phone numbers in a masked format.
 * It includes a single instance for phone numbers and supports conversion to a masked
 * phone number format.</p>
 *
 * <p>The {@link ToStringEnumSerializer} is used to serialize this enum's values as strings.</p>
 */
@Getter
@JsonSerialize(using = ToStringEnumSerializer.class)
public enum MaskedPhoneNumber implements ApiParameter {

  /**
   * Instance representing a phone number.
   */
  PHONE("Phone", "");

  @Setter
  private String value;
  @Setter
  private String rawValue;

  MaskedPhoneNumber(
      final String value,
      final String rawValue) {
    this.value = value;
    this.rawValue = rawValue;
  }

  /**
   * Returns the string representation of the enum value.
   *
   * <p>This method returns the value associated with the enum instance.</p>
   *
   * @return the string representation of the enum value.
   */
  @Override
  public String toString() {
    return value;
  }

  /**
   * Creates a {@link MaskedPhoneNumber} instance with a masked phone number.
   *
   * <p>This method generates a masked phone number using the provided value and
   * returns a {@link MaskedPhoneNumber} instance with the masked phone number.</p>
   *
   * @param value the phone number to mask.
   * @return a {@link MaskedPhoneNumber} instance with the masked phone number.
   * @throws IllegalArgumentException if the {@code value} is {@code null} or has fewer than 2 characters.
   */
  public static MaskedPhoneNumber of(final String value) {
    final MaskedPhoneNumber phoneNumber = MaskedPhoneNumber.PHONE;
    phoneNumber.setValue(maskPhoneNumber(value));
    phoneNumber.setRawValue(value);
    return phoneNumber;
  }

  /**
   * Masks a phone number, obscuring all but the last two characters.
   *
   * <p>This method masks the phone number by replacing all characters except for the
   * last two with asterisks.</p>
   *
   * @param phoneNumber the phone number to mask. It must not be {@code null} and must have at least 2 characters.
   * @return the masked phone number with all but the last two characters obscured.
   * @throws IllegalArgumentException if the {@code phoneNumber} is {@code null} or has fewer than 2 characters.
   */
  public static String maskPhoneNumber(final String phoneNumber) {
    if (phoneNumber == null || phoneNumber.length() < 2) {
      throw new IllegalArgumentException("Invalid phone number");
    }

    // Mask all but the last two characters
    return "***" + phoneNumber.substring(phoneNumber.length() - 2);
  }
}

