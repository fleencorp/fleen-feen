package com.fleencorp.feen.converter.impl;

import com.fleencorp.feen.util.security.EncryptionUtils;
import jakarta.persistence.AttributeConverter;
import org.springframework.stereotype.Component;

import static java.util.Objects.nonNull;
/**
 * Attribute converter for encrypting and decrypting strings using EncryptionUtils.
 *
 * <p> This converter is used to encrypt and decrypt string attributes when storing them in the database.
 * It implements {@link AttributeConverter} interface to provide methods for converting strings to encrypted form
 * (for storage) and from encrypted form back to plain text (for retrieval).
 * </p>
 */
@Component
public class StringCryptoConverter implements AttributeConverter<String, String> {

  private final EncryptionUtils encryptionUtils;


  /**
   * Constructor to initialize the converter with an instance of {@link EncryptionUtils}.
   *
   * @param encryptionUtils The utility class used for encryption and decryption.
   */
  public StringCryptoConverter(EncryptionUtils encryptionUtils) {
    this.encryptionUtils = encryptionUtils;
  }

  /**
   * Converts the string attribute into its encrypted form for storage.
   *
   * @param attribute The string attribute to be encrypted.
   * @return The encrypted string.
   */
  @Override
  public String convertToDatabaseColumn(String attribute) {
    if (nonNull(attribute)) {
      return encryptionUtils.encrypt(attribute);
    }
    return null;
  }

  /**
   * Converts the encrypted string attribute back into its decrypted form for entity attribute.
   *
   * @param attribute The encrypted string from the database.
   * @return The decrypted string.
   */
  @Override
  public String convertToEntityAttribute(String attribute) {
    if (nonNull(attribute)) {
      return encryptionUtils.decrypt(attribute);
    }
    return null;
  }
}
