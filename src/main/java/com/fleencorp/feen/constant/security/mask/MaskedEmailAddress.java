package com.fleencorp.feen.constant.security.mask;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fleencorp.base.constant.base.ApiParameter;
import com.fleencorp.base.serializer.ToStringEnumSerializer;
import lombok.Getter;
import lombok.Setter;

/**
 * Enum representing a masked email address, used as an API parameter.
 *
 * <p>This enum provides a way to represent and handle email addresses in a masked format.
 * It includes a single instance for email addresses and supports conversion to a masked
 * email format.</p>
 *
 * <p>The {@link ToStringEnumSerializer} is used to serialize this enum's values as strings.</p>
 *
 * @author Yusuf Alamu Musa
 * @version 1.0
 */
@Getter
@JsonSerialize(using = ToStringEnumSerializer.class)
public enum MaskedEmailAddress implements ApiParameter {

  /**
   * Instance representing an email address.
   */
  EMAIL("Email");

  @Setter
  private String value;

  MaskedEmailAddress(final String value) {
    this.value = value;
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
   * Creates a {@link MaskedEmailAddress} instance with a masked email address.
   *
   * <p>This method generates a masked email address using the provided value and
   * returns a {@link MaskedEmailAddress} instance with the masked email.</p>
   *
   * @param value the email address to mask.
   * @return a {@link MaskedEmailAddress} instance with the masked email address.
   */
  public static MaskedEmailAddress of(final String value) {
    final MaskedEmailAddress emailAddress = MaskedEmailAddress.EMAIL;
    emailAddress.setValue(maskEmail(value));
    return emailAddress;
  }

  /**
   * Masks an email address, obscuring parts of it while keeping certain segments visible.
   *
   * <p>This method masks the local part of the email address except for the first two characters.
   * It also masks the domain part, showing only the top-level domain. If the email address
   * is invalid or doesn't contain an '@' character, an {@link IllegalArgumentException} is thrown.</p>
   *
   * @param email the email address to be masked. It must not be {@code null} and must contain an '@' character.
   * @return the masked email address with the local part and domain appropriately obscured.
   * @throws IllegalArgumentException if the {@code email} is {@code null} or does not contain an '@' character.
   */
  public static String maskEmail(final String email) {
    final String _at = "@";
    if (email == null || !email.contains(_at)) {
      throw new IllegalArgumentException("Invalid email address");
    }

    // Split the email into local part and domain part
    final String[] parts = email.split(_at);
    final String localPart = parts[0];
    final String domainPart = parts[1];

    // Mask the local part except for the first two characters
    final String maskedLocalPart = localPart.length() > 2 ? localPart.substring(0, 2) + "***" : localPart;

    // Find the last dot in the domain part to determine the top-level domain
    final int lastDotIndex = domainPart.lastIndexOf('.');
    if (lastDotIndex <= 0 || lastDotIndex == domainPart.length() - 1) {
      // If the domain part doesn't have a valid top-level domain, return the masked local part with full domain
      return maskedLocalPart + _at + domainPart;
    }

    // Mask the top-level domain, leaving the last part after the dot visible
    final String maskedDomainPart = "***" + domainPart.substring(lastDotIndex);

    return maskedLocalPart + _at + maskedDomainPart;
  }
}
